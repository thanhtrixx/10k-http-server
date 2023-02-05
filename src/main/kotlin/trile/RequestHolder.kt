package trile

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.HttpRequest

data class RequestHolder(
  val id: String,
  val context: ChannelHandlerContext,
  val message: HttpRequest
)

