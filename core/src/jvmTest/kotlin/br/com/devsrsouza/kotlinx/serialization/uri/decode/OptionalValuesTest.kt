package br.com.devsrsouza.kotlinx.serialization.uri.decode

import br.com.devsrsouza.kotlinx.serialization.uri.Path
import br.com.devsrsouza.kotlinx.serialization.uri.Query
import br.com.devsrsouza.kotlinx.serialization.uri.newUriPath
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import org.junit.jupiter.api.Test

class OptionalValuesTest {

    companion object {
        val expectedOptionalFilter = "optionalFilter"

        val expectedName = "example"
        val expectedFilter = "somefilter"

        val uriPathScheme = "/{name}"
        val uriPathWithFilter = "/$expectedName?filter=$expectedFilter"
        val uriPathWithoutFilter = "/$expectedName"
    }

    @Serializable
    data class WithOptional(
        @Path val name: String,
        @Query val filter: String = expectedOptionalFilter,
    )

    @Test
    fun `filter should be optionalFilter when query param is not available on uri path`() {
        val uriPathFormat = newUriPath(uriPathScheme)

        val result = uriPathFormat.decodeFromString<WithOptional>(uriPathWithoutFilter)

        result.name shouldBe expectedName
        result.filter shouldBe expectedOptionalFilter
    }

    @Test
    fun `filter should be somefilter when query filter param is somefilter in the uri path`() {
        val uriPathFormat = newUriPath(uriPathScheme)

        val result = uriPathFormat.decodeFromString<WithOptional>(uriPathWithFilter)

        result.name shouldBe expectedName
        result.filter shouldBe expectedFilter
    }
}
