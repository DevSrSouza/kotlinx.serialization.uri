package br.com.devsrsouza.kotlinx.serialization.uri

interface UriProvider {
    fun uri(uriScheme: String, uriPath: String): Uri
}
