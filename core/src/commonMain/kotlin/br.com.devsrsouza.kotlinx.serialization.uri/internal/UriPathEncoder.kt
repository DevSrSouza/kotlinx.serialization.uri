package br.com.devsrsouza.kotlinx.serialization.uri.internal

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.internal.TaggedEncoder
import kotlinx.serialization.json.Json

@OptIn(InternalSerializationApi::class)
internal class UriPathEncoder(
    val queryParams: MutableMap<String, String>,
    val pathParams: MutableMap<String, String>,
) : TaggedEncoder<UriDesc>() {
    private val json = Json {
        encodeDefaults = true
    }
    private val jsonEncoderStack = arrayListOf<JsonEncodeWrapper>()

    override fun SerialDescriptor.getTag(index: Int): UriDesc = extractTag(this, index)

    override fun shouldEncodeElementDefault(descriptor: SerialDescriptor, index: Int): Boolean = true

    override fun encodeNull() {
        // Nothing, we don't encode query/path nulls
    }

    override fun encodeTaggedValue(tag: UriDesc, value: Any) {
        when (tag.paramType) {
            ParamType.QUERY -> queryParams.put(tag.elementName, value.toString())
            ParamType.PATH -> pathParams.put(tag.elementName, value.toString())
        }
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        if (currentTagOrNull == null) return this

        val type = descriptor.jsonEncodeElementType()

        return type?.let {
            JsonEncodeWrapper(
                descriptor = descriptor,
                type = it,
                uriTag = currentTag,
            ).also(jsonEncoderStack::add)
        } ?: super.beginStructure(descriptor)
    }

    override fun endEncode(descriptor: SerialDescriptor) {
        for (currentEncoder in jsonEncoderStack) {
            val jsonElement = currentEncoder.buildJsonElementFromEncoded()

            val encodedJson = json.encodeToString(jsonElement)

            when (currentEncoder.uriTag.paramType) {
                ParamType.QUERY -> queryParams.put(currentEncoder.uriTag.elementName, encodedJson)
                ParamType.PATH -> pathParams.put(currentEncoder.uriTag.elementName, encodedJson)
            }
        }

        jsonEncoderStack.clear()
    }
}
