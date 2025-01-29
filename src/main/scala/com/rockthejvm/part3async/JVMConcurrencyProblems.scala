package com.rockthejvm.part3async

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
      val thread1 = new Thread(() => buy(account, "shoes", 3000))
      val thread2 = new Thread(() => buy(account, "iPhone", 4000))
      thread1.start()
      thread2.start()
      thread1.join()
      thread2.join()
      if (account.amount != 43000) println(s"AHA! I've just broken the bank: ${account.amount}")
    }
  }

  def main(args: Array[String]): Unit = {
    demoBankingProblem()
  }
}
