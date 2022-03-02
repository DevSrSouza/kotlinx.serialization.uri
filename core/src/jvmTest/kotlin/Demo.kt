// import br.com.devsrsouza.kotlinx.serialization.uri.Path
// import br.com.devsrsouza.kotlinx.serialization.uri.Query
// import br.com.devsrsouza.kotlinx.serialization.uri.UriPath
// import kotlinx.serialization.Serializable
// import kotlinx.serialization.encodeToString
//
// @Serializable
// data class Demo(
//    @Path val name: String,
//    @Query val test: String,
//    @Query val demo: String? = "Exemplo de default value",
//    @Query val something: SomeObject,
//    @Query val list: List<String>,
//    @Query val map: Map<String, String>,
// )
//
// @Serializable
// data class SomeObject(
//    val example: String,
//    val batata: String,
//    val test2: String = "aaaaa",
//    val bananas: String?
// )
//
// @Serializable
// data class EncodeTest(
//    @Path val name: String,
//    @Query val encode: Int,
//    @Query val encodeObj: EncodeObj,
//    @Query val list: List<String>,
//    @Query val default: String = "value",
// )
//
// @Serializable
// data class EncodeObj(
//    val insideJsonKey: String,
//    val second: Second,
//    val map: Map<String, String>,
// )
//
// @Serializable
// data class Second(
//    val nothing: String
// )
//
// fun main() {
//    val uriPath = UriPath(
//        uriPathScheme = "/home/{name}",
//        uriProvider = JvmUriProvider(shouldEncodeValues = false),
//    )
//    val result = uriPath.decodeFromString<Demo>(
//        Demo.serializer(),
//        // /home/Ronaldo?test=something with space&demo=asdasda&something={"example":"test","batata":"com feijao"}&list=["one","two","three"]&map={"test":"value","test2":"second"}
//        "/home/Ronaldo?test=something%20with%20space&demo=asdasda&something=%7B%22example%22%3A%22test%22%2C%22batata%22%3A%22com%20feijao%22%7D&list=%5B%22one%22%2C%22two%22%2C%22three%22%5D&map=%7B%22test%22%3A%22value%22%2C%22test2%22%3A%22second%22%7D"
//    )
//
//    println(result)
//
//    val encodeResult = uriPath.encodeToString(
//        EncodeTest(
//            "Dota",
//            123,
//            EncodeObj(
//                "somevalue",
//                Second("aaaaaa"),
//                mapOf("batata" to "vaue", "arroz" to "feijao")
//            ),
//            listOf("batinha", "123brow")
//        )
//    )
//
//    println(encodeResult)
// }
