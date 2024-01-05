package io.github.imashtak.projectl.dsl.http

import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream

class MainTest {

    @Test
    fun testRequest() {
        val result = httpRequest {
            get()
            uri("/posts/1/view")
            headers {
                host("example.com")
                contentType("application/json")
            }
        }

        val os = ByteArrayOutputStream()
        result.dump(os)
        val str = os.toString()

        println(str)
    }

    @Test
    fun testResponse() {
        val result = httpResponse {
            code(201)
            body("some")
        }

        val os = ByteArrayOutputStream()
        result.dump(os)
        val str = os.toString()

        println(str)
    }
}