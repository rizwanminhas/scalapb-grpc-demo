package com.rminhas.server

import com.rminhas.generated.proto.clock.ClockGrpc
import com.rminhas.service.ClockService
import io.grpc.netty.NettyServerBuilder
import scala.concurrent.ExecutionContext

object ClockServer extends App {
  val service = ClockGrpc.bindService(new ClockService(), ExecutionContext.global)
  val server = NettyServerBuilder.forPort(9000).addService(service).build()
  server.start()

  Runtime.getRuntime.addShutdownHook(new Thread() {
    override def run(): Unit = server.shutdown()
  })

  server.awaitTermination()

}
