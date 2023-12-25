package io.github.imashtak.projectl.langs

import io.github.imashtak.projectl.langs.java.java
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream

class JavaTest {

    @Test
    fun testSimple() {
        val result = java {
            `package`("org.example")

            `class`("TestClass") {
                public()

                field("first", ArrayList::class.java)
                field("second", String::class.java) {
                    volatile()
                }

                method("firstMethod") {
                    public()
                    arg("x", "Double")
                    body {
                        statement("var a = 5")
                        comment("some")
                        `if`("x < 5") {
                            statement("println(a)")
                        }
                        elseif("x < 10") {
                            statement("var n = 4")
                        }
                        elseif("x < 25") {
                            statement("var i = 10")
                        }
                        `else` {
                            statement("var k = 5")
                        }
                        foreach("item", "list") {
                            statement("println(item)")
                        }
                    }
                }
            }
        }

        val os = ByteArrayOutputStream()
        result.dump(os)
        val str = os.toString()

        println(str)
    }
}