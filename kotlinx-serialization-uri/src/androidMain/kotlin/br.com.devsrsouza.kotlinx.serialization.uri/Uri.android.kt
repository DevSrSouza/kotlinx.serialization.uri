package br.com.devsrsouza.kotlinx.serialization.uri

import android.net.Uri as AndroidUri

actual class Uri(actual val pathScheme: String, val uri: AndroidUri) {
    actual val path: String = requireNotNull(uri.path)
    // TODO: test this to be sure is right
    actual val fullPath: String = "$path${if (uri.query != null) "?${uri.query}" else ""}"

    actual fun getQueryParam(key: String): String? = uri.getQueryParameter(key)
}
