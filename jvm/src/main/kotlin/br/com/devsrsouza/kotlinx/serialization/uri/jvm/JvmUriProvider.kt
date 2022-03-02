package br.com.devsrsouza.kotlinx.serialization.uri.jvm

import br.com.devsrsouza.kotlinx.serialization.uri.Uri
import br.com.devsrsouza.kotlinx.serialization.uri.UriData
import br.com.devsrsouza.kotlinx.serialization.uri.UriProvider
import com.google.common.net.UrlEscapers
import java.net.URI

class JvmUriProvider(
    val shouldEncodeValues: Boolean,
) : UriProvider {
    override fun uri(uriScheme: String, uri: String): Uri {
        val jvmUri = runCatching { URI.create("https://endpoint.domain$uri") }.getOrThrow()
        return JvmUri(uriScheme, jvmUri)
    }

    override fun createUriPath(uriScheme: String, uriData: UriData): String {
        fun Map<String, String>.encode() = if (shouldEncodeValues)
            mapValues { UrlEscapers.urlPathSegmentEscaper().escape(it.value) }
        else
            this

        val queryParams = uriData.queryParams.encode()
        val pathParams = uriData.queryParams.encode()

        val pathResult = buildPath(uriScheme, pathParams)

        val queryResult = queryParams.toList().joinToString(separator = QUERY_PARAM_SEPARATOR) { (key, value) ->
            "$key$QUERY_PARAM_EQUAL_OPERATOR$value"
        }

        return "$pathResult${if (queryParams.isNotEmpty()) "$QUERY_PARAM_INITIAL_OPERATOR$queryResult" else ""}"
    }
}
