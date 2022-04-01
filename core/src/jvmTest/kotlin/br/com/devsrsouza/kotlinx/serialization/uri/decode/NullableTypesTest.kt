package br.com.devsrsouza.kotlinx.serialization.uri.decode

import br.com.devsrsouza.kotlinx.serialization.uri.Path
import br.com.devsrsouza.kotlinx.serialization.uri.Query
import br.com.devsrsouza.kotlinx.serialization.uri.newUriPath
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import org.junit.jupiter.api.Test

class NullableTypesTest {
    companion object {
        val expectedName = "example"
        val expectedFilter = "somefilter"

        val uriPathScheme = "/{name}"
        val uriPathWithFilter = "/$expectedName?filter=$expectedFilter"
        val uriPathWithoutFilter = "/$expectedName"
    }

    @Serializable
    data class WithNullable(
        @Path val name: String,
        @Query val filter: String?,
    )

    @Test
    fun `should output with filter null when there is not a available filter query param`() {
        val uriPathFormat = newUriPath(uriPathScheme)

        val result = uriPathFormat.decodeFromString<WithNullable>(uriPathWithoutFilter)

        result.name shouldBe expectedName
        result.filter shouldBe null
    }

    @Test
    fun `should output with filter being expected when there is a filter query param`() {
        val uriPathFormat = newUriPath(uriPathScheme)

        val result = uriPathFormat.decodeFromString<WithNullable>(uriPathWithFilter)

        result.name shouldBe expectedName
        result.filter shouldBe expectedFilter
    }
}
