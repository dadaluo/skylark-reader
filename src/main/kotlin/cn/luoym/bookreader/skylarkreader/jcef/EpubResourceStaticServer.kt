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
import org.jsoup.nodes.Document
import org.jsoup.parser.ParseSettings
import org.jsoup.parser.Parser

class EpubResourceStaticServer: HttpRequestHandler() {

    companion object {
        private val logger = logger<EpubResourceStaticServer>()
    }

    override fun process(
        urlDecoder: QueryStringDecoder,
        request: FullHttpRequest,
        context: ChannelHandlerContext
    ): Boolean {
        val bookshelves =Bookshelves.instance
        if (!urlDecoder.path().contains(Constants.PLUGIN_NAME)) {
            logger.info("not epub resource path. ${urlDecoder.path()}")
            return false
        }
        val requestPath = urlDecoder.path().removePrefix("/${Constants.PLUGIN_NAME}/")
        logger.info("epub resource path: $requestPath")
        if (requestPath.startsWith(Constants.LOCAL_RESOURCE)) {
            return sendLocalResource(requestPath, request, context)
        }
        val id = requestPath.substring(0, requestPath.indexOf('/'))
        val resourcePath = requestPath.removePrefix("$id/")
        val book = bookshelves.findBookById(id.toLong())
        if (book != null && book is EpubBook) {
            val resource = book.staticResource(resourcePath)
            if (resource == null) {
                HttpResponseStatus.NOT_FOUND.send(context.channel(), request)
                return true
            }
            val contentType = resource.mediaType.name
            var bytes = resource.data
            val settings =
                SettingsProperties.instance
            if (MediaTypes.XHTML.equals(resource.mediaType)) {
                bytes = addStyle(bytes, settings)
            } else if (MediaTypes.CSS.equals(resource.mediaType) && (settings.overrideEpubFontFamily || settings.overrideEpubFontSize)) {
                bytes = changeCSSFontStyle(bytes, settings)
            }
            sendResponse(bytes, contentType, request, context)
            return true
        }
        return false
    }

    fun sendResponse(bytes: ByteArray, contentType: String, request: FullHttpRequest, context: ChannelHandlerContext) {
        val resultBuffer = Unpooled.wrappedBuffer(bytes)
        val response = response(contentType, resultBuffer)
        response.send(context.channel(), request)
    }

    fun sendLocalResource(requestPath: String, request: FullHttpRequest, context: ChannelHandlerContext): Boolean {
        val resourcePath = requestPath.removePrefix(Constants.LOCAL_RESOURCE)
        val bytes = this.javaClass.classLoader.getResourceAsStream(resourcePath)?.use {
            it.readBytes()
        }
        if (bytes == null) {
            HttpResponseStatus.NOT_FOUND.send(context.channel(), request)
            return true
        }
        val mediaType = MediaTypes.determineMediaType(requestPath)
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
        val htmlParser = Parser.htmlParser()
        htmlParser.settings(ParseSettings(false, false))
        val document = htmlParser.parseInput(html, "")
        val body = document.body()
        body.attr("style", style())
        val head = document.getElementsByTag("head")
        if (head.isNotEmpty()) {
            val nodes = Parser.parseXmlFragment(Constants.SCROLLBAR_CSS, "")
            head[0].appendChildren(nodes)
        }
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        return document.html().toByteArray(Charsets.UTF_8)
    }

    fun changeCSSFontStyle(data: ByteArray, settings: SettingsProperties): ByteArray {
        var cssFile = String(data, Charsets.UTF_8)
        val styleSheet = CSSReader.readFromString(cssFile, ECSSVersion.LATEST)
        if (styleSheet == null) {
            return data
        }
        val rules = styleSheet.allRules
        rules.forEach { rule ->
            if (rule is CSSStyleRule) {
                val declarations = rule.allDeclarations
                declarations.forEach { declaration ->
                    if (declaration.property.startsWith(Constants.FONT_FAMILY_NAME) && settings.overrideEpubFontFamily) {
                        val expression = CSSExpression()
                        val term = CSSExpressionMemberTermSimple("'${settings.fontFamily}'")
                        expression.addMember(term)
                        declaration.setExpression(expression)
                    } else if (declaration.property.startsWith(Constants.FONT_SIZE_NAME) && settings.overrideEpubFontSize) {
                        val expression = declaration.expression
                        val member = expression.getMemberAtIndex(0)
                        if (member != null && member is CSSExpressionMemberTermSimple && member.value.endsWith(Constants.FONT_SIZE_SUFFIX)) {
                            member.value = "${settings.fontSize}${Constants.FONT_SIZE_SUFFIX}"
                        }
                    }
                }
            }
        }
        val cssWriter = CSSWriter()
        val cssAsString = cssWriter.getCSSAsString(styleSheet)
        return cssAsString.toByteArray(Charsets.UTF_8)
    }


    fun style(): String {
        val background = UIUtil.getPanelBackground()
        val foreground = UIUtil.getLabelForeground()
        val settings = SettingsProperties.instance
        return "background-color: rgba(${background.red}, ${background.green}, ${background.blue}, ${background.alpha});" +
                " color: rgba(${foreground.red}, ${foreground.green}, ${foreground.blue}, ${foreground.alpha});" +
                "font-family: '${settings.fontFamily}';" +
                "font-size: ${settings.fontSize}px;"
    }
}