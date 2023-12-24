package io.github.imashtak.projectl.langs.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import io.github.imashtak.projectl.LangFile
import java.io.OutputStream

data class JsonSettings(
    var format: JsonFormatStyle = JsonFormatStyle.ONELINE
)

enum class JsonFormatStyle {
    ONELINE
}

class JsonFile : LangFile<JsonSettings> {

    private var root: JsonNode? = null

    internal constructor(dsl: JsonDsl) {
        this.root = dsl.root
    }

    override fun dump(out: OutputStream, settingsInitializer: JsonSettings.() -> Unit) {
        val mapper = ObjectMapper()
        mapper.writeValue(out, root)
    }
}

@DslMarker
annotation class JsonDslMarker

@JsonDslMarker
class JsonDsl {
    internal var root: JsonNode? = null

    fun text(text: String) {
        root = TextNode(text)
    }

    fun obj(i: JsonObjectDsl.() -> Unit) {
        root = ObjectNode(JsonNodeFactory.instance)
        JsonObjectDsl(root as ObjectNode).apply(i)
    }

    fun array(i: JsonArrayDsl.() -> Unit) {
        root = ArrayNode(JsonNodeFactory.instance)
        JsonArrayDsl(root as ArrayNode).apply(i)
    }
}

@JsonDslMarker
class JsonObjectDsl(
    private val node: ObjectNode
) {
    fun text(key: String, value: String) {
        node.put(key, value)
    }

    fun int(key: String, value: Int) {
        node.put(key, value)
    }

    fun obj(key: String, i: JsonObjectDsl.() -> Unit) {
        val next = node.putObject(key)
        JsonObjectDsl(next).apply(i)
    }

    fun array(key: String, i: JsonArrayDsl.() -> Unit) {
        val next = node.putArray(key)
        JsonArrayDsl(next).apply(i)
    }
}

@JsonDslMarker
class JsonArrayDsl(
    private val node: ArrayNode
) {
    fun text(text: String) {
        node.add(text)
    }

    fun obj(i: JsonObjectDsl.() -> Unit) {
        val objNode = ObjectNode(JsonNodeFactory.instance)
        JsonObjectDsl(objNode).apply(i)
        node.add(objNode)
    }
}

fun json(i: JsonDsl.() -> Unit): JsonFile {
    val dsl = JsonDsl().apply(i)
    return JsonFile(dsl)
}