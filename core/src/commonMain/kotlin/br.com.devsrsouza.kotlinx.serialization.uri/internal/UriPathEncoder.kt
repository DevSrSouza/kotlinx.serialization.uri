package br.com.devsrsouza.kotlinx.serialization.uri.internal

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.internal.TaggedEncoder

@OptIn(InternalSerializationApi::class)
internal class UriPathEncoder(
    val queryParams: MutableMap<String, String>,
    val pathParams: MutableMap<String, String>,
) : TaggedEncoder<UriDesc>() {
    override fun SerialDescriptor.getTag(index: Int): UriDesc = extractTag(this, index)

    override fun shouldEncodeElementDefault(descriptor: SerialDescriptor, index: Int): Boolean = true

    override fun encodeTaggedValue(tag: UriDesc, value: Any) {
        when(tag.paramType) {
            ParamType.QUERY -> queryParams.put(tag.elementName, value.toString())
            ParamType.PATH -> pathParams.put(tag.elementName, value.toString())
        }
    }
}
