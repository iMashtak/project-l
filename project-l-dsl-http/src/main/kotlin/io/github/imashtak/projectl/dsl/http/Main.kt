package io.github.imashtak.projectl.dsl.http

import io.github.imashtak.projectl.dsl.LangSource
import java.io.OutputStream

fun httpRequest(i: HttpRequestDsl.() -> Unit) : LangSource<HttpSettings, HttpRequest> {
    val dsl = HttpRequestDsl().apply(i)
    return HttpRequestSource(dsl.x)
}

fun httpResponse(i: HttpResponseDsl.() -> Unit) : LangSource<HttpSettings, HttpResponse> {
    val dsl = HttpResponseDsl().apply(i)
    return HttpResponseSource(dsl.x)
}

class HttpSettings

class HttpRequestSource(
    private val x: HttpRequest
) : LangSource<HttpSettings, HttpRequest> {

    override fun dump(out: OutputStream, settingsInitializer: HttpSettings.() -> Unit) {
        val settings = HttpSettings().apply(settingsInitializer)
        x.writeTo(out)
    }

    override fun ast(): HttpRequest {
        return x
    }
}

class HttpResponseSource(
    private val x: HttpResponse
) : LangSource<HttpSettings, HttpResponse> {

    override fun dump(out: OutputStream, settingsInitializer: HttpSettings.() -> Unit) {
        val settings = HttpSettings().apply(settingsInitializer)
        x.writeTo(out)
    }

    override fun ast(): HttpResponse {
        return x
    }
}