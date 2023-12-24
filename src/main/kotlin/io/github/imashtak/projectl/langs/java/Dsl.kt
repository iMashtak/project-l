package io.github.imashtak.projectl.langs.java

import java.lang.RuntimeException

@DslMarker
annotation class JavaDslMarker

@JavaDslMarker
class JavaDsl {

    internal val def = JavaDefinition()

    fun packageName(name: String) {
        def.packageName = name
    }

    fun import(import: String) {
        def.imports.add(import)
    }

    fun `class`(`class`: String, i: JavaClassDsl.() -> Unit) {
        val clazz = JavaClass(`class`, JavaKind.CLASS)
        JavaClassDsl(clazz, def.imports).apply(i)
        def.classes.add(clazz)
    }

    fun `interface`(`interface`: String, i: JavaClassDsl.() -> Unit) {
        val clazz = JavaClass(`interface`, JavaKind.INTERFACE)
        JavaClassDsl(clazz, def.imports).apply(i)
        def.classes.add(clazz)
    }
}

@JavaDslMarker
class JavaClassDsl(
    private val x: JavaClass,
    private val imports: MutableSet<String>
) {
    fun public() {
        x.visibility = JavaVisibility.PUBLIC
    }

    fun private() {
        x.visibility = JavaVisibility.PRIVATE
    }

    fun protected() {
        x.visibility = JavaVisibility.PROTECTED
    }

    fun packagePrivate() {
        x.visibility = JavaVisibility.PACKAGE_PRIVATE
    }

    fun field(field: String, i: JavaFieldDsl.() -> Unit) {
        val javaField = JavaField(field)
        JavaFieldDsl(javaField, imports).apply(i)
        x.fields.add(javaField)
    }

    fun method(method: String, i: JavaMethodDsl.() -> Unit) {
        val javaMethod = JavaMethod(method)
        JavaMethodDsl(javaMethod, imports).apply(i)
        x.methods.add(javaMethod)
    }
}

@JavaDslMarker
class JavaFieldDsl(
    private val x: JavaField,
    private val imports: MutableSet<String>
) {
    fun public() {
        x.visibility = JavaVisibility.PUBLIC
    }

    fun private() {
        x.visibility = JavaVisibility.PRIVATE
    }

    fun protected() {
        x.visibility = JavaVisibility.PROTECTED
    }

    fun packagePrivate() {
        x.visibility = JavaVisibility.PACKAGE_PRIVATE
    }

    fun static() {
        x.static = true
    }

    fun import(import: String) {
        imports.add(import)
    }

    fun type(type: String) {
        x.type = type
    }

    fun type(type: Class<*>) {
        x.type = type.simpleName
        imports.add(type.canonicalName)
    }
}

@JavaDslMarker
class JavaMethodDsl(
    private val x: JavaMethod,
    private val imports: MutableSet<String>
) {
    fun public() {
        x.visibility = JavaVisibility.PUBLIC
    }

    fun private() {
        x.visibility = JavaVisibility.PRIVATE
    }

    fun protected() {
        x.visibility = JavaVisibility.PROTECTED
    }

    fun packagePrivate() {
        x.visibility = JavaVisibility.PACKAGE_PRIVATE
    }

    fun static() {
        x.static = true
    }

    fun arg(arg: String, type: String) {
        x.args.add(JavaMethodArg(arg, type))
    }

    fun body(i: JavaBlockStatementDsl.() -> Unit) {
        val block = JavaBlockStatement()
        JavaBlockStatementDsl(block, imports).apply(i)
        x.body = block
    }
}

