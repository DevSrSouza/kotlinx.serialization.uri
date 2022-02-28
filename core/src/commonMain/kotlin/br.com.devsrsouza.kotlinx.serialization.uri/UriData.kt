package br.com.devsrsouza.kotlinx.serialization.uri

data class UriData(
    val queryParams: Map<String, String>,
    val pathParams: Map<String, String>,
)
