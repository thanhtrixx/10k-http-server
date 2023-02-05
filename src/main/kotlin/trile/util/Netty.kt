package trile.util

import io.netty.channel.ChannelInboundHandler
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoopGroup
import io.netty.channel.epoll.Epoll
import io.netty.channel.epoll.EpollEventLoopGroup
import io.netty.channel.epoll.EpollServerSocketChannel
import io.netty.channel.kqueue.KQueue
import io.netty.channel.kqueue.KQueueEventLoopGroup
import io.netty.channel.kqueue.KQueueServerSocketChannel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.ServerSocketChannel
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http.HttpServerCodec

fun createEventLoopGroup() = when {
  Epoll.isAvailable() -> EpollEventLoopGroup()
  KQueue.isAvailable() -> KQueueEventLoopGroup()
  else -> NioEventLoopGroup()
}

fun createServerSocketChannel() = when {
  Epoll.isAvailable() -> EpollServerSocketChannel::class
  KQueue.isAvailable() -> KQueueServerSocketChannel::class
  else -> NioServerSocketChannel::class
}.java as Class<out ServerSocketChannel>

private fun createShutdownThread(bossGroup: EventLoopGroup, workerGroup: EventLoopGroup) = Thread {
  bossGroup.shutdownGracefully()
  workerGroup.shutdownGracefully()
}

fun addShutdownHook(bossGroup: EventLoopGroup, workerGroup: EventLoopGroup) {
  Runtime.getRuntime().addShutdownHook(createShutdownThread(bossGroup, workerGroup))
}

fun createChannelInitializer(handler: ChannelInboundHandler) = object : ChannelInitializer<SocketChannel>() {
  override fun initChannel(ch: SocketChannel) {
    val pipeline = ch.pipeline()
    pipeline.addLast("codec", HttpServerCodec())
    pipeline.addLast("handler", handler)
  }
}

