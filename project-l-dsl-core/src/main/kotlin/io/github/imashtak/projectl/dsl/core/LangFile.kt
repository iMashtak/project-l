package io.github.imashtak.projectl

import java.io.OutputStream

interface LangFile<Settings, AST> {

    fun dump(out: OutputStream, settingsInitializer: Settings.() -> Unit = {})

    fun ast(): AST
}