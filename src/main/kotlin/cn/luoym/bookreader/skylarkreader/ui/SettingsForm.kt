package cn.luoym.bookreader.skylarkreader.ui

import cn.luoym.bookreader.skylarkreader.properties.SettingProperties
import cn.luoym.bookreader.skylarkreader.utils.ReaderBundle
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.bindIntText
import com.intellij.ui.dsl.builder.bindItem
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel
import java.awt.BorderLayout
import java.awt.GraphicsEnvironment
import javax.swing.JPanel

class SettingsForm : JPanel() {

    private val settings
        get() = SettingProperties.instance

    private val properties = PropertyGraph()

    var fontFamily = properties.property(settings.fontFamily)

    var fontSize = properties.property(settings.fontSize)

    var pageSize = properties.property(settings.pageSize)

    var autoTurnPage = properties.property(settings.autoTurnPage)

    var overrideEpubFontFamily = properties.property(settings.overrideEpubFontFamily)

    var overrideEpubFontSize = properties.property(settings.overrideEpubFontSize)

    private val fontStyleGroup = panel {
        group("Font Style") {
            row(ReaderBundle.message("skylark.reader.settings.font.family")) {
                comboBox(GraphicsEnvironment.getLocalGraphicsEnvironment().availableFontFamilyNames.asList())
                    .bindItem(fontFamily)
            }
            row(ReaderBundle.message("skylark.reader.settings.font.size")) {
                intTextField(8..30, 1)
                    .bindIntText(fontSize)
            }
        }
    }

    private val texConsoleGroup = panel {
        group("Text Reader") {
            row {
                checkBox(ReaderBundle.message("skylark.reader.settings.auto.turn.page"))
                    .bindSelected(autoTurnPage)
            }
            row(ReaderBundle.message("skylark.reader.settings.page.size")) {
                intTextField(1000..10000, 500)
                    .bindIntText(pageSize)
                    .comment(ReaderBundle.message("skylark.reader.settings.page.size.comment"))
            }
        }
    }

    private val epubGroup = panel {
        group("Epub Reader") {
            row {
                checkBox(ReaderBundle.message("skylark.reader.settings.override.epub.font.family"))
                    .bindSelected(overrideEpubFontFamily)
                    .comment(ReaderBundle.message("skylark.reader.settings.override.epub.font.family.comment"))
            }
            row {
                checkBox(ReaderBundle.message("skylark.reader.settings.override.epub.font.size"))
                    .bindSelected(overrideEpubFontSize)
                    .comment(ReaderBundle.message("skylark.reader.settings.override.epub.font.size.comment"))
            }
        }
    }

    init {
        layout = BorderLayout()
        add(panel {
            row { cell(fontStyleGroup).align(AlignX.FILL) }
            row { cell(texConsoleGroup).align(AlignX.FILL) }
            row { cell(epubGroup).align(AlignX.FILL) }
        })
    }

    fun reset(){
        fontFamily.set(settings.fontFamily)
        fontSize.set(settings.fontSize)
        pageSize.set(settings.pageSize)
        autoTurnPage.set(settings.autoTurnPage)
        overrideEpubFontFamily.set(settings.overrideEpubFontFamily)
        overrideEpubFontSize.set(settings.overrideEpubFontSize)
    }
}