package com.rockthejvm.part3async

import java.util.concurrent.Executors

object JVMConcurrencyProblems {

  def runInParallel(): Unit = {
    var x = 0

    val thread1 = new Thread(() => {
      x = 1
    })

    val thread2 = new Thread(() => {
      x = 2
    })

    thread1.start()
    thread2.start()
    println(x)
    //this is called a race condition - we don't have control over which thread finishes first--
    // the output can be either 1 or 2.
  }

  //can be controlled by synchronization:

  case class BankAccount(var amount: Int)

  def buy(bankAccount: BankAccount, thing: String, price: Int): Unit = {
    //this involves 3 steps:
    // - read old value
    // - compute result
    // - write new value
    bankAccount.amount -= price
  }

  def buySafe(bankAccount: BankAccount, thing: String, price: Int): Unit = {
    //not race condition because of synchronized.
    //does not allow the critical section to be run by multiple threads at the same time.
    //will block the running of the next thread until the first thread has finished the computation.
    bankAccount.synchronized {
      bankAccount.amount -= price //critical section
    }
  }

  def demoBankingProblem(): Unit = {
    (1 to 10000).foreach { _ =>
      val account = new BankAccount(50000)
      val thread1 = new Thread(() => buySafe(account, "shoes", 3000))
      val thread2 = new Thread(() => buySafe(account, "iPhone", 4000))
      thread1.start()
      thread2.start()
      thread1.join()
      thread2.join()
      if (account.amount != 42000) println(s"AHA! I've just broken the bank: ${account.amount}")
    }
  }

  //exercises


  def inceptionThreads(maxThreads: Int, i: Int): Thread = {
    new Thread(() => {
      if (i < maxThreads) {
        val newThread = inceptionThreads(maxThreads, i + 1)
        newThread.start()
        newThread.join()
      }
      println(s"hello from thread $i")
    })
  }

  def main(args: Array[String]): Unit = {
    inceptionThreads(10, 1).start()
  }
}

//  def inceptionThreads(n: Int): Unit = {
//    val threadPool = Executors.newFixedThreadPool(n)
//    val tasksReverse = (1 to n).map { t =>
//      new Thread(() => runThread(t))
//    }
//
//    tasksReverse.foreach(threadPool.execute)
//    threadPool.shutdown()
//  }
//
//  def runThread(threadNum: Int): Unit = {
//    this.synchronized {
//      println(s"hello from thread $threadNum")
//    }
//  }
//
//
//  def main(args: Array[String]): Unit = {
//    inceptionThreads(10)
//  }

//  def inceptionThreads(n: Int): Unit = {
//
//    def spawnThread(currentMap: Map[Int, Thread]): Map[Int, Thread] = {
//      val newThreadNum = currentMap.last._1 + 1
//      if (newThreadNum == n) currentMap
//      else {
//        val newTuple = newThreadNum -> new Thread(() => runThread(newThreadNum))
//        spawnThread(currentMap + newTuple)
//      }
//    }
//
//    spawnThread(Map(1 -> new Thread)).foreach((_, thread) => thread.start())
//  }
//
