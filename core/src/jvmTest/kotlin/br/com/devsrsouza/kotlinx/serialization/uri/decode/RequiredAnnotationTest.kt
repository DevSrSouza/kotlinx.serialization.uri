package br.com.devsrsouza.kotlinx.serialization.uri.decode

import br.com.devsrsouza.kotlinx.serialization.uri.Path
import br.com.devsrsouza.kotlinx.serialization.uri.Query
import br.com.devsrsouza.kotlinx.serialization.uri.newUriPath
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RequiredAnnotationTest {
    @Serializable
    data class WithoutAnnotation(
        val name: String,
        val filter: String,
    )

    @Serializable
    data class WithAnnotation(
        @Path val name: String,
        @Query val filter: String,
    )

    private val expectedName = "example"
    private val expectedFilter = "somefilter"

    private val uriPathScheme = "/{name}"
    private val uriPath = "/$expectedName?filter=$expectedFilter"

    @Test
    fun `should fail when deserializable class is without Path Query annotation`() {
        val uriPathFormat = newUriPath(uriPathScheme)

        assertThrows<SerializationException> {
            uriPathFormat.decodeFromString<WithoutAnnotation>(uriPath)
        }
    }

    @Test
    fun `should successfully deserializable class with Path Query annotation`() {
        val uriPathFormat = newUriPath(uriPathScheme)

        val result = uriPathFormat.decodeFromString<WithAnnotation>(uriPath)

        result.name shouldBe expectedName
        result.filter shouldBe expectedFilter
    }


}
