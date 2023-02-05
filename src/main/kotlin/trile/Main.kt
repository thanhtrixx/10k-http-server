package trile

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelOption
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import trile.util.addShutdownHook
import trile.util.createChannelInitializer
import trile.util.createEventLoopGroup
import trile.util.createServerSocketChannel

fun main() {

  val bossGroup = createEventLoopGroup()
  val workerGroup = createEventLoopGroup()
//  val coroutineScope = CoroutineScope(Executors.newFixedThreadPool(32).asCoroutineDispatcher())
  val coroutineScope = CoroutineScope(Dispatchers.IO)
  val capacity = 16384
  val counter = AtomicLong()

  addShutdownHook(bossGroup, workerGroup)

  val requestChannel = Channel<RequestHolder>(capacity)
  val requestMap = ConcurrentHashMap<String, RequestHolder>(capacity)

  val handler = CoroutineRequestReceiver(coroutineScope, requestChannel, requestMap, counter)
  CoroutineRequestProcessor(coroutineScope, requestChannel, requestMap, counter).process()

  try {
    val server = ServerBootstrap()
      .group(bossGroup, workerGroup)
      .channel(createServerSocketChannel())
      .childHandler(createChannelInitializer(handler))
      .childOption(ChannelOption.SO_SNDBUF, 1024 * 1024)
      .childOption(ChannelOption.SO_RCVBUF, 32 * 1024)
      .bind(8080)
      .sync()
      .channel()
    println("Server started at 8080")
    server.closeFuture().await()
  } finally {
    bossGroup.shutdownGracefully()
    workerGroup.shutdownGracefully()
  }

}
