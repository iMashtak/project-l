package io.github.imashtak.projectl.dsl.json

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream

class MainTest {

    @Test
    fun testSimple() {
        val result = json {
            obj {
                text("x", "some")
                obj("y") {
                    text("z", "another")
                }
                array("k") {
                    text("a")
                    text("b")
                }
            }
        }

        val os = ByteArrayOutputStream()
        result.dump(os)
        val str = os.toString()

        Assertions.assertEquals(
            """
            {"x":"some","y":{"z":"another"},"k":["a","b"]}
            """.trimIndent(), str
        )
    }
}