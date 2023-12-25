package io.github.imashtak.projectl.langs.java

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.comments.LineComment
import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.expr.VariableDeclarationExpr
import com.github.javaparser.ast.stmt.*
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.ast.type.VarType
import io.github.imashtak.projectl.LangFile
import java.io.OutputStream

class JavaSettings()

class JavaFile(
    private val dsl: JavaDsl
) : LangFile<JavaSettings> {

    override fun dump(out: OutputStream, settingsInitializer: JavaSettings.() -> Unit) {
        val code = dsl.x.toString()
        out.write(code.toByteArray())
    }
}

@DslMarker
annotation class JavaDslMarker

@JavaDslMarker
class JavaDsl {
    internal val x: CompilationUnit = CompilationUnit()
    private val p: JavaParser = JavaParser()

    fun `package`(`package`: String) {
        x.setPackageDeclaration(`package`)
    }

    fun import(import: String) {
        x.addImport(import)
    }

    fun `class`(`class`: String, i: JavaClassDsl.() -> Unit) {
        val instance = x.addClass(`class`)
        JavaClassDsl(instance, x, p).apply(i)
    }
}

@JavaDslMarker
class JavaClassDsl(
    private val x: ClassOrInterfaceDeclaration,
    private val cu: CompilationUnit,
    private val p: JavaParser
) {
    fun public() {
        x.setModifier(Modifier.Keyword.PUBLIC, true)
    }

    fun abstract() {
        x.setModifier(Modifier.Keyword.ABSTRACT, true)
    }

    fun import(import: String) {
        cu.addImport(import)
    }

    fun field(field: String, type: String, i: (JavaFieldDsl.() -> Unit)? = null) {
        val instance = x.addField(type, field)
        if (i != null) {
            JavaFieldDsl(instance, cu, p).apply(i)
        }
    }

    fun field(field: String, type: Class<*>, i: (JavaFieldDsl.() -> Unit)? = null) {
        val instance = x.addField(type, field)
        if (i != null) {
            JavaFieldDsl(instance, cu, p).apply(i)
        }
    }

    fun method(method: String, i: (JavaMethodDsl.() -> Unit)?) {
        val instance = x.addMethod(method)
        if (i != null) {
            JavaMethodDsl(instance, cu, p).apply(i)
        }
    }
}

@JavaDslMarker
class JavaFieldDsl(
    private val x: FieldDeclaration,
    private val cu: CompilationUnit,
    private val p: JavaParser
) {
    fun public() {
        x.setModifier(Modifier.Keyword.PUBLIC, true)
    }

    fun volatile() {
        x.setModifier(Modifier.Keyword.VOLATILE, true)
    }
}

@JavaDslMarker
class JavaMethodDsl(
    private val x: MethodDeclaration,
    private val cu: CompilationUnit,
    private val p: JavaParser
) {
    fun public() {
        x.setModifier(Modifier.Keyword.PUBLIC, true)
    }

    fun private() {
        x.setModifier(Modifier.Keyword.PRIVATE, true)
    }

    fun body(i: JavaBlockStatementDsl.() -> Unit) {
        val instance = x.createBody()
        JavaBlockStatementDsl(instance, cu, p).apply(i)
    }

    fun arg(arg: String, type: String) {
        x.addParameter(type, arg)
    }

    fun arg(arg: String, type: Class<*>) {
        x.addParameter(type, arg)
    }
}

@JavaDslMarker
class JavaBlockStatementDsl(
    private val x: BlockStmt,
    private val cu: CompilationUnit,
    private val p: JavaParser
) {

    private var declaredStatement: Statement? = null

    private fun <T : Expression> toExpression(x: String): T {
        val result = p.parseExpression<T>(x)
        if (!result.isSuccessful) throw RuntimeException()
        return result.result.get()
    }

    fun statement(statement: String) {
        if (declaredStatement != null) declaredStatement = null
        x.addStatement("$statement;")
    }

    fun comment(comment: String) {
        x.addOrphanComment(LineComment(comment))
    }

    fun `if`(`if`: String, i: JavaBlockStatementDsl.() -> Unit) {
        if (declaredStatement != null) declaredStatement = null
        val instance = IfStmt()
        instance.setCondition(toExpression(`if`))
        val block = BlockStmt()
        JavaBlockStatementDsl(block, cu, p).apply(i)
        instance.setThenStmt(block)
        x.addStatement(instance)
        declaredStatement = instance
    }

    fun elseif(elseif: String, i: JavaBlockStatementDsl.() -> Unit) {
        if (declaredStatement == null || declaredStatement !is IfStmt) {
            throw RuntimeException()
        }
        val instance = declaredStatement as IfStmt
        val continuation = IfStmt()
        continuation.setCondition(toExpression(elseif))
        instance.setElseStmt(continuation)
        val block = BlockStmt()
        JavaBlockStatementDsl(block, cu, p).apply(i)
        continuation.setThenStmt(block)
        declaredStatement = continuation
    }

    fun `else`(i: JavaBlockStatementDsl.() -> Unit) {
        if (declaredStatement == null || declaredStatement !is IfStmt) {
            throw RuntimeException()
        }
        val instance = declaredStatement as IfStmt
        val block = BlockStmt()
        JavaBlockStatementDsl(block, cu, p).apply(i)
        instance.setElseStmt(block)
        declaredStatement = null
    }

    fun `for`() {
        if (declaredStatement != null) declaredStatement = null
        val instance = ForStmt()
    }

    fun foreach(foreach: String, type: String, iterable: String, i: JavaBlockStatementDsl.() -> Unit) {
        if (declaredStatement != null) declaredStatement = null
        val instance = ForEachStmt()
        when (type) {
            "var" -> instance.setVariable(VariableDeclarationExpr(VarType(), foreach))
            else -> instance.setVariable(VariableDeclarationExpr(ClassOrInterfaceType().setName(type), foreach))
        }
        instance.setIterable(toExpression(iterable))
        val block = BlockStmt()
        JavaBlockStatementDsl(block, cu, p).apply(i)
        instance.setBody(block)
        x.statements.add(instance)
    }

    fun foreach(foreach: String, iterable: String, i: JavaBlockStatementDsl.() -> Unit) {
        foreach(foreach, "var", iterable, i)
    }
}

fun java(i: JavaDsl.() -> Unit): JavaFile {
    val dsl = JavaDsl().apply(i)
    return JavaFile(dsl)
}