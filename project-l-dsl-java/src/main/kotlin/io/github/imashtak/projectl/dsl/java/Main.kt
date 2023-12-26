package io.github.imashtak.projectl.dsl.java

import com.github.javaparser.ParserConfiguration.LanguageLevel
import com.github.javaparser.StaticJavaParser
import com.github.javaparser.StaticJavaParser.*
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.NodeList
import com.github.javaparser.ast.body.AnnotationDeclaration
import com.github.javaparser.ast.body.AnnotationMemberDeclaration
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.comments.LineComment
import com.github.javaparser.ast.expr.*
import com.github.javaparser.ast.stmt.*
import io.github.imashtak.projectl.LangFile
import java.io.OutputStream
import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

class JavaSettings()

class JavaFile(
    private val dsl: JavaDsl
) : LangFile<JavaSettings, CompilationUnit> {

    override fun dump(out: OutputStream, settingsInitializer: JavaSettings.() -> Unit) {
        val code = dsl.x.toString()
        out.write(code.toByteArray())
    }

    override fun ast(): CompilationUnit {
        return dsl.x
    }
}

@DslMarker
annotation class JavaDslMarker

@JavaDslMarker
class JavaDsl {

    companion object {
        init {
            getParserConfiguration().setLanguageLevel(LanguageLevel.RAW)
        }
    }

    internal val x: CompilationUnit = CompilationUnit()

    fun `package`(`package`: String) {
        x.setPackageDeclaration(`package`)
    }

    fun import(import: String) {
        x.addImport(import)
    }

    fun `class`(`class`: String, i: JavaClassOrInterfaceDsl.() -> Unit) {
        val instance = x.addClass(`class`)
        JavaClassOrInterfaceDsl(instance, x).apply(i)
    }

    fun `interface`(`interface`: String, i: JavaClassOrInterfaceDsl.() -> Unit) {
        val instance = x.addInterface(`interface`)
        JavaClassOrInterfaceDsl(instance, x).apply(i)
    }

    fun annotation(annotation: String, i: JavaAnnotationDefinitionDsl.() -> Unit) {
        val instance = x.addAnnotationDeclaration(annotation)
        JavaAnnotationDefinitionDsl(instance, x).apply(i)
    }
}

@JavaDslMarker
class JavaClassOrInterfaceDsl(
    private val x: ClassOrInterfaceDeclaration,
    private val cu: CompilationUnit
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
            JavaFieldDsl(instance, cu).apply(i)
        }
    }

    fun field(field: String, type: Class<*>, i: (JavaFieldDsl.() -> Unit)? = null) {
        val instance = x.addField(type, field)
        if (i != null) {
            JavaFieldDsl(instance, cu).apply(i)
        }
    }

    fun method(method: String, i: (JavaMethodDsl.() -> Unit)? = null) {
        val instance = x.addMethod(method)
        instance.setBody(null)
        if (i != null) {
            JavaMethodDsl(instance, cu).apply(i)
        }
    }

    fun annotation(annotation: String, i: (JavaNormalAnnotationUsageDsl.() -> Unit)? = null) {
        if (i == null) {
            x.addMarkerAnnotation(annotation)
        } else {
            val expr = NormalAnnotationExpr(Name(annotation), NodeList())
            JavaNormalAnnotationUsageDsl(expr, cu).apply(i)
            x.addAnnotation(expr)
        }
    }

    fun annotation(annotation: String, value: String) {
        x.addSingleMemberAnnotation(annotation, value)
    }
}

