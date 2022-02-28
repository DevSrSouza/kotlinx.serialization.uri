package br.com.devsrsouza.kotlinx.serialization.uri.internal

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

internal fun SerialDescriptor.jsonElementType(element: JsonElement): JsonElementType? {
    return when (kind) {
        StructureKind.CLASS -> JsonElementType.JsonClass(element.jsonObject)
        StructureKind.LIST -> JsonElementType.JsonList(element.jsonArray)
        StructureKind.MAP -> JsonElementType.JsonMap(element.jsonObject.entries.toList())
        else -> null
    }
}
