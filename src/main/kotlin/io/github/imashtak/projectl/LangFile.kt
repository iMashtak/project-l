package io.github.imashtak.projectl

import java.io.OutputStream

interface LangFile<TSettings> {

    fun dump(out: OutputStream, settingsInitializer: TSettings.() -> Unit = {})
}