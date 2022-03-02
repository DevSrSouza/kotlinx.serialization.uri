package br.com.devsrsouza.kotlinx.serialization.uri.android

import br.com.devsrsouza.kotlinx.serialization.uri.Uri
import br.com.devsrsouza.kotlinx.serialization.uri.UriData
import br.com.devsrsouza.kotlinx.serialization.uri.UriProvider
import android.net.Uri as AndroidUri

class AndroidUriProvider : UriProvider {
    override fun uri(uriScheme: String, uriPath: String): Uri {
        val androidUri = runCatching { AndroidUri.parse("https://endpoint.domain$uriPath") }.getOrThrow()
        return AndroidUri(uriScheme, androidUri)
    }

    override fun createUriPath(uriScheme: String, uriData: UriData): String {
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
