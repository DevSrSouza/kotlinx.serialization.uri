package br.com.devsrsouza.kotlinx.serialization.uri.decode

import br.com.devsrsouza.kotlinx.serialization.uri.Path
import br.com.devsrsouza.kotlinx.serialization.uri.Query
import io.kotest.core.spec.style.FunSpec
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val expectations = Expectations(
    expected = WithMap("test_name", mapOf("id" to "test")),
    expectedWithOptional = WithMapOptional("test_name_optional"),
    expectedWithNullable = WithMapNullable("test_name_nullable", null),
    filterStringify = { Json {}.encodeToString(it) },
)

@Serializable
data class WithMap(
    @Path override val name: String,
    @Query override val filter: Map<String, String>,
) : ExpectedType<Map<String, String>>

@Serializable
data class WithMapOptional(
    @Path override val name: String,
    @Query override val filter: Map<String, String> = mapOf("test" to "optional"),
) : ExpectedType<Map<String, String>>

@Serializable
data class WithMapNullable(
    @Path override val name: String,
    @Query override val filter: Map<String, String>?,
) : ExpectedType<Map<String, String>?>

class NestedMapTest : FunSpec({
    decodeRootClassTest(expectations)
})
