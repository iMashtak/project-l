package io.github.imashtak.projectl.dsl.http

import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.URI

@DslMarker
annotation class HttpDslMarker

@HttpDslMarker
class HttpRequestDsl {

    internal val x: HttpRequest = HttpRequest()

    fun get() {
        x.method = "GET"
    }

    fun post() {
        x.method = "POST"
    }

    fun put() {
        x.method = "PUT"
    }

    fun patch() {
        x.method = "PATCH"
    }

    fun delete() {
        x.method = "DELETE"
    }

    fun head() {
        x.method = "HEAD"
    }

    fun options() {
        x.method = "OPTIONS"
    }

    fun trace() {
        x.method = "TRACE"
    }

    fun connect() {
        x.method = "CONNECT"
    }

    fun method(method: String) {
        x.method = method
    }

    fun uri(uri: String) {
        x.uri = URI.create(uri)
    }

    fun uri(uri: URI) {
        x.uri = uri
    }

    fun version(version: String) {
        x.version = version
    }

    fun headers(i: HttpHeadersDsl.() -> Unit) {
        HttpHeadersDsl(x.headers).apply(i)
    }

    fun body(body: String) {
        x.body = ByteArrayInputStream(body.toByteArray())
    }

    fun body(body: InputStream) {
        x.body = body
    }

    fun body(body: ByteArray) {
        x.body = ByteArrayInputStream(body)
    }

    fun body(bodyProvider: () -> InputStream) {
        x.body = bodyProvider.invoke()
    }
}

@HttpDslMarker
class HttpResponseDsl {

    internal val x: HttpResponse = HttpResponse()

    fun version(version: String) {
        x.version = version
    }

    fun code(code: String) {
        x.code = code
        if (httpCodeReasons.containsKey(code)) {
            x.reason = httpCodeReasons[code]!!
        } else {
            throw IllegalArgumentException("Cannot automatically find corresponding reason for code: $code")
        }
    }

    fun code(code: Int) {
        code(code.toString())
    }

    fun code(code: String, reason: String) {
        x.code = code
        x.reason = reason
    }

    fun code(code: Int, reason: String) {
        x.code = code.toString()
        x.reason = reason
    }

    fun headers(i: HttpHeadersDsl.() -> Unit) {
        HttpHeadersDsl(x.headers).apply(i)
    }

    fun body(body: String) {
        x.body = ByteArrayInputStream(body.toByteArray())
    }

    fun body(body: InputStream) {
        x.body = body
    }

    fun body(body: ByteArray) {
        x.body = ByteArrayInputStream(body)
    }

    fun body(bodyProvider: () -> InputStream) {
        x.body = bodyProvider.invoke()
    }
}

@HttpDslMarker
class HttpHeadersDsl(
    internal val headers: MutableList<Pair<String, String>>
) {

    fun host(host: String) {
        header("Host", host)
    }

    fun contentType(contentType: String) {
        header("Content-Type", contentType)
    }

    fun header(header: String, value: String) {
        headers.add(Pair(header, value))
    }
}

val httpCodeReasons: Map<String, String> = mapOf(
    Pair("100", "Continue"),
    Pair("101", "Switching Protocols"),
    Pair("102", "Processing"),
    Pair("103", "Early Hints"),
    Pair("200", "OK"),
    Pair("201", "Created"),
    Pair("202", "Accepted"),
    Pair("203", "Non-Authoritative Information"),
    Pair("204", "No Content"),
    Pair("205", "Reset Content"),
    Pair("206", "Partial Content"),
    Pair("207", "Multi-Status"),
    Pair("208", "Already Reported"),
    Pair("226", "IM Used"),
    Pair("300", "Multiple Choices"),
    Pair("301", "Moved Permanently"),
    Pair("302", "Found"),
    Pair("303", "See Other"),
    Pair("304", "Not Modified"),
    Pair("305", "Use Proxy"),
    Pair("307", "Temporary Redirect"),
    Pair("308", "Permanent Redirect"),
    Pair("400", "Bad Request"),
    Pair("401", "Unauthorized"),
    Pair("402", "Payment Required"),
    Pair("403", "Forbidden"),
    Pair("404", "Not Found"),
    Pair("405", "Method Not Allowed"),
    Pair("406", "Not Acceptable"),
    Pair("407", "Proxy Authentication Required"),
    Pair("408", "Request Timeout"),
    Pair("409", "Conflict"),
    Pair("410", "Gone"),
    Pair("411", "Length Required"),
    Pair("412", "Precondition Failed"),
    Pair("413", "Payload Too Large"),
    Pair("414", "URI Too Long"),
    Pair("415", "Unsupported Media Type"),
    Pair("416", "Range Not Satisfiable"),
    Pair("417", "Expectation Failed"),
    Pair("418", "I'm a teapot"),
    Pair("421", "Misdirected Request"),
    Pair("422", "Unprocessable Content"),
    Pair("423", "Locked"),
    Pair("424", "Failed Dependency"),
    Pair("425", "Too Early"),
    Pair("426", "Upgrade Required"),
    Pair("428", "Precondition Required"),
    Pair("429", "Too Many Requests"),
    Pair("431", "Request Header Fields Too Large"),
    Pair("451", "Unavailable For Legal Reasons"),
    Pair("500", "Internal Server Error"),
    Pair("501", "Not Implemented"),
    Pair("502", "Bad Gateway"),
    Pair("503", "Service Unavailable"),
    Pair("504", "Gateway Timeout"),
    Pair("505", "HTTP Version Not Supported"),
    Pair("506", "Variant Also Negotiates"),
    Pair("507", "Insufficient Storage"),
    Pair("508", "Loop Detected"),
    Pair("510", "Not Extended"),
    Pair("511", "Network Authentication Required"),
)