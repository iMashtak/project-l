package io.github.imashtak.projectl.dsl.http

import org.apache.commons.io.IOUtils
import java.io.InputStream
import java.io.OutputStream
import java.net.URI

class HttpRequest {

    var method: String = "NONE"
    var uri: URI = URI.create("/")
    var version: String = "1.1"
    val headers: MutableList<Pair<String, String>> = mutableListOf()
    var body: InputStream? = null

    fun writeTo(out: OutputStream) {
        fun write(x: String) {
            out.write(x.toByteArray())
        }

        write("$method ${uri.toASCIIString()} HTTP/$version\r\n")
        for (header in headers) {
            write("${header.first}: ${header.second}\r\n")
        }
        write("\r\n")
        if (body != null) {
            IOUtils.copy(body, out)
        }
    }
}

class HttpResponse {

    var version: String = "1.1"
    var code: String = "200"
    var reason: String = "OK"
    val headers: MutableList<Pair<String, String>> = mutableListOf()
    var body: InputStream? = null

    fun writeTo(out: OutputStream) {
        fun write(x: String) {
            out.write(x.toByteArray())
        }

        write("HTTP/$version $code $reason\r\n")
        for (header in headers) {
            write("${header.first}: ${header.second}\r\n")
        }
        write("\r\n")
        if (body != null) {
            IOUtils.copy(body, out)
        }
    }
}