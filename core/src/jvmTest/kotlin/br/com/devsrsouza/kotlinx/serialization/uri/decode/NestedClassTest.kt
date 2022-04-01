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
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.TestInstance

private val expectations = Expectations(
    expected = WithClass("test_name", Filter("test")),
    expectedWithOptional = WithClassOptional("test_name_optional"),
    expectedWithNullable = WithClassNullable("test_name_nullable", filter = null),
    filterStringify = { Json {}.encodeToString(it) },
)

@Serializable
data class Filter(
    val id: String,
)

@Serializable
data class WithClass(
    @Path override val name: String,
    @Query override val filter: Filter,
) : ExpectedType<Filter>

@Serializable
data class WithClassOptional(
    @Path override val name: String,
    @Query override val filter: Filter = Filter("optional"),
) : ExpectedType<Filter>

@Serializable
data class WithClassNullable(
    @Path override val name: String,
    @Query override val filter: Filter?,
) : ExpectedType<Filter?>

class NestedClassTest : FunSpec({
    decodeRootClassTest(expectations)
})
