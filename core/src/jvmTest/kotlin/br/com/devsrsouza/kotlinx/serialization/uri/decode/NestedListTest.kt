package br.com.devsrsouza.kotlinx.serialization.uri.decode

import br.com.devsrsouza.kotlinx.serialization.uri.Path
import br.com.devsrsouza.kotlinx.serialization.uri.Query
import br.com.devsrsouza.kotlinx.serialization.uri.newUriPath
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

private val expectations = Expectations(
    expected = WithList("test_name", listOf("kotlin")),
    expectedWithOptional = WithListOptional("test_name_optional"),
    filterStringify = { Json {}.encodeToString(it) },
)

@Serializable
data class WithList(
    @Path override val name: String,
    @Query override val filter: List<String>,
) : ExpectedType<List<String>>

@Serializable
data class WithListOptional(
    @Path override val name: String,
    @Query override val filter: List<String> = listOf("kmm"),
) : ExpectedType<List<String>>

class NestedListTest : FunSpec({
    decodeRootClassTest(expectations)
})
