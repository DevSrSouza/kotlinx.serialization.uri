package br.com.devsrsouza.kotlinx.serialization.uri.internal

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.internal.TaggedEncoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive

internal sealed interface JsonEncodeElementType {
    class JsonClass(val element: MutableMap<String, JsonElement>) : JsonEncodeElementType
    class JsonList(val element: MutableList<JsonElement>) : JsonEncodeElementType
    class JsonMap(
        val element: MutableMap<String, JsonElement>,
        val putElementStack: MutableList<JsonElement> = mutableListOf(),
    ) : JsonEncodeElementType
}

@OptIn(InternalSerializationApi::class)
internal class JsonEncodeWrapper(
    val descriptor: SerialDescriptor,
    val type: JsonEncodeElementType,
    val uriTag: UriDesc,
) : TaggedEncoder<JsonTag>() {
    private val jsonEncoderStack = arrayListOf<Pair<JsonEncodeWrapper, JsonTag>>()

    override fun SerialDescriptor.getTag(index: Int): JsonTag =
        if (type is JsonEncodeElementType.JsonClass)
            JsonTag(
                name = getElementName(index),
                descriptor = getElementDescriptor(index),
            )
        else
            JsonTag(
                name = descriptor.serialName,
                descriptor = descriptor
            )

    override fun shouldEncodeElementDefault(descriptor: SerialDescriptor, index: Int): Boolean = true
    override fun encodeTaggedNull(tag: JsonTag): Unit = encodeJsonElement(tag, JsonNull)
    override fun encodeTaggedInt(tag: JsonTag, value: Int): Unit = encodeJsonElement(tag, JsonPrimitive(value))
    override fun encodeTaggedByte(tag: JsonTag, value: Byte): Unit = encodeJsonElement(tag, JsonPrimitive(value))
    override fun encodeTaggedShort(tag: JsonTag, value: Short): Unit = encodeJsonElement(tag, JsonPrimitive(value))
    override fun encodeTaggedLong(tag: JsonTag, value: Long): Unit = encodeJsonElement(tag, JsonPrimitive(value))
    override fun encodeTaggedFloat(tag: JsonTag, value: Float): Unit = encodeJsonElement(tag, JsonPrimitive(value))
    override fun encodeTaggedDouble(tag: JsonTag, value: Double): Unit = encodeJsonElement(tag, JsonPrimitive(value))
    override fun encodeTaggedBoolean(tag: JsonTag, value: Boolean): Unit = encodeJsonElement(tag, JsonPrimitive(value))
    override fun encodeTaggedChar(tag: JsonTag, value: Char): Unit = encodeJsonElement(tag, JsonPrimitive("$value"))
    override fun encodeTaggedString(tag: JsonTag, value: String): Unit = encodeJsonElement(tag, JsonPrimitive(value))

    private fun encodeJsonElement(tag: JsonTag, jsonElement: JsonElement) {
        when (type) {
            is JsonEncodeElementType.JsonClass -> type.element.put(tag.name, jsonElement)
            is JsonEncodeElementType.JsonList -> type.element.add(jsonElement)
            is JsonEncodeElementType.JsonMap -> {
                when (type.putElementStack.size) {
                    1 -> type.element.put(type.putElementStack.removeLast().jsonPrimitive.content, jsonElement)
                    0 -> type.putElementStack.add(jsonElement)
                }
            }
        }
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        if (currentTagOrNull == null) return this

        val type = descriptor.jsonEncodeElementType() ?: return super.beginStructure(descriptor)

        return JsonEncodeWrapper(descriptor, type, uriTag)
            .also { jsonEncoderStack.add(it to currentTag) }
    }

    fun buildJsonElementFromEncoded(): JsonElement =
        when (type) {
            is JsonEncodeElementType.JsonClass -> buildJsonObject {
                for ((key, value) in type.element)
                    put(key, value)
            }
            is JsonEncodeElementType.JsonList -> buildJsonArray {
                for (value in type.element)
                    add(value)
            }
            is JsonEncodeElementType.JsonMap -> buildJsonObject {
                for ((key, value) in type.element)
                    put(key, value)
            }
        }

    override fun endEncode(descriptor: SerialDescriptor) {
        for ((currentEncoder, tag) in jsonEncoderStack) {
            val jsonElement = currentEncoder.buildJsonElementFromEncoded()

            encodeJsonElement(tag, jsonElement)
        }

        jsonEncoderStack.clear()
    }
}