@JavaDslMarker
class JavaBlockStatementDsl(
    private val x: JavaBlockStatement,
    private val imports: MutableSet<String>
) {
    fun `var`(`var`: String, i: JavaVariableDeclarationStatementDsl.() -> Unit) {
        val statement = JavaVariableDeclarationStatement(`var`)
        JavaVariableDeclarationStatementDsl(statement, imports).apply(i)
        x.statements.add(statement)
    }

    fun `val`(`val`: String, i: JavaVariableDeclarationStatementDsl.() -> Unit) {
        val statement = JavaVariableDeclarationStatement(`val`)
        JavaVariableDeclarationStatementDsl(statement, imports).apply(i).apply { final() }
        x.statements.add(statement)
    }

    fun statement(statement: String) {
        x.statements.add(JavaRawStatement(statement))
    }

    fun `if`(`if`: String, i: JavaBlockStatementDsl.() -> Unit) {
        val block = JavaBlockStatement()
        JavaBlockStatementDsl(block, imports).apply(i)
        val statement = JavaIfStatement(JavaExpression(`if`), block)
        x.statements.add(statement)
    }

    fun `else`(i: JavaBlockStatementDsl.() -> Unit) {
        val block = JavaBlockStatement()
        JavaBlockStatementDsl(block, imports).apply(i)
        val statement = JavaElseStatement(block)
        x.statements.add(statement)
    }

    fun elseif(elseif: String, i: JavaBlockStatementDsl.() -> Unit) {
        val block = JavaBlockStatement()
        JavaBlockStatementDsl(block, imports).apply(i)
        val statement = JavaElseIfStatement(JavaExpression(elseif), block)
        x.statements.add(statement)
    }

    fun `for`(starting: String, condition: String, ending: String, i: JavaBlockStatementDsl.() -> Unit) {
        val block = JavaBlockStatement()
        JavaBlockStatementDsl(block, imports).apply(i)
        val statement = JavaForStatement(
            JavaExpression(starting),
            JavaExpression(condition),
            JavaExpression(ending),
            block
        )
        x.statements.add(statement)
    }

    fun foreach(foreach: String, collection: String, i: JavaBlockStatementDsl.() -> Unit) {
        val block = JavaBlockStatement()
        JavaBlockStatementDsl(block, imports).apply(i)
        val statement = JavaForEachStatement(
            foreach, JavaExpression(collection), block
        )
        x.statements.add(statement)
    }

    fun foreach(foreach: String, type: String, collection: String, i: JavaBlockStatementDsl.() -> Unit) {
        val block = JavaBlockStatement()
        JavaBlockStatementDsl(block, imports).apply(i)
        val statement = JavaForEachStatement(
            foreach, JavaExpression(collection), block, type
        )
        x.statements.add(statement)
    }

    fun `while`(`while`: String, i: JavaBlockStatementDsl.() -> Unit) {
        val block = JavaBlockStatement()
        JavaBlockStatementDsl(block, imports).apply(i)
        val statement = JavaWhileStatement(
            `while`, block
        )
        x.statements.add(statement)
    }

    fun switch(switch: String, i: JavaSwitchStatementDsl.() -> Unit) {
        val statement = JavaSwitchStatement(switch)
        JavaSwitchStatementDsl(statement, imports).apply(i)
        x.statements.add(statement)
    }

    fun `return`(`return`: String) {
        x.statements.add(JavaReturnStatement(JavaExpression(`return`)))
    }

    fun `break`() {
        x.statements.add(JavaBreakStatement())
    }

    fun `continue`() {
        x.statements.add(JavaContinueStatement())
    }
}

@JavaDslMarker
class JavaVariableDeclarationStatementDsl(
    private val x: JavaVariableDeclarationStatement,
    private val imports: MutableSet<String>
) {
    fun final() {
        x.final = true
    }

    fun type(type: String) {
        x.type = type
    }

    fun type(type: Class<*>) {
        x.type = type.simpleName
        imports.add(type.canonicalName)
    }

    fun expr(expr: String) {
        x.expression = JavaExpression(expr)
    }
}

@JavaDslMarker
class JavaSwitchStatementDsl(
    private val x: JavaSwitchStatement,
    private val imports: MutableSet<String>
) {
    fun oldStyle() {
        x.style = SwitchStyle.OLD
    }

    fun case(case: String, i: (JavaBlockStatementDsl.() -> Unit)? = null) {
        if (i == null && x.style == SwitchStyle.NEW) {
            throw RuntimeException()
        } else if (i == null) {
            val javaCase = JavaSwitchCase(case)
            x.cases.add(javaCase)
        } else {
            val block = JavaBlockStatement()
            JavaBlockStatementDsl(block, imports).apply(i)
            val javaCase = JavaSwitchCase(case, block)
            x.cases.add(javaCase)
        }
    }
}