package trile

import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.DefaultFullHttpResponse
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.HttpVersion
import io.netty.util.AsciiString
import java.util.UUID
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import trile.util.Log

@ChannelHandler.Sharable
class CoroutineRequestHandler(
  private val coroutineScope: CoroutineScope,
  private val counter: AtomicLong
) : SimpleChannelInboundHandler<HttpRequest>(), Log {

  private val CONTENT_TYPE = AsciiString.cached("Content-Type")
  private val CONTENT_TYPE_TEXT_PLAIN = AsciiString.cached("text/plain")
  private val CONTENT_LENGTH = AsciiString.cached("Content-Length")

  private val NOT_ACCEPTABLE = DefaultFullHttpResponse(
    HttpVersion.HTTP_1_1,
    HttpResponseStatus.NOT_ACCEPTABLE,
  ).apply {
    headers().set(CONTENT_TYPE, CONTENT_TYPE_TEXT_PLAIN)
    headers().setInt(CONTENT_LENGTH, this.content().readableBytes())
  }


  override fun channelRead0(context: ChannelHandlerContext, message: HttpRequest) {
    if (counter.get() >= 10_000) {
      context.writeAndFlush(NOT_ACCEPTABLE)
      context.close()
      return
    }

    val id = UUID.randomUUID().toString()

    l.info { "id $id, counter ${counter.incrementAndGet()}" }

    coroutineScope.launch {
      val response = DefaultFullHttpResponse(
        HttpVersion.HTTP_1_1,
        HttpResponseStatus.OK,
        Unpooled.wrappedBuffer("Hello from HttpServerHandler".toByteArray())
      )

      response.headers().set(CONTENT_TYPE, CONTENT_TYPE_TEXT_PLAIN)
      response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes())


      l.info { "id $id" }

      if (message.uri().startsWith("/delay")) {
        delay(30 * 1000)
      }

      context.writeAndFlush(response)
      context.close()
      l.info { "uri ${message.uri()} id ${id}, counter ${counter.decrementAndGet()}" }
    }
  }
}

