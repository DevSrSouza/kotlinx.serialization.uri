package br.com.devsrsouza.kotlinx.serialization.uri

internal fun newUriProvider() = UriProvider(shouldEncodeValues = false)

internal fun newUriPath(scheme: String) = UriPath(
    uriPathScheme = scheme,
    uriProvider = newUriProvider(),
)
