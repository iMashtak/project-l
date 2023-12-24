package io.github.imashtak.projectl.langs.java

class JavaDefinition(
    var packageName: String = "",
    var imports: MutableSet<String> = HashSet(),
    var types: MutableList<JavaType> = ArrayList()
)

interface JavaType

class JavaClass(
    val name: String,
    var visibility: JavaVisibility = JavaVisibility.PUBLIC,
    var fields: MutableList<JavaField> = ArrayList(),
    var methods: MutableList<JavaMethod> = ArrayList(),
    var annotations: MutableList<JavaAnnotationUsage> = ArrayList()
): JavaType

class JavaInterface(
    val name: String,
    var visibility: JavaVisibility = JavaVisibility.PUBLIC,
    var fields: MutableList<JavaField> = ArrayList(),
    var methods: MutableList<JavaMethod> = ArrayList(),
    var annotations: MutableList<JavaAnnotationUsage> = ArrayList()
): JavaType

class JavaAnnotation(
    val name: String,
    var visibility: JavaVisibility = JavaVisibility.PUBLIC,
    var fields: MutableList<JavaAnnotationField> = ArrayList(),
    var annotations: MutableList<JavaAnnotationUsage> = ArrayList()
): JavaType

class JavaEnum(
    val name: String,
    var visibility: JavaVisibility = JavaVisibility.PUBLIC,
    var values: MutableList<JavaEnumValue> = ArrayList(),
    var fields: MutableList<JavaField> = ArrayList(),
    var methods: MutableList<JavaMethod> = ArrayList(),
    var annotations: MutableList<JavaAnnotationUsage> = ArrayList()
): JavaType

class JavaField(
    val name: String,
    var visibility: JavaVisibility = JavaVisibility.PACKAGE_PRIVATE,
    var static: Boolean = false,
    var type: String = "Object",
    var initializer: JavaExpression? = null,
    var annotations: MutableList<JavaAnnotationUsage> = ArrayList()
)

class JavaAnnotationField(
    val name: String,
    var type: String = "String",
    var default: JavaExpression? = null
)

class JavaEnumValue(
    val name: String,
    val args: List<JavaExpression>
)

class JavaMethod(
    val name: String,
    var visibility: JavaVisibility = JavaVisibility.PACKAGE_PRIVATE,
    var static: Boolean = false,
    var default: Boolean = false,
    var args: MutableList<JavaMethodArg> = ArrayList(),
    var returns: String? = null,
    var body: JavaBlockStatement? = null,
    var annotations: MutableList<JavaAnnotationUsage> = ArrayList()
)

class JavaMethodArg(
    var name: String,
    var type: String = "Object"
)

enum class JavaVisibility {
    PUBLIC, PRIVATE, PROTECTED, PACKAGE_PRIVATE
}

class JavaAnnotationUsage(
    val name: String,
    var args: MutableList<JavaAnnotationArg> = ArrayList()
)

class JavaAnnotationArg(
    val name: String,
    val values: MutableList<JavaExpression>
)

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

class JavaForStatement(
    var starting: JavaExpression,
    var condition: JavaExpression,
    var ending: JavaExpression,
    var body: JavaBlockStatement
): JavaStatement

class JavaForEachStatement(
    var variable: String,
    var collection: JavaExpression,
    var body: JavaBlockStatement,
    var variableType: String = "var",
): JavaStatement

class JavaWhileStatement(
    var condition: String,
    var body: JavaBlockStatement
): JavaStatement

class JavaSwitchStatement(
    var obj: String,
    var cases: MutableList<JavaSwitchCase> = ArrayList(),
    var style: SwitchStyle = SwitchStyle.NEW
): JavaStatement

enum class SwitchStyle {
    OLD, NEW
}

class JavaSwitchCase(
    var match: String,
    var body: JavaBlockStatement? = null
)

class JavaReturnStatement(
    var expr: JavaExpression
): JavaStatement

class JavaBreakStatement : JavaStatement

class JavaContinueStatement: JavaStatement

class JavaExpression(
    var raw: String
)
