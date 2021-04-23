package com.rminhas.server

import com.rminhas.generated.proto.hello.GreeterGrpc
import com.rminhas.service.GreeterService
import scala.concurrent.ExecutionContext
import io.grpc.netty.NettyServerBuilder

object HelloWorldServer extends App {
  val service = GreeterGrpc.bindService(new GreeterService(), ExecutionContext.global)
  val server = NettyServerBuilder.forPort(9000).addService(service).build()
  server.start()

  Runtime.getRuntime.addShutdownHook(new Thread() {
    override def run(): Unit = server.shutdown()
  })

  server.awaitTermination()
}
