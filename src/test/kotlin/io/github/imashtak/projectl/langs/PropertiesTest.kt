package io.github.imashtak.projectl.langs

import io.github.imashtak.projectl.langs.properties.properties
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream

class PropertiesTest {

    @Test
    fun testSimple() {
        val result = properties {
            property("a.b.c") { 1 }
            prefix("some") {
                prefix("another") {
                    property("key") { 5 }
                }
            }
        }
        Assertions.assertEquals("1", result["a.b.c"])
        Assertions.assertEquals("5", result["some.another.key"])

        val os = ByteArrayOutputStream()
        result.dump(os)
        val str = os.toString()

        Assertions.assertEquals(
            """
            a.b.c=1
            some.another.key=5
            
            """.trimIndent(), str
        )
    }
}