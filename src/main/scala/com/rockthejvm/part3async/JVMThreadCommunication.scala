package com.rockthejvm.part3async

object JVMThreadCommunication {


  def main(args: Array[String]): Unit = {
    ProdConsV2.start()
  }
}

//example: the producer-consumer problem
class SimpleContainer {
  private var value: Int = 0

  def isEmpty: Boolean =
    value == 0

  def set(newValue: Int): Unit =
    value = newValue

  def get: Int = {
    val result = value
    value = 0
    result
  }
}

//PC part 1: one producer, one consumer
object ProdConsV1 {
  def start(): Unit = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("[consumer] waiting...")
      // busy waiting- a poor construction.
      while (container.isEmpty) {
        println("[consumer] waiting for a value...")
      }

      println(s"[consumer] I have consumed a value: ${container.get}")
    })

    val producer = new Thread(() => {
      println("[producer] computing ...")
      Thread.sleep(500)
      val value = 42
      println(s"[producer] I am producing, after LONG work, the value $value")
      container.set(value)
    })

    consumer.start()
    producer.start()
  }
}

//wait + notify -- passive waiting
object ProdConsV2 {
  def start(): Unit = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("[consumer] waiting...")

      container.synchronized { //block all other threads trying to "lock" this object
        //thread safe code
        if(container.isEmpty) container.wait() //release the lock and suspend the thread
        //reaquire the lock here
        //continue execution
        println(s"[consumer] I have consumed a value: ${container.get}")
      }
    })

    val producer = new Thread(() => {
      println("[producer] computing ...")
      Thread.sleep(500)
      val value = 42

      container.synchronized {
        println(s"[producer] I am producing, after LONG work, the value $value")
        container.set(value)
        container.notify() //awaken ONE suspended thread in this object
      }
    })
    // no guarantee if cons or prod starts first -- needs if check in consumer
    consumer.start()
    producer.start()
  }
}
