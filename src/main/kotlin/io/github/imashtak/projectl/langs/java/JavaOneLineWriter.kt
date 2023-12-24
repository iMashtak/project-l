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
        for (clazz in x.types) {
            write(clazz)
        }
    }

    fun write(x: JavaType) {
        when (x) {
            is JavaClass -> write(x)
            is JavaInterface -> write(x)
            is JavaAnnotation -> write(x)
            is JavaEnum -> write(x)
        }
    }

    fun write(x: JavaClass) {
        for (annotation in x.annotations) {
            write(annotation)
        }
        write(x.visibility)
        write("class ")
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

    fun write(x: JavaInterface) {
        for (annotation in x.annotations) {
            write(annotation)
        }
        write(x.visibility)
        write("interface ")
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

    fun write(x: JavaAnnotation) {
        for (annotation in x.annotations) {
            write(annotation)
        }
        write(x.visibility)
        write("@interface ")
        write(x.name)
        write("{")
        for (field in x.fields) {
            write(field)
        }
        write("}")
    }

    fun write(x: JavaEnum) {
        for (annotation in x.annotations) {
            write(annotation)
        }
        write(x.visibility)
        write("enum ")
        write(x.name)
        write("{")
        for (value in x.values) {
            write(value.name)
            if (value.args.isNotEmpty()) {
                write("(")
                var comma = false
                for (arg in value.args) {
                    if (comma) write(",")
                    write(arg.raw)
                    comma = true
                }
                write(")")
            }
        }
        write(";")
        for (field in x.fields) {
            write(field)
        }
        for (method in x.methods) {
            write(method)
        }
        write("}")
    }

    fun write(x: JavaField) {
        for (annotation in x.annotations) {
            write(annotation)
        }
        write(x.visibility)
        if (x.static) {
            write("static ")
        }
        write(x.type)
        write(" ")
        write(x.name)
        if (x.initializer != null) {
            write("=")
            write(x.initializer!!)
        }
        write(";")
    }

    fun write(x: JavaAnnotationField) {
        write(x.type)
        write(" ")
        write(x.name)
        write("()")
        if (x.default != null) {
            write("default ")
            write(x.default!!)
        }
        write(";")
    }

    fun write(x: JavaMethod) {
        for (annotation in x.annotations) {
            write(annotation)
        }
        write(x.visibility)
        if (x.static) {
            write("static ")
        }
        if (x.default) {
            write("default ")
        }
        if (x.returns != null) {
            write(x.returns!!)
            write(" ")
        }
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
        } else {
            write(";")
        }
    }

    fun write(x: JavaAnnotationUsage) {
        write("@" + x.name)
        if (x.args.isEmpty()){
            write(" ")
            return
        }
        if (x.args.size == 1 && x.args[0].name == "value") {
            val values = x.args[0].values
            write("(")
            writeAnnotationList(values)
            write(")")
        } else {
            var comma = false
            write("(")
            for (arg in x.args) {
                if (comma) write(",")
                write(arg.name)
                write("=")
                writeAnnotationList(arg.values)
                comma = true
            }
            write(")")
        }
    }

    fun writeAnnotationList(values: MutableList<JavaExpression>) {
        if (values.size == 1) {
            write(values[0].raw)
        } else {
            var comma = false
            write("{")
            for (value in values) {
                if (comma) write(",")
                write(value.raw)
                comma = true
            }
            write("}")
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
            is JavaForStatement -> write(x)
            is JavaForEachStatement -> write(x)
            is JavaWhileStatement -> write(x)
            is JavaSwitchStatement -> write(x)
            is JavaReturnStatement -> write(x)
            is JavaBreakStatement -> write(x)
            is JavaContinueStatement -> write(x)
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
        if (x.final) {
            write("final ")
        }
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

    fun write(x: JavaForStatement) {
        write("for(")
        write(x.starting)
        write(";")
        write(x.condition)
        write(";")
        write(x.ending)
        write(")")
        write(x.body)
    }

    fun write(x: JavaForEachStatement) {
        write("for(")
        write(x.variableType)
        write(" ")
        write(x.variable)
        write(":")
        write(x.collection)
        write(")")
        write(x.body)
    }

    fun write(x: JavaWhileStatement) {
        write("while(")
        write(x.condition)
        write(")")
        write(x.body)
    }

    fun write(x: JavaSwitchStatement) {
        write("switch(")
        write(x.obj)
        write("){")
        for (case in x.cases) {
            write(case, x.style)
        }
        write("}")
    }

    fun write(x: JavaSwitchCase, fashion: SwitchStyle) {
        write("case ")
        write(x.match)
        when (fashion) {
            SwitchStyle.NEW -> {
                write("->")
                write(x.body!!)
            }
            SwitchStyle.OLD -> {
                write(":")
                if (x.body != null) {
                    write(x.body!!)
                }
            }
        }
    }

    fun write(x: JavaReturnStatement) {
        write("return ${x.expr.raw};")
    }

    fun write(x: JavaBreakStatement) {
        write("break;")
    }

    fun write(x: JavaContinueStatement) {
        write("continue;")
    }

    fun write(x: JavaExpression) {
        write(x.raw)
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