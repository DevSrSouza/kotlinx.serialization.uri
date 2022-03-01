package br.com.devsrsouza.kotlinx.serialization.uri.internal

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.internal.TaggedDecoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonPrimitive

internal data class JsonTag(
    val name: String,
    val descriptor: SerialDescriptor,
)

internal sealed interface JsonDecodeElementType {
    class JsonClass(val element: JsonObject) : JsonDecodeElementType
    class JsonList(val element: JsonArray) : JsonDecodeElementType
    class JsonMap(val element: List<Map.Entry<String, JsonElement>>) : JsonDecodeElementType
}

@OptIn(InternalSerializationApi::class)
internal class JsonDecoderWrapper(
    val descriptor: SerialDescriptor,
    val type: JsonDecodeElementType
) : TaggedDecoder<JsonTag>() {
    private var currentIndex = 0
    private val currentIndexInUse get() = currentIndex - 1

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int =
        when (type) {
            is JsonDecodeElementType.JsonClass -> type.decodeElementIndexForClass(descriptor)
            is JsonDecodeElementType.JsonList -> type.decodeElementIndexForList(descriptor)
            is JsonDecodeElementType.JsonMap -> type.decodeElementIndexForMap(descriptor)
        }

    private fun JsonDecodeElementType.JsonClass.decodeElementIndexForClass(descriptor: SerialDescriptor): Int =
        if (descriptor.elementsCount == currentIndex) CompositeDecoder.DECODE_DONE
        else {
            val currentValue = element.get(descriptor.getTag(currentIndex).name)
            if (descriptor.isElementOptional(currentIndex) && currentValue == null) {
                currentIndex++
                decodeElementIndex(descriptor)
            } else currentIndex++
        }

    private fun JsonDecodeElementType.JsonList.decodeElementIndexForList(descriptor: SerialDescriptor): Int =
        if (currentIndex == element.size) CompositeDecoder.DECODE_DONE
        else currentIndex++

    private fun JsonDecodeElementType.JsonMap.decodeElementIndexForMap(descriptor: SerialDescriptor): Int =
        if (currentIndex == element.size * 2) CompositeDecoder.DECODE_DONE
        else currentIndex++

    override fun SerialDescriptor.getTag(index: Int): JsonTag =
        if (type is JsonDecodeElementType.JsonClass)
            JsonTag(
                name = getElementName(index),
                descriptor = getElementDescriptor(index),
            )
        else
            JsonTag(
                name = descriptor.serialName,
                descriptor = descriptor
            )

    override fun decodeTaggedNotNullMark(tag: JsonTag): Boolean =
        (tag.descriptor.isNullable && retrieveValue() == null).not()

    override fun decodeTaggedBoolean(tag: JsonTag): Boolean = withTag(tag) { retrieveValue()!!.jsonPrimitive.boolean }
    override fun decodeTaggedByte(tag: JsonTag): Byte = withTag(tag) { retrieveValueNumber(String::toByte) }
    override fun decodeTaggedShort(tag: JsonTag): Short = withTag(tag) { retrieveValueNumber(String::toShort) }
    override fun decodeTaggedInt(tag: JsonTag): Int = withTag(tag) { retrieveValueNumber(String::toInt) }
    override fun decodeTaggedLong(tag: JsonTag): Long = withTag(tag) { retrieveValueNumber(String::toLong) }
    override fun decodeTaggedFloat(tag: JsonTag): Float = withTag(tag) { retrieveValueNumber(String::toFloat) }
    override fun decodeTaggedDouble(tag: JsonTag): Double = withTag(tag) { retrieveValueNumber(String::toDouble) }
    override fun decodeTaggedChar(tag: JsonTag): Char =
        withTag(tag) {
            retrievePrimitiveValue()!!.takeIf { it.isString }!!.content.takeIf { it.length == 1 }?.get(0)!!
        }
    override fun decodeTaggedString(tag: JsonTag): String = withTag(tag) { retrieveValue()!!.jsonPrimitive.content }
    override fun decodeTaggedEnum(tag: JsonTag, enumDescriptor: SerialDescriptor): Int =
        withTag(tag) { findEnumIndexByElementName(retrieveValue()!!.jsonPrimitive.content, enumDescriptor)!! }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        if (currentTagOrNull == null) return this

        val value = retrieveValue()!!

        val type = descriptor.jsonDecodeElementType(value)

        return type?.let { JsonDecoderWrapper(descriptor, it) } ?: super.beginStructure(descriptor)
    }

    private fun <T> withTag(tag: JsonTag, context: () -> T): T {
        pushTag(tag)
        val result = context()
        popTag()

        return result
    }

    private fun retrieveValue(): JsonElement? = when (type) {
        is JsonDecodeElementType.JsonClass -> type.element.get(currentTag.name)
        is JsonDecodeElementType.JsonList -> type.element.get(currentIndexInUse)
        is JsonDecodeElementType.JsonMap -> type.element.get((currentIndexInUse) / 2).run {
            if ((currentIndexInUse) % 2 == 0) JsonPrimitive(key) else value
        }
    }
    private fun retrievePrimitiveValue(): JsonPrimitive? = retrieveValue()?.jsonPrimitive
    private fun <T> retrieveValueNumber(map: String.() -> T): T =
        retrievePrimitiveValue()!!.takeUnless { it.isString }!!.content.map()
}
