package br.com.devsrsouza.kotlinx.serialization.uri.internal

import br.com.devsrsouza.kotlinx.serialization.uri.Uri
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.CompositeDecoder.Companion.DECODE_DONE
import kotlinx.serialization.internal.TaggedDecoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@OptIn(InternalSerializationApi::class)
internal class UriPathDecoder(
    val uri: Uri,
) : TaggedDecoder<UriDesc>() {
    private val json = Json {
        isLenient = true
    }

    private var currentIndex = 0

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int =
        if (descriptor.elementsCount == currentIndex) DECODE_DONE
        else {
            val tag = extractTag(descriptor, currentIndex)
            if(tag.isOptional && retrieveParamFromUri(tag) == null) {
                currentIndex++
                decodeElementIndex(descriptor)
            } else currentIndex++
        }

    final override fun SerialDescriptor.getTag(index: Int): UriDesc = extractTag(this, index)

    override fun decodeTaggedNotNullMark(tag: UriDesc): Boolean =
        (tag.isNullable && retrieveParamFromUri(tag) == null).not()

    override fun decodeTaggedValue(tag: UriDesc): Any {
        return super.decodeTaggedValue(tag)
    }

    // TODO: replace strict parsers with a SerializationException
    override fun decodeTaggedBoolean(tag: UriDesc): Boolean = retrieveParamFromUri(tag)!!.lowercase()?.toBooleanStrict()
    override fun decodeTaggedByte(tag: UriDesc): Byte = retrieveParamFromUri(tag)!!.toByte()
    override fun decodeTaggedShort(tag: UriDesc): Short = retrieveParamFromUri(tag)!!.toShort()
    override fun decodeTaggedInt(tag: UriDesc): Int = retrieveParamFromUri(tag)!!.toInt()
    override fun decodeTaggedLong(tag: UriDesc): Long = retrieveParamFromUri(tag)!!.toLong()
    override fun decodeTaggedFloat(tag: UriDesc): Float = retrieveParamFromUri(tag)!!.toFloat()
    override fun decodeTaggedDouble(tag: UriDesc): Double = retrieveParamFromUri(tag)!!.toDouble()
    override fun decodeTaggedChar(tag: UriDesc): Char = retrieveParamFromUri(tag)!!.takeIf { it.length == 1 }?.get(0)!!
    override fun decodeTaggedString(tag: UriDesc): String = retrieveParamFromUri(tag)!!
    override fun decodeTaggedEnum(tag: UriDesc, enumDescriptor: SerialDescriptor): Int =
        findEnumIndexByElementName(retrieveParamFromUri(tag)!!, enumDescriptor)!!

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        // currentTagOrNull being null means that this descriptor is from the root class
        if(currentTagOrNull == null) return this

        val rawJson = retrieveParamFromUri(currentTag)!!
        val jsonElement = json.parseToJsonElement(rawJson)

        val type = descriptor.jsonElementType(jsonElement)

        return type?.let { JsonDecoderWrapper(descriptor, it) } ?: super.beginStructure(descriptor)
    }

    // TODO: ?
    override fun <T : Any> decodeNullableSerializableValue(deserializer: DeserializationStrategy<T?>): T? {
        return super.decodeNullableSerializableValue(deserializer)
    }

    private fun retrieveParamFromUri(desc: UriDesc): String? =
        when(desc.paramType) {
            ParamType.QUERY -> uri.getQueryParam(desc.elementName)
            ParamType.PATH -> uri.getPathParam(desc.elementName)
        }
}
