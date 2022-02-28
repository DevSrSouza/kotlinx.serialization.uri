package br.com.devsrsouza.kotlinx.serialization.uri

val URI_PARAM_SCHEME_REGEX = "\\{(.+)}".toRegex()

interface UriProvider {
    fun uri(uriScheme: String, uriPath: String): Uri

    fun createUriPath(uriScheme: String, uriData: UriData): String
}
