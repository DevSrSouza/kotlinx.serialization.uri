package br.com.devsrsouza.kotlinx.serialization.uri

import br.com.devsrsouza.kotlinx.serialization.uri.jvm.JvmUriProvider

internal fun newUriProvider() = JvmUriProvider(shouldEncodeValues = false)

internal fun newUriPath(scheme: String) = UriPath(
    uriPathScheme = scheme,
    uriProvider = newUriProvider(),
)