@JavaDslMarker
class JavaAnnotationDefinitionDsl(
    private val x: AnnotationDeclaration,
    private val cu: CompilationUnit
) {
    fun public() {
        x.setModifier(Modifier.Keyword.PUBLIC, true)
    }

    fun member(member: String, type: String, default: String? = null) {
        val instance = AnnotationMemberDeclaration()
        instance.setName(member)
        val parsedType = parseType(type)
        instance.setType(parsedType)
        if (default != null) {
            if (parsedType.isArrayType) {
                instance.setDefaultValue(parseArrayInitializerExpr(default))
            } else {
                instance.setDefaultValue(parseExpression(default))
            }
        }
        x.addMember(instance)
    }

    fun member(member: String, type: Class<*>, default: String? = null) {
        val instance = AnnotationMemberDeclaration()
        instance.setName(member)
        instance.setType(type)
        if (default != null) {
            instance.setDefaultValue(parseExpression(default))
        }
        x.addMember(instance)
    }

    fun annotation(annotation: String, i: (JavaNormalAnnotationUsageDsl.() -> Unit)? = null) {
        if (i == null) {
            x.addMarkerAnnotation(annotation)
        } else {
            val expr = NormalAnnotationExpr(Name(annotation), NodeList())
            JavaNormalAnnotationUsageDsl(expr, cu).apply(i)
            x.addAnnotation(expr)
        }
    }

    fun annotation(annotation: String, value: String) {
        x.addSingleMemberAnnotation(annotation, value)
    }

    fun documented() {
        annotation("Documented")
        cu.addImport("java.lang.annotation.Documented")
    }

    fun retention(retention: RetentionPolicy = RetentionPolicy.RUNTIME) {
        annotation("Retention", "RetentionPolicy." + retention.name)
    }

    fun target(vararg target: ElementType) {
        if (target.size == 1) {
            annotation("Target", "ElementType." + target[0].name)
        } else {
            val instance = ArrayInitializerExpr()
            for (element in target) {
                instance.values.add(parseExpression("ElementType.$element"))
            }
            x.addSingleMemberAnnotation("Target", instance)
        }
    }
}

@JavaDslMarker
class JavaFieldDsl(
    private val x: FieldDeclaration,
    private val cu: CompilationUnit
) {
    fun public() {
        x.setModifier(Modifier.Keyword.PUBLIC, true)
    }

    fun volatile() {
        x.setModifier(Modifier.Keyword.VOLATILE, true)
    }

    fun annotation(annotation: String, i: (JavaNormalAnnotationUsageDsl.() -> Unit)? = null) {
        if (i == null) {
            x.addMarkerAnnotation(annotation)
        } else {
            val expr = NormalAnnotationExpr(Name(annotation), NodeList())
            JavaNormalAnnotationUsageDsl(expr, cu).apply(i)
            x.addAnnotation(expr)
        }
    }

    fun annotation(annotation: String, value: String) {
        x.addSingleMemberAnnotation(annotation, value)
    }
}

@JavaDslMarker
class JavaMethodDsl(
    private val x: MethodDeclaration,
    private val cu: CompilationUnit
) {
    fun public() {
        x.setModifier(Modifier.Keyword.PUBLIC, true)
    }

    fun private() {
        x.setModifier(Modifier.Keyword.PRIVATE, true)
    }

    fun abstract() {
        x.setModifier(Modifier.Keyword.ABSTRACT, true)
    }

    fun default() {
        x.setModifier(Modifier.Keyword.DEFAULT, true)
    }

    fun synchronized() {
        x.setModifier(Modifier.Keyword.SYNCHRONIZED, true)
    }

    fun body(i: JavaBlockStatementDsl.() -> Unit) {
        val instance = x.createBody()
        JavaBlockStatementDsl(instance, cu).apply(i)
    }

    fun arg(arg: String, type: String) {
        x.addParameter(type, arg)
    }

    fun arg(arg: String, type: Class<*>) {
        x.addParameter(type, arg)
    }

    fun annotation(annotation: String, i: (JavaNormalAnnotationUsageDsl.() -> Unit)? = null) {
        if (i == null) {
            x.addMarkerAnnotation(annotation)
        } else {
            val expr = NormalAnnotationExpr(Name(annotation), NodeList())
            JavaNormalAnnotationUsageDsl(expr, cu).apply(i)
            x.addAnnotation(expr)
        }
    }

    fun annotation(annotation: String, value: String) {
        x.addSingleMemberAnnotation(annotation, value)
    }
}

