package br.com.devsrsouza.kotlinx.serialization.uri

import kotlinx.serialization.SerialInfo

/**
 * Defines that the property will be serialized by the Uri Path
 */
@SerialInfo
@Target(AnnotationTarget.PROPERTY)
annotation class Path

/**
 * Defines that the property will be serialized by the Uri query params.
 */
@SerialInfo
@Target(AnnotationTarget.PROPERTY)
annotation class Query
