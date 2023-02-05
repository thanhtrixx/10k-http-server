package trile

import io.netty.buffer.Unpooled
import io.netty.handler.codec.http.DefaultFullHttpResponse
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.HttpVersion
import io.netty.util.AsciiString
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import trile.util.Log


class CoroutineRequestProcessor(
  private val coroutineScope: CoroutineScope,
  private val requestChannel: Channel<RequestHolder>,
  private val requestMap: ConcurrentHashMap<String, RequestHolder>,
  private val counter: AtomicLong
) : Log {

  private val CONTENT_TYPE = AsciiString.cached("Content-Type")
  private val CONTENT_TYPE_TEXT_PLAIN = AsciiString.cached("text/plain")
  private val CONTENT_LENGTH = AsciiString.cached("Content-Length")


  fun process() {
    coroutineScope.launch {
      for (request in requestChannel) {

        val response = DefaultFullHttpResponse(
          HttpVersion.HTTP_1_1,
          HttpResponseStatus.OK,
          Unpooled.wrappedBuffer("Hello from HttpServerHandler".toByteArray())
        )

        response.headers().set(CONTENT_TYPE, CONTENT_TYPE_TEXT_PLAIN)
        response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes())

        val context = request.context
        val message = request.message

        l.info { "process message.uri ${message.uri()} id ${request.id}" }

        if (message.uri().startsWith("/delay")) {
          delay(60)
          l.info { "resume id ${request.id}" }
        }

        context.writeAndFlush(response)
        context.close()
        l.info { "id ${request.id}, counter ${counter.decrementAndGet()}" }
      }
    }
  }
}
