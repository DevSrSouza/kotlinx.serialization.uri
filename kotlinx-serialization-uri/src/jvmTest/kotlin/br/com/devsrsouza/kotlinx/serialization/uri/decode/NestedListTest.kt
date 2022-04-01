package br.com.devsrsouza.kotlinx.serialization.uri.decode

import br.com.devsrsouza.kotlinx.serialization.uri.Path
import br.com.devsrsouza.kotlinx.serialization.uri.Query
import io.kotest.core.spec.style.FunSpec
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val expectations = Expectations(
    expected = WithList("test_name", listOf("kotlin")),
    expectedWithOptional = WithListOptional("test_name_optional"),
    expectedWithNullable = WithListNullable("test_name_optional", null),
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

@Serializable
data class WithListNullable(
    @Path override val name: String,
    @Query override val filter: List<String>?,
) : ExpectedType<List<String>?>

class NestedListTest : FunSpec({
    decodeRootClassTest(expectations)
})
