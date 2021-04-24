package com.rminhas.service

import com.rminhas.generated.proto.clock.{ClockGrpc, TimeRequest, TimeResponse}
import io.grpc.stub.StreamObserver

import java.util.concurrent.{Executors, TimeUnit}
import java.util.concurrent.atomic.AtomicInteger

class ClockService extends ClockGrpc.Clock {
  override def streamTime(request: TimeRequest, responseObserver: StreamObserver[TimeResponse]): Unit = {
    val scheduler = Executors.newSingleThreadScheduledExecutor()
    val tick = new Runnable {
      val counter = new AtomicInteger(5)
      def run(): Unit =
        if (counter.getAndDecrement() >= 0) {
          val currentTime = System.currentTimeMillis()
          responseObserver.onNext(TimeResponse(currentTime))
        } else {
          scheduler.shutdown()
          responseObserver.onCompleted()
        }
    }
    scheduler.scheduleAtFixedRate(tick, 0l, 1000l, TimeUnit.MILLISECONDS)
  }
}
