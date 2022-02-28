package br.com.devsrsouza.kotlinx.serialization.uri

import br.com.devsrsouza.kotlinx.serialization.uri.internal.UriPathDecoder
import br.com.devsrsouza.kotlinx.serialization.uri.internal.UriPathEncoder
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.StringFormat
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

// TODO: remove uriPathScheme ends with `/`
fun UriPath(
    uriPathScheme: String,
    uriProvider: UriProvider,
    serializersModule: SerializersModule = EmptySerializersModule,
): UriPathSerializer = UriSerializerImpl(
    uriPathScheme,
    uriProvider,
    serializersModule,
)

interface UriPathSerializer : StringFormat {
    /**
     * The Uri Path Scheme for serialization and deserialization.
     * Query params are not required.
     *
     * A uriPathScheme example would be: /{name}/delete
     */
    val uriPathScheme: String

    /**
     * Decoder for lists, objects that do not follow uri.
     *
     * For example you could have a query param that is a Json object,
     * for that, you would use object as well and the structureFormat
     * would take care deserializing it.
     *
     * ex: /path?param={"name": "example"}
     */
    //val structureFormat: StringFormat

    /**
     * A platform specific Uri logic provider.
     *
     * see [AndroidUri], [JvmUri], [MultiplatformUri].
     */
    val uriProvider: UriProvider
}

internal class UriSerializerImpl(
    override val uriPathScheme: String,
    override val uriProvider: UriProvider,
    override val serializersModule: SerializersModule,
) : UriPathSerializer {
    override fun <T> decodeFromString(deserializer: DeserializationStrategy<T>, string: String): T {
        return UriPathDecoder(uriProvider.uri(uriPathScheme, string)).decodeSerializableValue(deserializer)
    }

    override fun <T> encodeToString(serializer: SerializationStrategy<T>, value: T): String {
        val queryParams = mutableMapOf<String, String>()
        val pathParams = mutableMapOf<String, String>()
        UriPathEncoder(queryParams = queryParams, pathParams = pathParams)
            .encodeSerializableValue(serializer, value)

        val data = UriData(
            queryParams = queryParams,
            pathParams = pathParams,
        )

        return uriProvider.createUriPath(uriPathScheme, data)
    }
}

