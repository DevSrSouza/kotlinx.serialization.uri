package br.com.devsrsouza.kotlinx.serialization.uri

import com.google.common.net.UrlEscapers
import java.net.URI

actual class UriProvider(
    val shouldEncodeValues: Boolean,
) {
    actual fun uri(uriScheme: String, uriPath: String): Uri {
        val escapedUrl = UrlEscapers.urlFragmentEscaper().escape("https://endpoint.domain$uriPath")
        val jvmUri = URI.create(escapedUrl)

        return Uri(uriScheme, jvmUri)
    }

    actual fun createUriPath(uriScheme: String, uriData: UriData): String {
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
