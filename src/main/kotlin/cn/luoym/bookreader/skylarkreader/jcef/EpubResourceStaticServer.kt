package cn.luoym.bookreader.skylarkreader.jcef

import cn.luoym.bookreader.skylarkreader.book.EpubBook
import cn.luoym.bookreader.skylarkreader.toolwindows.Context
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.logger
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.QueryStringDecoder
import org.jetbrains.ide.HttpRequestHandler
import org.jetbrains.io.response
import org.jetbrains.io.send

class EpubResourceStaticServer: HttpRequestHandler() {
    companion object {
        private val logger = logger<EpubResourceStaticServer>()
    }

    override fun process(
        urlDecoder: QueryStringDecoder,
        request: FullHttpRequest,
        context: ChannelHandlerContext
    ): Boolean {
        val bookContext = ApplicationManager.getApplication().getService<Context>(Context::class.java)
        if (!urlDecoder.path().contains(bookContext.uuid)) {
            logger.info("not epub resource path. ${urlDecoder.path()}")
            return false
        }
        val requestPath = urlDecoder.path().removePrefix("/${bookContext.uuid}/")
        logger.info("epub resource path: $requestPath")
        val book = bookContext.currentBook
        if (book != null && book is EpubBook) {
            val resource = book.staticResource(requestPath)
            if (resource == null) {
                HttpResponseStatus.NOT_FOUND.send(context.channel(), request)
                return true
            }
            val contentType = resource.mediaType.name
            val resultBuffer = Unpooled.wrappedBuffer(resource.data)
            val response = response(contentType, resultBuffer)
            response.send(context.channel(), request)
            return true
        }
        return false
    }
}