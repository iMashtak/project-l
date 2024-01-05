package io.github.imashtak.projectl.dsl.camel

@CamelDslMarker
class CamelNettyHttpUriDsl(private val it: CamelUriDsl) {

    init {
        it.component("netty-http")
    }

    private var protocol: String = "http"
    private var host: String = "localhost"
    private var port: String = "8080"

    fun protocol(protocol: String) {
        this.protocol = protocol
        it.url("$protocol://$host:$port")
    }

    fun host(host: String) {
        this.host = host
        it.url("$protocol://$host:$port")
    }

    fun port(port: Int) {
        this.port = port.toString()
        it.url("$protocol://$host:$port")
    }

    fun port(port: String) {
        this.port = port
        it.url("$protocol://$host:$port")
    }

    fun keepAlive(keepAlive: Boolean) {
        it.property("keepAlive", keepAlive.toString())
    }

    fun keepAlive(keepAlive: String) {
        it.property("keepAlive", keepAlive)
    }
}

fun CamelUriDsl.nettyHTTP(i: CamelNettyHttpUriDsl.() -> Unit) {
    CamelNettyHttpUriDsl(this).apply(i)
}