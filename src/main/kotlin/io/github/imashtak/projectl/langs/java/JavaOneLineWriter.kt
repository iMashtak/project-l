package io.github.imashtak.projectl.langs.java

import java.io.OutputStream
import java.nio.charset.Charset

internal class JavaOneLineWriter(
    private val os: OutputStream,
    private val charset: Charset
) {
    fun write(x: JavaDefinition) {
        if (x.packageName.isNotBlank()) {
            write("package ")
            write(x.packageName)
            write(";")
        }
        for (import in x.imports.sortedBy { it }) {
            write("import ")
            write(import)
            write(";")
        }
        for (clazz in x.classes) {
            write(clazz)
        }
    }

    fun write(x: JavaClass) {
        write(x.visibility)
        write(x.kind)
        write(x.name)
        write("{")
        for (field in x.fields) {
            write(field)
        }
        for (method in x.methods) {
            write(method)
        }
        write("}")
    }

    fun write(x: JavaKind) {
        when (x) {
            JavaKind.CLASS -> write("class ")
            JavaKind.INTERFACE -> write("interface ")
            JavaKind.ENUM -> write("enum ")
            JavaKind.ANNOTATION -> write("@interface ")
        }
    }

    fun write(x: JavaField) {
        write(x.visibility)
        if (x.static) {
            write("static ")
        }
        write(x.type)
        write(" ")
        write(x.name)
        write(";")
    }

    fun write(x: JavaMethod) {
        write(x.visibility)
        write(x.name)
        write("(")
        var comma = false
        for (arg in x.args) {
            if (comma) write(",")
            write(arg.type)
            write(" ")
            write(arg.name)
            comma = true
        }
        write(")")
        if (x.body != null) {
            write(x.body!!)
        }
    }

    fun write(x: JavaStatement) {
        when (x) {
            is JavaRawStatement -> write(x)
            is JavaBlockStatement -> write(x)
            is JavaVariableDeclarationStatement -> write(x)
            is JavaIfStatement -> write(x)
            is JavaElseStatement -> write(x)
            is JavaElseIfStatement -> write(x)
        }
    }

    fun write(x: JavaRawStatement) {
        write(x.statement)
        write(";")
    }

    fun write(x: JavaBlockStatement) {
        write("{")
        for (statement in x.statements) {
            write(statement)
        }
        write("}")
    }

    fun write(x: JavaVariableDeclarationStatement) {
        write(x.type)
        write(" ")
        write(x.name)
        if (x.expression != null) {
            write("=")
            write(x.expression!!)
        }
        write(";")
    }

    fun write(x: JavaIfStatement) {
        write("if(")
        write(x.condition)
        write(")")
        write(x.body)
    }

    fun write(x: JavaElseStatement) {
        write("else")
        write(x.body)
    }

    fun write(x: JavaElseIfStatement) {
        write("else if(")
        write(x.condition)
        write(")")
        write(x.body)
    }

    fun write(x: JavaExpression) {
        write(x.expr)
    }

    fun write(x: String) {
        os.write(x.toByteArray(charset))
    }

    fun write(x: JavaVisibility) {
        when (x) {
            JavaVisibility.PUBLIC -> os.write("public ".toByteArray(charset))
            JavaVisibility.PRIVATE -> os.write("private ".toByteArray(charset))
            JavaVisibility.PROTECTED -> os.write("protected ".toByteArray(charset))
            JavaVisibility.PACKAGE_PRIVATE -> {}
        }
    }
}