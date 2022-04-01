package br.com.devsrsouza.kotlinx.serialization.uri

import android.net.Uri as AndroidUri

actual class UriProvider {
    actual fun uri(uriScheme: String, uriPath: String): Uri {
        val androidUri = runCatching { AndroidUri.parse("https://endpoint.domain$uriPath") }.getOrThrow()
        return Uri(uriScheme, androidUri)
    }

    actual fun createUriPath(uriScheme: String, uriData: UriData): String {
        val builder = AndroidUri.Builder()
            .scheme("https")
            .authority("dummy.domain")
            .path(buildPath(uriScheme, uriData.pathParams))
            .run {
                uriData.queryParams.toList().fold(this) { acc, (key, value) ->
                    acc.appendQueryParameter(key, value)
                }
            }

        return builder.build().path!! // TODO check if this is the full path with query
    }
}
