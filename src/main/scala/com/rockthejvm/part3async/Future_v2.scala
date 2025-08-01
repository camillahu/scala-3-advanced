package com.rockthejvm.part3async

import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object Future_v2 {

  def calculateMeaningOfLife(): Int = {
    //simulate long compute
    Thread.sleep(1000)
    42
  }

  //thread pool Java-specific
  val executor = Executors.newFixedThreadPool(4)

  //thread pool Scala-specific
  given executionContext: ExecutionContext = ExecutionContext.fromExecutorService(executor)

  //a future = an async computation that will finish at some point
  val aFuture: Future[Int] = Future.apply(calculateMeaningOfLife())(executionContext)
  val futureInstantResult: Option[Try[Int]] = aFuture.value //may not have finished yet

  //callbacks
  aFuture.onComplete {
    case Success(value) => println(s"completed with: $value")
    case Failure(exception) => println(s"failed with $exception")
  }



  def main(args: Array[String]): Unit = {

  }

}
