package br.com.devsrsouza.kotlinx.serialization.uri.internal

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

internal fun SerialDescriptor.jsonDecodeElementType(element: JsonElement): JsonDecodeElementType? {
    return when (kind) {
        StructureKind.CLASS -> JsonDecodeElementType.JsonClass(element.jsonObject)
        StructureKind.LIST -> JsonDecodeElementType.JsonList(element.jsonArray)
        StructureKind.MAP -> JsonDecodeElementType.JsonMap(element.jsonObject.entries.toList())
        else -> null
    }
}

internal fun SerialDescriptor.jsonEncodeElementType(): JsonEncodeElementType? {
    return when (kind) {
        StructureKind.CLASS -> JsonEncodeElementType.JsonClass(mutableMapOf())
        StructureKind.LIST -> JsonEncodeElementType.JsonList(mutableListOf())
        StructureKind.MAP -> JsonEncodeElementType.JsonMap(mutableMapOf())
        else -> null
    }
}
