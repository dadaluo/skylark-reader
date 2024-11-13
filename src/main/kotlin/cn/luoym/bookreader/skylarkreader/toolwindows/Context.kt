package cn.luoym.bookreader.skylarkreader.toolwindows

import com.intellij.openapi.components.Service

@Service
class Context {
    lateinit var readerToolWindowFactory: ReaderToolWindowFactory
}