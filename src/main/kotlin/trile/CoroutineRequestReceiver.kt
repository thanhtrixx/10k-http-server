package trile

import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.HttpRequest
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import trile.util.Log

@ChannelHandler.Sharable
class CoroutineRequestReceiver(
  private val coroutineScope: CoroutineScope,
  private val requestChannel: Channel<RequestHolder>,
  private val requestMap: ConcurrentHashMap<String, RequestHolder>,
  private val counter: AtomicLong
) : SimpleChannelInboundHandler<HttpRequest>(), Log {

  override fun channelRead0(context: ChannelHandlerContext, message: HttpRequest) {
    val id = UUID.randomUUID().toString()
    val holder = RequestHolder(id, context, message)
//    requestMap[id] = holder
    l.info { "id $id, counter ${counter.incrementAndGet()}" }

    coroutineScope.launch { requestChannel.send(holder) }
  }
}

