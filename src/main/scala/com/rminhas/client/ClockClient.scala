package com.rminhas.client

import com.rminhas.generated.proto.clock.{ClockGrpc, TimeRequest, TimeResponse}
import io.grpc.netty.{NegotiationType, NettyChannelBuilder}
import io.grpc.{ManagedChannel}
import io.grpc.stub.StreamObserver
import java.util.concurrent.TimeUnit
import scala.concurrent.{Await, Promise}
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success, Try}

object ClockClient extends App {
  val channel: ManagedChannel = NettyChannelBuilder.forAddress("localhost", 9000).negotiationType(NegotiationType.PLAINTEXT).build()

  val client  = ClockGrpc.stub(channel)

  def observer(p: Promise[String]): StreamObserver[TimeResponse] =
    new StreamObserver[TimeResponse] {
      def onError(t: Throwable): Unit = {
        println(s"ON_ERROR: $t")
        p.complete(tryAwaitTermination(channel, "received onError"))
      }
      def onCompleted(): Unit = {
        println("ON_COMPLETED")
        p.complete(tryAwaitTermination(channel, "received onComplete"))
      }
      def onNext(response: TimeResponse): Unit =
        println(s"ON_NEXT: Received current time ms: ${response.currentTime}")
    }

  val terminated = Promise[String]
  client.streamTime(TimeRequest(), observer(terminated))

  val shutdownReason = Await.result(terminated.future, Duration.Inf)
  println(s"Successfully shutdown application. Reason: $shutdownReason")

  private def tryAwaitTermination(channel: ManagedChannel, reason: String): Try[String] = {
    channel.shutdown()
    if (channel.awaitTermination(5, TimeUnit.SECONDS)) {
      Success(reason)
    } else {
      Failure(new RuntimeException("Could not terminate channel."))
    }
  }
}
