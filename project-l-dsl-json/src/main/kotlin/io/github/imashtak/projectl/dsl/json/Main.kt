package io.github.imashtak.projectl.dsl.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.node.NullNode
import io.github.imashtak.projectl.dsl.LangSource
import java.io.OutputStream

class JsonSettings

fun json(i: JsonDsl.() -> Unit): LangSource<JsonSettings, JsonNode> {
    val dsl = JsonDsl().apply(i)
    return JsonSource(dsl.root)
}

private class JsonSource(
    private val root: JsonNode?
) : LangSource<JsonSettings, JsonNode> {

    override fun dump(out: OutputStream, settingsInitializer: JsonSettings.() -> Unit) {
        val mapper = ObjectMapper()
        mapper.writeValue(out, root)
    }

    override fun ast(): JsonNode {
        return root ?: NullNode.getInstance()
    }
}