package io.github.imashtak.projectl.dsl.camel

import org.apache.camel.model.dataformat.CsvDataFormat
import org.apache.camel.model.dataformat.YAMLDataFormat

// --- Endpoint DSL ---

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

// --- DataFormat DSL ---

// ---- YAML ----

@CamelDslMarker
class CamelDataFormatYamlDsl {
    internal val x: YAMLDataFormat = YAMLDataFormat()
}

fun CamelDataFormatDsl.yaml(i: CamelDataFormatYamlDsl.() -> Unit) {
    x = CamelDataFormatYamlDsl().apply(i).x
}

// ---- CSV ----

@CamelDslMarker
class CamelDataFormatCsvDsl {

    internal val x: CsvDataFormat = CsvDataFormat()

    fun delimiter(delimiter: String) {
        x.delimiter = delimiter
    }
}

fun CamelDataFormatDsl.csv(i: CamelDataFormatCsvDsl.() -> Unit) {
    x = CamelDataFormatCsvDsl().apply(i).x
}