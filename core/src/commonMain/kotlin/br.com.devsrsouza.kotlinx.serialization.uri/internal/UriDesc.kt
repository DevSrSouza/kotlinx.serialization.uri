package br.com.devsrsouza.kotlinx.serialization.uri.internal

internal enum class ParamType { QUERY, PATH }

internal data class UriDesc(
    val elementName: String,
    val paramType: ParamType,
    val isNullable: Boolean,
    val isOptional: Boolean,
)
