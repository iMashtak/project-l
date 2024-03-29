package io.github.imashtak.projectl.dsl.camel

import org.apache.camel.Expression
import org.apache.camel.Predicate
import org.apache.camel.builder.PredicateBuilder
import org.apache.camel.builder.ValueBuilder
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.model.ChoiceDefinition
import org.apache.camel.model.DataFormatDefinition
import org.apache.camel.model.ProcessorDefinition
import org.apache.camel.model.RouteDefinition
import org.apache.camel.model.app.RegistryBeanDefinition
import org.apache.camel.model.dataformat.YAMLDataFormat
import org.apache.camel.model.language.ConstantExpression
import org.apache.camel.model.language.HeaderExpression
import org.apache.camel.model.language.SimpleExpression

@DslMarker
annotation class CamelDslMarker

@CamelDslMarker
class CamelDsl {

    internal val ctx: DefaultCamelContext = DefaultCamelContext()

    fun route(i: CamelRouteDsl.() -> Unit) {
        val routeDefinition = RouteDefinition()
        CamelRouteDsl(routeDefinition).apply(i)
        ctx.addRouteDefinition(routeDefinition)
    }

    fun bean(i: CamelBeanDsl.() -> Unit) {
        val dsl = CamelBeanDsl().apply(i)
        ctx.addRegistryBean(dsl.x)
    }
}

@CamelDslMarker
class CamelRouteDsl(
    private val x: RouteDefinition
) {

    fun id(id: String) {
        x.id = id
    }

    fun from(i: CamelUriDsl.() -> Unit) {
        val dsl = CamelUriDsl().apply(i)
        val uri = dsl.toUri()
        x.from(uri)
    }

    fun steps(i: CamelRouteStepsDsl.() -> Unit) {
        CamelRouteStepsDsl(x).apply(i)
    }
}

@CamelDslMarker
class CamelRouteStepsDsl(
    private val x: ProcessorDefinition<*>
) {

    fun choice(i: CamelChoiceDsl.() -> Unit) {
        val def = x.choice()
        CamelChoiceDsl(def).apply(i)
        def.end()
    }

    fun marshal(i: CamelDataFormatDsl.() -> Unit) {
        val dsl = CamelDataFormatDsl().apply(i)
        x.marshal(dsl.x)
    }

    fun setHeader(setHeader: String, value: String) {
        x.setHeader(setHeader, constant(value))
    }

    fun setHeader(setHeader: String, expr: Expression) {
        x.setHeader(setHeader, expr)
    }

    fun to(i: CamelUriDsl.() -> Unit) {
        val dsl = CamelUriDsl().apply(i)
        val uri = dsl.toUri()
        x.to(uri)
    }
}

@CamelDslMarker
class CamelChoiceDsl(
    private val x: ChoiceDefinition
) {

    fun `when`(`when`: Predicate, i: CamelRouteStepsDsl.() -> Unit) {
        val def = x.`when`(`when`)
        CamelRouteStepsDsl(def).apply(i)
        def.endChoice()
    }

    fun `when`(whenSupplier: () -> Predicate, i: CamelRouteStepsDsl.() -> Unit) {
        val def = x.`when`(whenSupplier())
        CamelRouteStepsDsl(def).apply(i)
        def.endChoice()
    }

    fun otherwise(i: CamelRouteStepsDsl.() -> Unit) {
        val def = x.otherwise()
        CamelRouteStepsDsl(def).apply(i)
        def.endChoice()
    }

    fun precondition() {
        x.precondition()
    }
}

@CamelDslMarker
class CamelUriDsl {

    private var component: String = ""
    private var url: String = ""
    private val properties: MutableMap<String, String> = mutableMapOf()

    fun component(component: String) {
        this.component = component
    }

    fun url(url: String) {
        this.url = url
    }

    fun property(property: String, value: String) {
        properties[property] = value
    }

    internal fun toUri(): String {
        val sb = StringBuilder()
        sb.append(component)
        if (component != "") {
            sb.append(":")
        }
        sb.append(url)
        var first = '?'
        for (property in properties) {
            sb.append(first)
            first = '&'
            sb.append(property.key)
            sb.append('=')
            sb.append(property.value)
        }
        return sb.toString()
    }
}

@CamelDslMarker
class CamelDataFormatDsl {

    internal lateinit var x: DataFormatDefinition
}

@CamelDslMarker
class CamelBeanDsl {

    internal val x: RegistryBeanDefinition = RegistryBeanDefinition()

    init {
        x.properties = mutableMapOf()
    }

    fun name(name: String) {
        x.name = name
    }

    fun type(type: String) {
        x.type = type
    }

    fun property(property: String, value: String) {
        x.properties[property] = value
    }
}

fun expression(expression: Expression): ValueBuilder {
    return ValueBuilder(expression)
}

fun header(header: String): ValueBuilder {
    return ValueBuilder(HeaderExpression(header))
}

fun body(): ValueBuilder {
    return ValueBuilder(simple("\${body}"))
}

fun constant(constant: String): ValueBuilder {
    return ValueBuilder(ConstantExpression(constant))
}

fun simple(simple: String): ValueBuilder {
    return ValueBuilder(SimpleExpression(simple))
}

fun not(not: ValueBuilder) {
    not.not()
}

infix fun Predicate.and(and: Predicate): Predicate {
    return PredicateBuilder.and(this, and)
}

fun and(vararg and: Predicate): Predicate {
    return PredicateBuilder.and(*and)
}