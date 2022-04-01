package br.com.devsrsouza.kotlinx.serialization.uri.internal

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.internal.TaggedDecoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

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
                name = descriptor.getElementDescriptor(index).serialName,
                descriptor = descriptor.getElementDescriptor(index)
            )

    override fun decodeTaggedNotNullMark(tag: JsonTag): Boolean =
        (tag.descriptor.isNullable && retrieveValue() == null).not()

    override fun decodeTaggedBoolean(tag: JsonTag): Boolean = withTag(tag) {
        val value = retrievePrimitiveValueStrict().content.lowercase()
        value.toBooleanStrictOrNull()
            ?: throw SerializationException(
                "Unable to parse '$value' as type Boolean. Serial type '${tag.name}'"
            )
    }

    override fun decodeTaggedByte(tag: JsonTag): Byte = withTag(tag) {
        retrieveNumberValueStrict(String::toByteOrNull, "Byte")
    }

    override fun decodeTaggedShort(tag: JsonTag): Short = withTag(tag) {
        retrieveNumberValueStrict(String::toShortOrNull, "Short")
    }

    override fun decodeTaggedInt(tag: JsonTag): Int = withTag(tag) {
        retrieveNumberValueStrict(String::toIntOrNull, "Int")
    }

    override fun decodeTaggedLong(tag: JsonTag): Long = withTag(tag) {
        retrieveNumberValueStrict(String::toLongOrNull, "Long")
    }

    override fun decodeTaggedFloat(tag: JsonTag): Float = withTag(tag) {
        retrieveNumberValueStrict(String::toFloatOrNull, "Float")
    }

    override fun decodeTaggedDouble(tag: JsonTag): Double = withTag(tag) {
        retrieveNumberValueStrict(String::toDoubleOrNull, "Double")
    }

    override fun decodeTaggedChar(tag: JsonTag): Char =
        withTag(tag) {
            val value = retrieveStringValueStrict()

            value.takeIf { it.length == 1 }?.get(0)
                ?: throw SerializationException(
                    "Unable to decode '$value' because is not Char type. Serial type '${tag.name}'"
                )
        }

    override fun decodeTaggedString(tag: JsonTag): String = withTag(tag) { retrieveStringValueStrict() }

    override fun decodeTaggedEnum(tag: JsonTag, enumDescriptor: SerialDescriptor): Int =
        withTag(tag) {
            val value = retrieveStringValueStrict()
            findEnumIndexByElementName(value, enumDescriptor)
                ?: throw SerializationException(
                    "Unable to decode'$value' as a Enum value from enum with serial name " +
                        "'${enumDescriptor.serialName}'"
                )
        }

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

    private fun retrieveValueStrict(): JsonElement =
        retrieveValue() ?: throw SerializationException("Field '${currentTag.name}' is required, but it was missing")

    private fun retrievePrimitiveValueStrict(): JsonPrimitive {
        val value = retrieveValueStrict()
        return value as? JsonPrimitive ?: throw SerializationException(
            "Unable to decode '$value' as json primitive. Serial type '${currentTag.name}'"
        )
    }

    private fun <T> retrieveNumberValueStrict(map: String.() -> T?, type: String): T {
        val value = retrievePrimitiveValueStrict()

        val result = value.takeUnless { it.isString }?.content ?: throw SerializationException(
            "Unable to decode '$value' because is not a json number primitive. Serial type '${currentTag.name}'"
        )

        return result.map() ?: throw SerializationException(
            "Unable to parse '$result' as type $type. Serial type '${currentTag.name}'"
        )
    }

    private fun retrieveStringValueStrict(): String {
        val value = retrievePrimitiveValueStrict()

        return value.takeIf { it.isString }?.content
            ?: throw SerializationException(
                "Unable to decode '$value' because is not json string primitive. Serial type " +
                    "'${currentTag.name}'"
            )
    }
}
