package com.rminhas.service

import com.rminhas.generated.proto.hello.{GreeterGrpc, HelloReply, HelloRequest}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class GreeterService extends GreeterGrpc.Greeter {
  override def sayHello(request: HelloRequest): Future[HelloReply] = Future {
    HelloReply(message = s"Welcome, ${request.name}!")
  }
}
