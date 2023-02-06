package trile

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelOption
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import trile.util.addShutdownHook
import trile.util.createChannelInitializer
import trile.util.createEventLoopGroup
import trile.util.createServerSocketChannel

fun main() {

  val bossGroup = createEventLoopGroup()
  val workerGroup = createEventLoopGroup()
//  val dispatcher = Executors.newFixedThreadPool(256).asCoroutineDispatcher()
  val dispatcher = Dispatchers.IO
  val coroutineScope = CoroutineScope(dispatcher)
  val counter = AtomicLong()

  addShutdownHook(bossGroup, workerGroup)

  val handler = CoroutineRequestHandler(coroutineScope, counter)

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
