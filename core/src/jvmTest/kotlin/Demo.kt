import br.com.devsrsouza.kotlinx.serialization.uri.Path
import br.com.devsrsouza.kotlinx.serialization.uri.Query
import br.com.devsrsouza.kotlinx.serialization.uri.URI_PARAM_SCHEME_REGEX
import br.com.devsrsouza.kotlinx.serialization.uri.Uri
import br.com.devsrsouza.kotlinx.serialization.uri.UriData
import br.com.devsrsouza.kotlinx.serialization.uri.UriPath
import br.com.devsrsouza.kotlinx.serialization.uri.UriProvider
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import java.net.URI
import java.net.URLEncoder

@Serializable
data class Demo(
    @Path val name: String,
    @Query val test: String,
    @Query val demo: String? = "Exemplo de default value",
    @Query val something: SomeObject,
    @Query val list: List<String>,
    @Query val map: Map<String, String>,
)

@Serializable
data class SomeObject(
    val example: String,
    val batata: String,
    val test2: String = "aaaaa",
    val bananas: String?
)

@Serializable
data class EncodeTest(
    @Path val name: String,
    @Query val encode: Int,
    @Query val encodeObj: EncodeObj,
    @Query val list: List<String>,
    @Query val default: String = "value",
)

@Serializable
data class EncodeObj(
    val insideJsonKey: String,
    val second: Second,
    val map: Map<String, String>,
)

@Serializable
data class Second(
    val nothing: String
)

fun main() {
    val uriPath = UriPath(
        uriPathScheme = "/home/{name}",
        uriProvider = JvmUriProvider(shouldEncodeValues = false),
    )
    val result = uriPath.decodeFromString<Demo>(
        Demo.serializer(),
        // /home/Ronaldo?test=something with space&demo=asdasda&something={"example":"test","batata":"com feijao"}&list=["one","two","three"]&map={"test":"value","test2":"second"}
        "/home/Ronaldo?test=something%20with%20space&demo=asdasda&something=%7B%22example%22%3A%22test%22%2C%22batata%22%3A%22com%20feijao%22%7D&list=%5B%22one%22%2C%22two%22%2C%22three%22%5D&map=%7B%22test%22%3A%22value%22%2C%22test2%22%3A%22second%22%7D"
    )

    println(result)

    val encodeResult = uriPath.encodeToString(
        EncodeTest(
            "Dota",
            123,
            EncodeObj(
                "somevalue",
                Second("aaaaaa"),
                mapOf("batata" to "vaue", "arroz" to "feijao")
            ),
            listOf("batinha", "123brow")
        )
    )

    println(encodeResult)
}

class JvmUriProvider(
    val shouldEncodeValues: Boolean,
) : UriProvider {
    override fun uri(uriScheme: String, uri: String): Uri {
        val jvmUri = runCatching { URI.create("https://endpoint.domain$uri") }.getOrThrow()
        return JvmUri(uriScheme, jvmUri)
    }

    override fun createUriPath(uriScheme: String, uriData: UriData): String {
        // TODO: replace with guava UrlEscapers
        fun encode(value: String) = URLEncoder.encode(value, Charsets.UTF_8).replace("+", "%20")

        val queryParams = if (shouldEncodeValues)
            uriData.queryParams.mapValues { encode(it.value) }
        else
            uriData.queryParams

        val pathParams = if (shouldEncodeValues)
            uriData.pathParams.mapValues { encode(it.value) }
        else
            uriData.pathParams

        val pathResult = URI_PARAM_SCHEME_REGEX.replace(uriScheme) {
            val paramName = it.groups.get(1)?.value ?: return@replace it.value

            pathParams.get(paramName) ?: it.value
        }

        val queryResult = queryParams.toList().joinToString(separator = QUERY_PARAM_SEPARATOR) { (key, value) ->
            "$key$QUERY_PARAM_EQUAL_OPERATOR$value"
        }

        return "$pathResult${if (queryParams.isNotEmpty()) "$QUERY_PARAM_INITIAL_OPERATOR$queryResult" else ""}"
    }
}

internal const val QUERY_PARAM_SEPARATOR = "&"
internal const val QUERY_PARAM_EQUAL_OPERATOR = "="
internal const val QUERY_PARAM_INITIAL_OPERATOR = "?"

class JvmUri(override val pathScheme: String, val uri: URI) : Uri {
    override val path: String = uri.path
    override val fullPath: String = uri.path

    private val queryParams: Map<String, String> = calculateQueryParam()

    override fun getQueryParam(key: String): String? = queryParams.get(key)

    private fun calculateQueryParam(): Map<String, String> {
        val query = uri.query
        val params = query.split(QUERY_PARAM_SEPARATOR)

        return params.associate {
            it.substringBefore(QUERY_PARAM_EQUAL_OPERATOR) to
                it.substringAfter(QUERY_PARAM_EQUAL_OPERATOR)
        }
    }
}
