package cn.luoym.bookreader.skylarkreader.ui

import cn.luoym.bookreader.skylarkreader.properties.SettingsProperties
import cn.luoym.bookreader.skylarkreader.properties.TextReaderUIEnum
import cn.luoym.bookreader.skylarkreader.utils.ReaderBundle
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.ui.SimpleListCellRenderer
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.bindIntText
import com.intellij.ui.dsl.builder.bindItem
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.layout.ComponentPredicate
import java.awt.BorderLayout
import java.awt.GraphicsEnvironment
import javax.swing.DefaultComboBoxModel
import javax.swing.JPanel

class SettingsForm : JPanel() {

    private val settings
        get() = SettingsProperties.instance

    private val properties = PropertyGraph()

    var fontFamily = properties.property(settings.fontFamily)

    var fontSize = properties.property(settings.fontSize)

    var pageSize = properties.property(settings.pageSize)

    var autoTurnPage = properties.property(settings.autoTurnPage)

    var overrideEpubFontFamily = properties.property(settings.overrideEpubFontFamily)

    var overrideEpubFontSize = properties.property(settings.overrideEpubFontSize)

    var textReaderUI = properties.property(settings.textReaderUI)

    var widgetPageSize = properties.property(settings.widgetPageSize)


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

            row(ReaderBundle.message("skylark.reader.settings.text.reader.show.ui")) {
                var cellRenderer = SimpleListCellRenderer.create<TextReaderUIEnum> { label, value, _ ->
                    label.text = when (value) {
                        TextReaderUIEnum.CONSOLE -> ReaderBundle.message("skylark.reader.settings.text.show.console")
                        TextReaderUIEnum.STATUS_BAR_WIDGET -> ReaderBundle.message("skylark.reader.settings.text.show.status.bar.widget")
                    }
                }
                comboBox(DefaultComboBoxModel(TextReaderUIEnum.entries.toTypedArray()), cellRenderer)
                    .bindItem(textReaderUI)
            }
            val consolePredicate = ShowReadUIComponentPredicate(TextReaderUIEnum.CONSOLE)
            row {
                checkBox(ReaderBundle.message("skylark.reader.settings.text.console.auto.turn.page"))
                    .bindSelected(autoTurnPage)
                    .enabledIf(consolePredicate)
            }
            row(ReaderBundle.message("skylark.reader.settings.text.console.page.size")) {
                intTextField(1000..50000, 1)
                    .bindIntText(pageSize)
                    .comment(ReaderBundle.message("skylark.reader.settings.text.console.page.size.comment"))
                    .enabledIf(consolePredicate)
            }
            row(ReaderBundle.message("skylark.reader.settings.text.widget.page.size")) {
                intTextField(50..1000, 1)
                    .bindIntText(widgetPageSize)
                    .comment(ReaderBundle.message("skylark.reader.settings.text.widget.page.size.comment"))
                    .enabledIf(ShowReadUIComponentPredicate(TextReaderUIEnum.STATUS_BAR_WIDGET))
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
        textReaderUI.set(settings.textReaderUI)
        widgetPageSize.set(settings.widgetPageSize)
    }

    inner class ShowReadUIComponentPredicate(val textReaderUIEnum: TextReaderUIEnum) : ComponentPredicate() {
        override fun addListener(listener: (Boolean) -> Unit) {
            return textReaderUI.afterChange { listener(it == textReaderUIEnum) }
        }

        override fun invoke(): Boolean {
            return textReaderUI.get() == textReaderUIEnum
        }
    }
}