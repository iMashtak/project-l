package io.github.imashtak.projectl.langs.java

class JavaDefinition(
    var packageName: String = "",
    var imports: MutableSet<String> = HashSet(),
    var classes: MutableList<JavaClass> = ArrayList()
)

class JavaClass(
    val name: String,
    val kind: JavaKind,
    var visibility: JavaVisibility = JavaVisibility.PUBLIC,
    var fields: MutableList<JavaField> = ArrayList(),
    var methods: MutableList<JavaMethod> = ArrayList()
)

enum class JavaKind {
    CLASS, INTERFACE, ENUM, ANNOTATION
}

class JavaField(
    val name: String,
    var visibility: JavaVisibility = JavaVisibility.PACKAGE_PRIVATE,
    var static: Boolean = false,
    var type: String = "Object",
    var initializer: JavaExpression? = null
)

class JavaMethod(
    val name: String,
    var visibility: JavaVisibility = JavaVisibility.PACKAGE_PRIVATE,
    var static: Boolean = false,
    var args: MutableList<JavaMethodArg> = ArrayList(),
    var body: JavaBlockStatement? = null
)

class JavaMethodArg(
    var name: String,
    var type: String = "Object"
)

enum class JavaVisibility {
    PUBLIC, PRIVATE, PROTECTED, PACKAGE_PRIVATE
}

interface JavaStatement

class JavaRawStatement(
    var statement: String
): JavaStatement

class JavaBlockStatement(
    var statements: MutableList<JavaStatement> = ArrayList()
) : JavaStatement

class JavaVariableDeclarationStatement(
    val name: String,
    var final: Boolean = false,
    var type: String = "var",
    var expression: JavaExpression? = null
) : JavaStatement

class JavaIfStatement(
    var condition: JavaExpression,
    var body: JavaBlockStatement
): JavaStatement

class JavaElseIfStatement(
    var condition: JavaExpression,
    var body: JavaBlockStatement
): JavaStatement

class JavaElseStatement(
    var body: JavaBlockStatement
): JavaStatement

class JavaExpression(
    var expr: String
)
