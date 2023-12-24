package io.github.imashtak.projectl.langs.java

import com.google.googlejavaformat.java.Formatter
import com.google.googlejavaformat.java.JavaFormatterOptions
import io.github.imashtak.projectl.LangFile
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

data class JavaSettings(
    var format: JavaFormatStyle = JavaFormatStyle.GOOGLE
)

enum class JavaFormatStyle {
    GOOGLE, AOSP, ONELINE
}

class JavaFile : LangFile<JavaSettings> {

    private val def: JavaDefinition

    internal constructor(dsl: JavaDsl) {
        def = dsl.def
    }

    override fun dump(out: OutputStream, settingsInitializer: JavaSettings.() -> Unit) {
        val settings = JavaSettings().apply(settingsInitializer)
        if (settings.format == JavaFormatStyle.ONELINE) {
            val printer = JavaOneLineWriter(out, StandardCharsets.UTF_8)
            printer.write(def)
        } else {
            val os = ByteArrayOutputStream()
            val printer = JavaOneLineWriter(os, StandardCharsets.UTF_8)
            printer.write(def)
            val oneline = os.toString(StandardCharsets.UTF_8)
            println(oneline)
            val formatterBuilder = JavaFormatterOptions.builder()
            if (settings.format == JavaFormatStyle.AOSP) {
                formatterBuilder.style(JavaFormatterOptions.Style.AOSP)
            }
            val formatter = Formatter(formatterBuilder.build())
            val result = formatter.formatSource(oneline)
            out.write(result.toByteArray(StandardCharsets.UTF_8))
        }
    }
}

fun java(i: JavaDsl.() -> Unit): JavaFile {
    val dsl = JavaDsl().apply(i)
    return JavaFile(dsl)
}