# Kotlinx.serialization.uri [WIP]
Retrieve Query and Path params using Kotlinx.serialization. Common use case: Deeplink URIs

## Example
Let's say you have a deeplink uri that you need to parse query and path params to navigate to your activity.

```kotlin
// your URI: https://mywebsite.domain/platform/{platform}/users?start_age=00&country_code=00
val yourUri: Uri = Uri.parse("https://mywebsite.domain/platform/android/users?start_age=21&country_code=uk")

// NOTE: At root data class is required that all properties uses @Path or @Query 
@Serializable
data class PlatformUserDeepLink(
    @Path val platform: String,
    @Query @SerialName("start_age") val startAge: Int,
    @Query @SerialName("country_code") val countryCode: String,
)

val uriPath = UriPath(
    uriPathScheme = "/platform/{platform}/users",
    uriProvider = AndroidUriProvider(),
)

val platformUserDeepLink = uriPath.decodeFromString<PlatformUserDeepLink>(yourUri.path)
// Result: PlatformUserDeepLink(platform = "Android", startAge = 21, countryCode = "uk")
```

The UriPath serializer will take care of validating required, optional and nullable values.

### Nested with Json
Some endpoints could implement Json on their queries, if this your use case, you can use nested classes.

```kotlin
// your URI: https://mywebsite.domain/platform/{platform}/users?complex_filter={"start_age":21,"country_code":"uk"}
val yourUri: Uri = Uri.parse("""https://mywebsite.domain/platform/{platform}/users?complex_filter={"start_age":21,"country_code":"uk"}""")

@Serializable
data class PlatformUserDeepLink(
    @Path val platform: String,
    @Query @SerialName("complex_filter") val complexFilter: PlatformUserComplexFilter,
)

// Nested classes does not require @Path/@Query
@Serializable
data class PlatformUserComplexFilter(
    @SerialName("start_age") val startAge: Int,
    @SerialName("country_code") val countryCode: String,
)

// same as previous example
val uriPath = UriPath(
    uriPathScheme = "/platform/{platform}/users",
    uriProvider = AndroidUriProvider(),
)

val platformUserDeepLink = uriPath.decodeFromString<PlatformUserDeepLink>(yourUri.path)
// RESULT: PlatformUserDeepLink(platform = "Android", complex_filter = PlatformUserComplexFilter(startAge = 21, countryCode = "uk"))
```