package br.com.devsrsouza.kotlinx.serialization.uri.internal

import br.com.devsrsouza.kotlinx.serialization.uri.Path
import br.com.devsrsouza.kotlinx.serialization.uri.Query
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor

internal fun extractTag(
    descriptor: SerialDescriptor,
    index: Int
): UriDesc {
    val elementName = descriptor.getElementName(index)
    val isNullable = descriptor.getElementDescriptor(index).isNullable
    val isOptional = descriptor.isElementOptional(index)
    val isPath = descriptor.findElementAnnotation<Path>(index) != null
    val isQuery = descriptor.findElementAnnotation<Query>(index) != null

    val paramType = when {
        isPath -> ParamType.PATH
        isQuery -> ParamType.QUERY
        else -> throw SerializationException(
            "UriPathSerializer: You forgot to define your property ${descriptor.serialName}.$elementName with @Path or @Query."
        )
    }
    return UriDesc(elementName, paramType, isNullable, isOptional)
}

internal inline fun <reified A : Annotation> SerialDescriptor.findElementAnnotation(
    elementIndex: Int
): A? {
    return getElementAnnotations(elementIndex).find { it is A } as A?
}

internal fun findEnumIndexByElementName(
    value: String,
    descriptor: SerialDescriptor,
): Int? = (0 until descriptor.elementsCount).firstOrNull { index ->
    value == descriptor.getElementName(index)
}
