package br.com.devsrsouza.kotlinx.serialization.uri.decode

import br.com.devsrsouza.kotlinx.serialization.uri.Path
import br.com.devsrsouza.kotlinx.serialization.uri.Query
import br.com.devsrsouza.kotlinx.serialization.uri.newUriPath
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

private val expectations = Expectations(
    expected = WithMap("test_name", mapOf("id" to "test")),
    expectedWithOptional = WithMapOptional("test_name_optional"),
    filterStringify = { Json {}.encodeToString(it) },
)

@Serializable
data class WithMap(
    @Path override val name: String,
    @Query override  val filter: Map<String, String>,
) : ExpectedType<Map<String, String>>

@Serializable
data class WithMapOptional(
    @Path override val name: String,
    @Query override val filter: Map<String, String> = mapOf("test" to "optional"),
) : ExpectedType<Map<String, String>>

class NestedMapTest : FunSpec({
    decodeRootClassTest(expectations)
})

