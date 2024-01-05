package io.github.imashtak.projectl.dsl

import java.io.OutputStream

interface LangSource<Settings, AST> {

    fun dump(out: OutputStream, settingsInitializer: Settings.() -> Unit = {})

    fun ast(): AST
}