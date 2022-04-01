package br.com.devsrsouza.kotlinx.serialization.uri.decode

import br.com.devsrsouza.kotlinx.serialization.uri.newUriPath
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.serializer
import org.junit.jupiter.api.Test

interface ExpectedType<T> {
    val name: String
    val filter: T
}

data class Expectations<
        Type : ExpectedType<FilterType>,
        TypeWithOptional : ExpectedType<FilterType>,
        TypeWithNullable : ExpectedType<FilterType?>,
        FilterType,
        >(
    val expected: Type,
    val expectedWithOptional: TypeWithOptional,
    val expectedWithNullable: TypeWithNullable,
    val filterStringify: (FilterType) -> String,
    val uriPathScheme: String = "/{name}",
    val uriPathWithFilter: String = "/${expected.name}?filter=${filterStringify(expected.filter)}",
    val uriPathWithoutFilter: String = "/${expectedWithOptional.name}",
)

@OptIn(InternalSerializationApi::class)
fun FunSpec.decodeRootClassTest(
    expectations: Expectations<*, *, *, *>
) {
    test("should deserialize correctly when path has a filter") {
        val uriPathFormat = newUriPath(expectations.uriPathScheme)

        val serializable = expectations.expected::class.serializer()

        val result = uriPathFormat.decodeFromString(serializable, expectations.uriPathWithFilter)

        result.name shouldBe expectations.expected.name
        result.filter shouldBe expectations.expected.filter
    }

    test("should deserialize correctly when path has a filter and has a optional value") {
        val uriPathFormat = newUriPath(expectations.uriPathScheme)

        val serializable = expectations.expectedWithOptional::class.serializer()

        val result = uriPathFormat.decodeFromString(serializable, expectations.uriPathWithFilter)

        result.name shouldBe expectations.expected.name
        result.filter shouldBe expectations.expected.filter
    }

    test("should deserialize correctly when path does not have a filter and should use optional one") {
        val uriPathFormat = newUriPath(expectations.uriPathScheme)

        val serializable = expectations.expectedWithOptional::class.serializer()

        val result = uriPathFormat.decodeFromString(serializable, expectations.uriPathWithoutFilter)

        result.name shouldBe expectations.expectedWithOptional.name
        result.filter shouldBe expectations.expectedWithOptional.filter
    }

    test("should deserialize correctly when filter is nullable and path has a filter") {
        val uriPathFormat = newUriPath(expectations.uriPathScheme)

        val serializable = expectations.expectedWithNullable::class.serializer()

        val result = uriPathFormat.decodeFromString(serializable, expectations.uriPathWithoutFilter)

        result.name shouldBe expectations.expectedWithOptional.name
        result.filter shouldBe null
    }

    test("should deserialize correctly when filter is nullable and path does not have a filter") {
        val uriPathFormat = newUriPath(expectations.uriPathScheme)

        val serializable = expectations.expectedWithNullable::class.serializer()

        val result = uriPathFormat.decodeFromString(serializable, expectations.uriPathWithFilter)

        result.name shouldBe expectations.expected.name
        result.filter shouldBe expectations.expected.filter
    }
}
