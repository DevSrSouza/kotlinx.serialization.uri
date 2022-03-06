package br.com.devsrsouza.kotlinx.serialization.uri.internal

import br.com.devsrsouza.kotlinx.serialization.uri.Uri
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.CompositeDecoder.Companion.DECODE_DONE
import kotlinx.serialization.internal.TaggedDecoder
import kotlinx.serialization.json.Json

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
            if (tag.isOptional && retrieveParamFromUri(tag) == null) {
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

    override fun decodeTaggedBoolean(tag: UriDesc): Boolean = retrieveParamFromUriStrict(tag).lowercase()
        .toBooleanStrictOrNull()
        ?: throwParseFailure("true or false", "Boolean", tag)

    override fun decodeTaggedByte(tag: UriDesc): Byte = retrieveParamFromUriStrict(tag).toByteOrNull()
        ?: throwParseFailure("number", "Byte", tag)

    override fun decodeTaggedShort(tag: UriDesc): Short = retrieveParamFromUriStrict(tag).toShortOrNull()
        ?: throwParseFailure("number", "Short", tag)

    override fun decodeTaggedInt(tag: UriDesc): Int = retrieveParamFromUriStrict(tag).toIntOrNull()
        ?: throwParseFailure("number", "Int", tag)

    override fun decodeTaggedLong(tag: UriDesc): Long = retrieveParamFromUriStrict(tag).toLongOrNull()
        ?: throwParseFailure("number", "Long", tag)

    override fun decodeTaggedFloat(tag: UriDesc): Float = retrieveParamFromUriStrict(tag).toFloatOrNull()
        ?: throwParseFailure("floating number", "Float", tag)

    override fun decodeTaggedDouble(tag: UriDesc): Double = retrieveParamFromUriStrict(tag).toDoubleOrNull()
        ?: throwParseFailure("floating number", "Float", tag)

    override fun decodeTaggedChar(tag: UriDesc): Char = retrieveParamFromUriStrict(tag).takeIf { it.length == 1 }
        ?.get(0)
        ?: throwParseFailure("one char", "Char", tag)

    override fun decodeTaggedString(tag: UriDesc): String = retrieveParamFromUriStrict(tag)

    override fun decodeTaggedEnum(tag: UriDesc, enumDescriptor: SerialDescriptor): Int =
        findEnumIndexByElementName(retrieveParamFromUriStrict(tag), enumDescriptor)
            ?: throw SerializationException(
                "${tag.paramType.name} param '${tag.elementName}' with serial name '${tag.serialName}' is not " +
                    "a value from enum with serial name '${enumDescriptor.serialName}'"
            )

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        // currentTagOrNull being null means that this descriptor is from the root class
        if (currentTagOrNull == null) return this

        val rawJson = retrieveParamFromUri(currentTag)!!
        val jsonElement = json.parseToJsonElement(rawJson)

        val type = descriptor.jsonDecodeElementType(jsonElement)

        return type?.let { JsonDecoderWrapper(descriptor, it) } ?: super.beginStructure(descriptor)
    }

    private fun retrieveParamFromUri(desc: UriDesc): String? =
        when (desc.paramType) {
            ParamType.QUERY -> uri.getQueryParam(desc.elementName)
            ParamType.PATH -> uri.getPathParam(desc.elementName)
        }

    private fun retrieveParamFromUriStrict(desc: UriDesc): String =
        retrieveParamFromUri(desc) ?: throwFieldNotFound(desc)

    private fun throwFieldNotFound(desc: UriDesc): Nothing {
        throw SerializationException(
            "Field '${desc.elementName}' is required in for type with serial name '${desc.serialName}', but it " +
                "was missing on ${desc.paramType} param"
        )
    }

    private fun throwParseFailure(expectedType: String, type: String, tag: UriDesc): Nothing {
        throw SerializationException(
            "${tag.paramType.name} param '${tag.elementName}' with serial name '${tag.serialName}' is not " +
                "$expectedType value, could not parse to '$type'"
        )
    }
}
