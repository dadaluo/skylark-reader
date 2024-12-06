package cn.luoym.bookreader.skylarkreader.jcef

import cn.luoym.bookreader.skylarkreader.book.EpubBook
import cn.luoym.bookreader.skylarkreader.properties.Bookshelves
import cn.luoym.bookreader.skylarkreader.properties.Constants
import cn.luoym.bookreader.skylarkreader.properties.SettingProperties
import com.intellij.openapi.application.ApplicationManager
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
import org.jsoup.nodes.DataNode
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
        val bookshelves = ApplicationManager.getApplication().getService<Bookshelves>(Bookshelves::class.java)
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
            if (MediaTypes.XHTML.equals(resource.mediaType)) {
                bytes = rebuildHtml(bytes)
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


    fun rebuildHtml(data: ByteArray): ByteArray {
        val html = String(data, Charsets.UTF_8)
        val htmlParser = Parser.htmlParser()
        htmlParser.settings(ParseSettings(false, false))
        val document = htmlParser.parseInput(html, "")
        val body = document.body()
        body.attr("style", style())
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        val head = document.getElementsByTag("head")
        if (head.isNotEmpty()) {
            val nodes = Parser.parseXmlFragment(Constants.SCROLLBAR_CSS, "")
            head[0].appendChildren(nodes)
        }
        return document.html().toByteArray(Charsets.UTF_8)
    }


    fun style(): String {
        val background = UIUtil.getPanelBackground()
        val foreground = UIUtil.getLabelForeground()
        val settings = ApplicationManager.getApplication().getService<SettingProperties>(SettingProperties::class.java)
        return "background-color: rgba(${background.red}, ${background.green}, ${background.blue}, ${background.alpha});" +
                " color: rgba(${foreground.red}, ${foreground.green}, ${foreground.blue}, ${foreground.alpha});" +
                "font-family: '${settings.fontFamily}';" +
                "font-size: ${settings.fontSize}px;"
    }
}