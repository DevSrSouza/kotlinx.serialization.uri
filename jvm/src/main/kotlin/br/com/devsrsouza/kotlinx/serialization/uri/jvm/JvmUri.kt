package br.com.devsrsouza.kotlinx.serialization.uri.jvm

import br.com.devsrsouza.kotlinx.serialization.uri.Uri
import java.net.URI

internal const val QUERY_PARAM_SEPARATOR = "&"
internal const val QUERY_PARAM_EQUAL_OPERATOR = "="
internal const val QUERY_PARAM_INITIAL_OPERATOR = "?"

internal class JvmUri(override val pathScheme: String, val uri: URI) : Uri {
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
