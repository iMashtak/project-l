package io.github.imashtak.projectl.dsl.camel

import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream

class MainTest {

    @Test
    fun test() {
        val result = camel {
            route {
                id("first")
                from {
                    component("netty-http")
                    url("http://localhost:8080")
                    property("key", "value")
                }
                steps {
                    to {
                        component("mock")
                        url("test")
                    }
                    setHeader("My-Header", simple("\${null}"))
                    to {
                        nettyHTTP {
                            protocol("https")
                            host("org.example")
                            port(8080)
                            keepAlive(false)
                        }
                    }
                    marshal {
                        csv {
                            delimiter(";")
                        }
                    }
                }
            }
            bean {
                name("some")
                type("org.example.Some")
                property("key", "value")
            }
        }

        val os = ByteArrayOutputStream()
        result.dump(os)
        val str = os.toString()

        println(str)
    }
}