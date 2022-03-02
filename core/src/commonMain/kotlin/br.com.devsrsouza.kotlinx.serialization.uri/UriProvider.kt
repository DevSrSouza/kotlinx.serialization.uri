package br.com.devsrsouza.kotlinx.serialization.uri

val URI_PARAM_SCHEME_REGEX = "\\{(.+)}".toRegex()

interface UriProvider {
    fun uri(uriScheme: String, uriPath: String): Uri

    fun createUriPath(uriScheme: String, uriData: UriData): String

    fun buildPath(uriScheme: String, pathParams: Map<String, String>): String {
        return URI_PARAM_SCHEME_REGEX.replace(uriScheme) {
            val paramName = it.groups.get(1)?.value ?: return@replace it.value

            pathParams.get(paramName) ?: it.value
        }
    }
}