@JavaDslMarker
class JavaBlockStatementDsl(
    private val x: BlockStmt,
    private val cu: CompilationUnit
) {

    private var declaredStatement: Statement? = null

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
        instance.setCondition(parseExpression(`if`))
        val block = BlockStmt()
        JavaBlockStatementDsl(block, cu).apply(i)
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
        continuation.setCondition(parseExpression(elseif))
        instance.setElseStmt(continuation)
        val block = BlockStmt()
        JavaBlockStatementDsl(block, cu).apply(i)
        continuation.setThenStmt(block)
        declaredStatement = continuation
    }

    fun `else`(i: JavaBlockStatementDsl.() -> Unit) {
        if (declaredStatement == null || declaredStatement !is IfStmt) {
            throw RuntimeException()
        }
        val instance = declaredStatement as IfStmt
        val block = BlockStmt()
        JavaBlockStatementDsl(block, cu).apply(i)
        instance.setElseStmt(block)
        declaredStatement = null
    }

    fun `for`(init: String?, compare: String?, update: String?, i: JavaBlockStatementDsl.() -> Unit) {
        if (declaredStatement != null) declaredStatement = null
        val instance = ForStmt()

        val initNodeList = NodeList<Expression>()
        if (init != null) {
            initNodeList.add(parseVariableDeclarationExpr(init))
        }
        instance.setInitialization(initNodeList)

        if (compare != null) {
            instance.setCompare(parseExpression(compare))
        } else {
            instance.setCompare(null)
        }

        val updateNodeList = NodeList<Expression>()
        if (update != null) {
            updateNodeList.add(parseExpression(update))
        }
        instance.setUpdate(updateNodeList)

        val block = BlockStmt()
        JavaBlockStatementDsl(block, cu).apply(i)
        instance.setBody(block)

        x.statements.add(instance)
    }

    fun foreach(foreach: String, iterable: String, i: JavaBlockStatementDsl.() -> Unit) {
        if (declaredStatement != null) declaredStatement = null
        val instance = ForEachStmt()
        instance.setVariable(parseVariableDeclarationExpr(foreach))
        instance.setIterable(parseExpression(iterable))
        val block = BlockStmt()
        JavaBlockStatementDsl(block, cu).apply(i)
        instance.setBody(block)
        x.statements.add(instance)
    }

    fun `try`(vararg `try`: String, i: JavaBlockStatementDsl.() -> Unit) {
        if (declaredStatement != null) declaredStatement = null
        val instance = TryStmt()

        val resourcesNodeList = NodeList<Expression>()
        for (element in `try`) {
            resourcesNodeList.add(parseVariableDeclarationExpr(element))
        }
        instance.setResources(resourcesNodeList)

        val block = BlockStmt()
        JavaBlockStatementDsl(block, cu).apply(i)
        instance.setTryBlock(block)

        x.statements.add(instance)
        declaredStatement = instance
    }

    fun catch(catch: String, i: JavaBlockStatementDsl.() -> Unit) {
        if (declaredStatement == null || declaredStatement !is TryStmt) {
            throw RuntimeException()
        }
        val instance = declaredStatement as TryStmt

        if (instance.catchClauses == null) {
            instance.catchClauses = NodeList()
        }

        val block = BlockStmt()
        JavaBlockStatementDsl(block, cu).apply(i)

        instance.catchClauses.add(
            CatchClause()
                .setParameter(parseParameter(catch))
                .setBody(block)
        )
    }

    fun finally(i: JavaBlockStatementDsl.() -> Unit) {
        if (declaredStatement == null || declaredStatement !is TryStmt) {
            throw RuntimeException()
        }
        val instance = declaredStatement as TryStmt

        val block = BlockStmt()
        JavaBlockStatementDsl(block, cu).apply(i)

        instance.setFinallyBlock(block)

        declaredStatement = null
    }

    fun synchronized(synchronized: String, i: JavaBlockStatementDsl.() -> Unit) {
        if (declaredStatement != null) declaredStatement = null
        val instance = SynchronizedStmt()

        val block = BlockStmt()
        JavaBlockStatementDsl(block, cu).apply(i)

        instance.setExpression(synchronized)
        instance.setBody(block)
        x.statements.add(instance)
    }
}

@JavaDslMarker
class JavaNormalAnnotationUsageDsl(
    private val x: NormalAnnotationExpr,
    private val cu: CompilationUnit
) {
    fun import(import: String) {
        cu.addImport(import)
    }

    fun value(vararg value: String) {
        arg("value", *value)
    }

    fun arg(arg: String, vararg values: String) {
        if (values.size == 1) {
            x.addPair(arg, values[0])
        } else {
            val instance = ArrayInitializerExpr()
            for (element in values) {
                instance.values.add(parseExpression(element))
            }
            x.addPair(arg, instance)
        }
    }
}

fun java(i: JavaDsl.() -> Unit): JavaFile {
    val dsl = JavaDsl().apply(i)
    return JavaFile(dsl)
}