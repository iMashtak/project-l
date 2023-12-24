package io.github.imashtak.projectl.langs

import io.github.imashtak.projectl.langs.java.JavaFormatStyle
import io.github.imashtak.projectl.langs.java.java
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream

class JavaTest {

    @Test
    fun testSimple() {
        val result = java {
            packageName("com.example")

            import("java.util.*")

            `class`("Sample") {
                field("x") {
                    public()
                    type(ArrayList::class.java)
                }

                method("test") {
                    public()
                    arg("in", "String")
                    body {
                        `var`("s") { expr("5") }
                        `val`("y") { expr("\"some\"") }
                        statement("s = 10")
                        `if`("s < 10") {
                            statement("s = 15")
                        }
                        elseif("s < 100") {
                            statement("s = 38")
                        }
                        `else` {
                            statement("s = 125")
                        }
                        foreach("elem", "Object", "this.x") {
                            statement("System.out.println(elem)")
                        }
                        `while`("s < 120") {
                            statement("s += 10")
                        }
                        switch("s") {
                            oldStyle()
                            case("10")
                            case("20")
                            case("30") {
                                statement("s = 150")
                                `break`()
                            }
                        }
                        switch("s") {
                            case("150") {
                                `return`("x")
                            }
                        }
                    }
                }
            }

            `interface`("Another") {
                packagePrivate()
            }
        }

        val os = ByteArrayOutputStream()
        result.dump(os) {
            format = JavaFormatStyle.AOSP
        }
        val str = os.toString()

        println()
        println(str)
    }
}