package io.github.imashtak.projectl.dsl.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode

@DslMarker
annotation class JsonDslMarker

@JsonDslMarker
class JsonDsl {
    internal var root: JsonNode? = null

    fun text(text: String) {
        if (root != null) throw RuntimeException()
        root = TextNode(text)
    }

    fun obj(i: JsonObjectDsl.() -> Unit) {
        if (root != null) throw RuntimeException()
        root = ObjectNode(JsonNodeFactory.instance)
        JsonObjectDsl(root as ObjectNode).apply(i)
    }

    fun array(i: JsonArrayDsl.() -> Unit) {
        if (root != null) throw RuntimeException()
        root = ArrayNode(JsonNodeFactory.instance)
        JsonArrayDsl(root as ArrayNode).apply(i)
    }
}

@JsonDslMarker
class JsonObjectDsl(
    private val node: ObjectNode
) {
    fun text(text: String, value: String) {
        node.put(text, value)
    }

    fun int(int: String, value: Int) {
        node.put(int, value)
    }

    fun obj(obj: String, i: JsonObjectDsl.() -> Unit) {
        val next = node.putObject(obj)
        JsonObjectDsl(next).apply(i)
    }

    fun array(array: String, i: JsonArrayDsl.() -> Unit) {
        val next = node.putArray(array)
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

    fun int(int: Int) {
        node.add(int)
    }

    fun obj(i: JsonObjectDsl.() -> Unit) {
        val objNode = ObjectNode(JsonNodeFactory.instance)
        JsonObjectDsl(objNode).apply(i)
        node.add(objNode)
    }
}