package io.github.imashtak.projectl.langs.properties

import io.github.imashtak.projectl.LangFile
import java.io.OutputStream
import java.lang.RuntimeException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.Objects
import java.util.Properties

data class PropertiesSettings(
    var setSpaceBetweenKeyValue: Boolean = false,
    var keyValueDelimiter: String = "=",
    var charset: Charset = StandardCharsets.ISO_8859_1,
    var lineSeparator: String = System.lineSeparator(),
    var sortByKey: Boolean = true
)

class PropertiesFile: LangFile<PropertiesSettings>, Map<String, String> {

    private val map: MutableMap<String, String> = HashMap()

    override fun dump(out: OutputStream, settingsInitializer: PropertiesSettings.() -> Unit) {
        val settings = PropertiesSettings().apply(settingsInitializer)
        val list =
            if (settings.sortByKey) map.entries.sortedBy { it.key }
            else map.entries.toList()
        for ((key, value) in list) {
            out.write(key.toByteArray(settings.charset))
            if (settings.setSpaceBetweenKeyValue) {
                out.write(" ".toByteArray(settings.charset))
            }
            out.write(settings.keyValueDelimiter.toByteArray(settings.charset))
            if (settings.setSpaceBetweenKeyValue) {
                out.write(" ".toByteArray(settings.charset))
            }
            out.write(value.toByteArray(settings.charset))
            out.write(settings.lineSeparator.toByteArray(settings.charset))
        }
    }

    internal fun put(key: String, value: Any) {
        map[key] = Objects.toString(value)
    }

    fun toJavaProperties(): Properties {
        val properties = Properties()
        for ((key, value) in map) {
            properties[key] = value
        }
        return properties
    }

    override val entries: Set<Map.Entry<String, String>>
        get() = map.entries
    override val keys: Set<String>
        get() = map.keys
    override val size: Int
        get() = map.size
    override val values: Collection<String>
        get() = map.values

    override fun isEmpty(): Boolean {
        return map.isEmpty()
    }

    override fun get(key: String): String? {
        return map[key]
    }

    override fun containsValue(value: String): Boolean {
        return map.containsValue(value)
    }

    override fun containsKey(key: String): Boolean {
        return map.containsKey(key)
    }
}

@DslMarker
annotation class PropertiesDslMarker

@PropertiesDslMarker
class PropertiesDsl(
    internal val file: PropertiesFile = PropertiesFile(),
    private val prefix: String,
    private val prefixDelimiter: String
) {
    private fun fullKey(key: String): String {
        return if (prefix.isBlank()) key else prefix + prefixDelimiter + key
    }

    fun property(key: String, value: () -> Any) {
        val fullKey = fullKey(key)
        if (file.containsKey(fullKey)) throw RuntimeException()
        file.put(fullKey, value())
    }

    fun prefix(prefix: String, i: PropertiesDsl.() -> Unit) {
        PropertiesDsl(file, fullKey(prefix), prefixDelimiter).apply(i)
    }
}

fun properties(
    prefix: String = "",
    prefixDelimiter: String = ".",
    i: PropertiesDsl.() -> Unit
): PropertiesFile {
    val dsl = PropertiesDsl(
        prefix = prefix,
        prefixDelimiter = prefixDelimiter
    ).apply(i)
    return dsl.file
}