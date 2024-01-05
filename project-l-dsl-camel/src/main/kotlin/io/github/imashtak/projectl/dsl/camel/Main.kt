package io.github.imashtak.projectl.dsl.camel

import io.github.imashtak.projectl.dsl.LangSource
import org.apache.camel.CamelContext
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.model.RoutesDefinition
import org.apache.camel.yaml.LwModelToYAMLDumper
import java.io.OutputStream

fun camel(i: CamelDsl.() -> Unit): LangSource<CamelSettings, CamelContext> {
    val dsl = CamelDsl().apply(i)
    return CamelSource(dsl.ctx)
}

class CamelSettings {
    var format: CamelDumpFormat = CamelDumpFormat.YAML
}

enum class CamelDumpFormat {
    YAML, XML
}

class CamelSource(
    private val ctx: DefaultCamelContext
) : LangSource<CamelSettings, CamelContext> {

    override fun dump(out: OutputStream, settingsInitializer: CamelSettings.() -> Unit) {
        val settings = CamelSettings().apply(settingsInitializer)
        val dumper = LwModelToYAMLDumper()
        val def = RoutesDefinition()
        def.routes.addAll(ctx.routeDefinitions)
        val yamlRoute = dumper.dumpModelAsYaml(ctx, def)
        val yamlBeans = dumper.dumpBeansAsYaml(ctx, ctx.registryBeans as List<Any>?)
        out.write("$yamlRoute${System.lineSeparator()}$yamlBeans".toByteArray())
    }

    override fun ast(): CamelContext {
        return ctx
    }
}