package br.com.devsrsouza.kotlinx.serialization.uri

/**
 * Definition of Uri
 */
interface Uri {
    /**
     * Returns the URL scheme ex: /{name}/delete
     */
    val pathScheme: String

    /**
     * Returns only the path without params
     */
    val path: String

    /**
     * Returns the full path with params
     */
    val fullPath: String

    /**
     * Gets a URL query param ex: ?key=value
     */
    fun getQueryParam(key: String): String?

    /**
     * Gets a URL path param ex: /path/{name}
     */
    fun getPathParam(key: String): String? {
        val keyScheme = "{$key}"

        val pathStart = pathScheme.substringBefore(keyScheme)
        val pathEnd = pathScheme.substringAfter(keyScheme)
        val pathRegex = "^$pathStart(.+)$pathEnd$".toRegex()

        return pathRegex.find(path)?.groups?.get(1)?.value
    }
}
