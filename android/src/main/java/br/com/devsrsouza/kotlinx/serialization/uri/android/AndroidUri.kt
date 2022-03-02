package br.com.devsrsouza.kotlinx.serialization.uri.android

import br.com.devsrsouza.kotlinx.serialization.uri.Uri
import android.net.Uri as AndroidUri

internal class AndroidUri(override val pathScheme: String, val uri: AndroidUri) : Uri {
    override val path: String = requireNotNull(uri.path)
    // TODO: test this to be sure is right
    override val fullPath: String = "$path${if (uri.query != null) "?${uri.query}" else ""}"

    override fun getQueryParam(key: String): String? = uri.getQueryParameter(key)
}
