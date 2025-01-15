package cn.luoym.bookreader.skylarkreader.jcef

import cn.luoym.bookreader.skylarkreader.book.EpubBook
import cn.luoym.bookreader.skylarkreader.properties.Bookshelves
import cn.luoym.bookreader.skylarkreader.properties.Constants
import cn.luoym.bookreader.skylarkreader.properties.SettingsProperties
import com.helger.css.ECSSVersion
import com.helger.css.decl.CSSExpression
import com.helger.css.decl.CSSExpressionMemberTermSimple
import com.helger.css.decl.CSSStyleRule
import com.helger.css.reader.CSSReader
import com.helger.css.writer.CSSWriter
import com.intellij.openapi.diagnostic.logger
import com.intellij.util.ui.UIUtil
import io.documentnode.epub4j.domain.MediaTypes
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.QueryStringDecoder
import org.jetbrains.ide.HttpRequestHandler
import org.jetbrains.io.response
import org.jetbrains.io.send
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.ParseSettings
import org.jsoup.parser.Parser

class EpubResourceStaticServer : HttpRequestHandler() {

    companion object {
        private val logger = logger<EpubResourceStaticServer>()
    }

    override fun process(
        urlDecoder: QueryStringDecoder,
        request: FullHttpRequest,
        context: ChannelHandlerContext
    ): Boolean {
        val bookshelves = Bookshelves.instance
        val path = urlDecoder.path()
        if (!path.contains(Constants.PLUGIN_PATH)) {
            return false
        }
        val requestPath = path.removePrefix("/${Constants.PLUGIN_PATH}/")
        logger.info("Epub resource path: $requestPath")

        return when {
            requestPath.startsWith(Constants.LOCAL_RESOURCE) -> sendLocalResource(requestPath, request, context)
            else -> {
                val (id, resourcePath) = parseRequestPath(requestPath) ?: return false
                val book = bookshelves.findBookById(id) as? EpubBook ?: return false
                serveEpubResource(book, resourcePath, request, context)
            }
        }
    }

    private fun parseRequestPath(requestPath: String): Pair<Long, String>? {
        val idEndIndex = requestPath.indexOf('/')
        return if (idEndIndex == -1) null else {
            val id = requestPath.substring(0, idEndIndex).toLongOrNull() ?: return null
            val resourcePath = requestPath.substring(idEndIndex + 1)
            id to resourcePath
        }
    }

    private fun serveEpubResource(
        book: EpubBook,
        resourcePath: String,
        request: FullHttpRequest,
        context: ChannelHandlerContext,
    ): Boolean {
        val resource = book.staticResource(resourcePath) ?: run {
            HttpResponseStatus.NOT_FOUND.send(context.channel(), request)
            return true
        }
        val contentType = resource.mediaType.name
        var bytes = resource.data
        val settings = SettingsProperties.instance

        bytes = when (resource.mediaType) {
            MediaTypes.XHTML -> addStyle(bytes, settings)
            MediaTypes.CSS -> changeCSSFontStyle(bytes, settings)
            else -> bytes
        }

        sendResponse(bytes, contentType, request, context)
        return true
    }

    fun sendResponse(bytes: ByteArray, contentType: String, request: FullHttpRequest, context: ChannelHandlerContext) {
        val resultBuffer = Unpooled.wrappedBuffer(bytes)
        val response = response(contentType, resultBuffer)
        response.send(context.channel(), request)
    }

    fun sendLocalResource(requestPath: String, request: FullHttpRequest, context: ChannelHandlerContext): Boolean {
        val resourcePath = requestPath.removePrefix(Constants.LOCAL_RESOURCE)
        val bytes = this.javaClass.classLoader.getResourceAsStream(resourcePath)?.use { it.readBytes() }
        if (bytes == null) {
            HttpResponseStatus.NOT_FOUND.send(context.channel(), request)
            return true
        }
        val mediaType = MediaTypes.determineMediaType(resourcePath)
        sendResponse(bytes, mediaType.name, request, context)
        return true
    }

    fun addStyle(data: ByteArray, settings: SettingsProperties): ByteArray {
        var html = String(data, Charsets.UTF_8)
        if (settings.overrideEpubFontFamily) {
            html = html.replace(Constants.FONT_FAMILY_NAME, "font-family-old", true)
        }
        if (settings.overrideEpubFontSize) {
            html = html.replace(Constants.FONT_SIZE_NAME, "font-size-old", true)
        }

        val document = Jsoup.parse(html, "", Parser.htmlParser().settings(ParseSettings(false, false)))
        document.body().attr("style", style())
        val nodes = Parser.parseXmlFragment(Constants.SCROLLBAR_CSS, "")
        document.head().appendChildren(nodes)
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml)
        return document.html().toByteArray(Charsets.UTF_8)
    }

    fun changeCSSFontStyle(data: ByteArray, settings: SettingsProperties): ByteArray {
        val cssFile = String(data, Charsets.UTF_8)
        val styleSheet = CSSReader.readFromString(cssFile, ECSSVersion.LATEST) ?: return data

        styleSheet.allRules.forEach { rule ->
            if (rule is CSSStyleRule) {
                rule.allDeclarations.forEach { declaration ->
                    when {
                        declaration.property.startsWith(Constants.FONT_FAMILY_NAME) && settings.overrideEpubFontFamily -> {
                            declaration.setExpression(CSSExpression().addMember(CSSExpressionMemberTermSimple("'${settings.fontFamily}'")))
                        }

                        declaration.property.startsWith(Constants.FONT_SIZE_NAME) && settings.overrideEpubFontSize -> {
                            val member = declaration.expression.getMemberAtIndex(0)
                            if (member is CSSExpressionMemberTermSimple && member.value.endsWith(Constants.FONT_SIZE_SUFFIX)) {
                                member.value = "${settings.fontSize}${Constants.FONT_SIZE_SUFFIX}"
                            }
                        }
                    }
                }
            }
        }

        val cssWriter = CSSWriter()
        return cssWriter.getCSSAsString(styleSheet).toByteArray(Charsets.UTF_8)
    }

    fun style(): String {
        val background = UIUtil.getPanelBackground()
        val foreground = UIUtil.getLabelForeground()
        val settings = SettingsProperties.instance
        // language=CSS
        return "background-color: rgba(${background.red}, ${background.green}, ${background.blue}, ${background.alpha});" +
                "color: rgba(${foreground.red}, ${foreground.green}, ${foreground.blue}, ${foreground.alpha});" +
                "font-family: '${settings.fontFamily}';" +
                "font-size: ${settings.fontSize}px;"
    }
}