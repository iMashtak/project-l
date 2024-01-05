package io.github.imashtak.projectl.dsl.java

import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

class MainTest {

    @Test
    fun testJavaMethod() {
        val result = javaMethod("test") {
            annotation("TestAnnotation", "\"some\"")
        }

        val os = ByteArrayOutputStream()
        result.dump(os)
        val str = os.toString()

        println(str)
    }

    @Test
    fun testSimple() {
        val result = java {
            `package`("org.example")

            annotation("TestAnn") {
                retention(RetentionPolicy.RUNTIME)
                target(ElementType.METHOD, ElementType.TYPE)
                annotation("MyCustom") {
                    arg("argp", "a", "b", "c")
                }
                member("name", "String", "ABC")
                member("oops", "String[]", "{}")
            }

            `class`("TestClass") {
                public()

                field("first", ArrayList::class.java)
                field("second", String::class.java) {
                    volatile()
                    annotation("Test")
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
                        foreach("var item", "list") {
                            statement("println(item)")
                        }
                        `for`("var i = 0", "i < list.size()", "++i") {

                        }
                        `for`(null, null,null) {}
                        `try` {
                        }
                        `try`("var scanner = new Scanner(new File(\"test.txt\"))", "var i = 0") {
                            statement("scanner.close()")
                        }
                        catch("RuntimeException e") {}
                        catch("Exception e") {}
                        finally {  }
                        synchronized("this") {}
                    }
                }

                field("p", "Object")

                method("secondMethod") {
                    public()
                    abstract()
                    annotation("Test")
                    annotation("TestPlan", "Element.Type")
                    annotation("TestCase") {
                        value("\"simple\"")
                        arg("key", "56")
                    }
                }
            }

            `interface`("TestInterface") {
                method("firstMethod")
            }
        }

        val os = ByteArrayOutputStream()
        result.dump(os)
        val str = os.toString()

        println(str)
    }
}