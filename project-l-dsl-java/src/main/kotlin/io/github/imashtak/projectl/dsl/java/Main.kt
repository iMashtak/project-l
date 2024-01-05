package io.github.imashtak.projectl.dsl.java

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.MethodDeclaration
import io.github.imashtak.projectl.dsl.LangSource
import java.io.OutputStream

class JavaSettings

fun java(i: JavaDsl.() -> Unit): LangSource<JavaSettings, CompilationUnit> {
    val dsl = JavaDsl().apply(i)
    return JavaSource(dsl.x)
}

fun javaMethod(name: String, i: JavaMethodDsl.() -> Unit): LangSource<JavaSettings, MethodDeclaration> {
    val compilationUnit = CompilationUnit()
    val methodDeclaration = MethodDeclaration()
    methodDeclaration.setName(name)
    val dsl = JavaMethodDsl(methodDeclaration, compilationUnit).apply(i)
    return JavaSource(dsl.x)
}

private class JavaSource<AST>(private val x: AST) : LangSource<JavaSettings, AST> {

    override fun dump(out: OutputStream, settingsInitializer: JavaSettings.() -> Unit) {
        val settings = JavaSettings().apply(settingsInitializer)
        out.write(x.toString().toByteArray())
    }

    override fun ast(): AST {
        return x
    }
}