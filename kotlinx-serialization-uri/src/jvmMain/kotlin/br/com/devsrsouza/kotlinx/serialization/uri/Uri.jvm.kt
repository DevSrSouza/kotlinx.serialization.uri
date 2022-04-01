package br.com.devsrsouza.kotlinx.serialization.uri

import java.net.URI

internal const val QUERY_PARAM_SEPARATOR = "&"
internal const val QUERY_PARAM_EQUAL_OPERATOR = "="
internal const val QUERY_PARAM_INITIAL_OPERATOR = "?"

actual class Uri(actual val pathScheme: String, val uri: URI) {
    actual val path: String = uri.path
    actual val fullPath: String = uri.path // FIXME: this is not the fullpath with query

    private val queryParams: Map<String, String> = calculateQueryParam()

    actual fun getQueryParam(key: String): String? = queryParams.get(key)

    private fun calculateQueryParam(): Map<String, String> {
        val query = uri.query.orEmpty()
        val params = query.split(QUERY_PARAM_SEPARATOR)

        return params.associate {
            it.substringBefore(QUERY_PARAM_EQUAL_OPERATOR) to
                it.substringAfter(QUERY_PARAM_EQUAL_OPERATOR)
        }
    }
}
