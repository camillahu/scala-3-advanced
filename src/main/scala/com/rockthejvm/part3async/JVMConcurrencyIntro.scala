package com.rockthejvm.part3async

import java.util.concurrent.Executors

object JVMConcurrencyIntro {

  def basicThreads(): Unit = {
    val runnable = new Runnable {
      override def run(): Unit = {
        println("waiting...")
        Thread.sleep(2000)
        println("running on some thread")
      }
    }

    //threads on the JVM
    val aThread = new Thread(runnable)

    //this executes the code in runnable without calling it in main because the thread's internal logic executes it:
    aThread.start()
    //JVM thread == OS thread (soon to change via Project Loom)

    //you can block a thread until the computation finishes:
    aThread.join()
  }

  //order of operations is NOT guaranteed
  //different runs give different results. Hello and Goodbye are scrambled:
  def orderOfExecution(): Unit = {
    val threadHello = new Thread(() => (1 to 5).foreach(_ => println("hello")))
    val threadGoodbye = new Thread(() => (1 to 5).foreach(_ => println("goodbye")))
    threadHello.start()
    threadGoodbye.start()
  }

  //executors -- manage the lifecycle of the thread pool for us
  def demoExecutors(): Unit = {
    val threadPool = Executors.newFixedThreadPool(4)
    // submit a computation
    threadPool.execute(() => println("something in the thread pool"))

    //shutdown is needed, or thread pool will be held open.
//    threadPool.shutdown()
//    threadPool.execute(() => println("this should NOT appear")) //should throw an exception because it's after shutdown.

    threadPool.execute { () =>
      Thread.sleep(1000)
      println("done after one second")
    }

    threadPool.execute {() =>
      Thread.sleep(1000)
      println("almost done")
      Thread.sleep(1000)
      println("done after two second")
    }
  }

  def main(args: Array[String]): Unit = {
    demoExecutors()
  }
}
