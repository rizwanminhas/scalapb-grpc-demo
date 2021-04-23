package com.rminhas.client

import com.rminhas.generated.proto.hello.{GreeterGrpc, HelloRequest}
import io.grpc.netty.{NegotiationType, NettyChannelBuilder}
import io.grpc.Channel
import scala.concurrent.ExecutionContext.Implicits.global

object HelloWorldClient extends App {
  val channel: Channel = NettyChannelBuilder.forAddress("localhost", 9000).negotiationType(NegotiationType.PLAINTEXT).build()

  val request = HelloRequest("rizwan")
  val stub        = GreeterGrpc.stub(channel)

  val greetingFuture = stub.sayHello(request)
  greetingFuture.foreach(response => println(s"Async server response: ${response.message}"))

  val blockingStub = GreeterGrpc.blockingStub(channel)
  val greeting                   = blockingStub.sayHello(request)
  println(s"Sync server response: ${greeting.message}")
}
