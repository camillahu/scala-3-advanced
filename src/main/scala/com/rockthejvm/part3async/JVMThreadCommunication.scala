package com.rockthejvm.part3async

import scala.collection.mutable
import scala.util.Random

object JVMThreadCommunication {


  def main(args: Array[String]): Unit = {
    ProdConsV4.start(2, 4, 5)
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

// insert a larger container
// producer -> [ _ _ _ ] -> consumer

object ProdConsV3 {
  def start(containerCapacity: Int): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]

    val consumer = new Thread(() => {
      val random = new Random(System.nanoTime())

      while(true) {
        buffer.synchronized {
          if (buffer.isEmpty) {
            println("[consumer] buffer empty, waiting...")
            buffer.wait()
          }

          //buffer must not be empty
          val x = buffer.dequeue()
          println(s"[consumer] I've just consumed $x")

          buffer.notify() //consumer: producer, give me more elements!
          // wake up producer if it's asleep
        }
        Thread.sleep(random.nextInt(500))
      }
    })

    val producer = new Thread(() => {
      val random = new Random(System.nanoTime())
      var counter = 0

      while(true) {
        buffer.synchronized{
          if(buffer.size == containerCapacity) {
            println("[producer] buffer full, waiting...")
            buffer.wait()
          }

          //buffer is not empty
          val newElement = counter
          counter += 1
          println(s"[producer] I'm producing $newElement")
          buffer.enqueue(newElement)

          buffer.notify() //producer: consumer, dont be lazy!
          //wakes up the consumer (if it's asleep). will not happen if no other thread is waiting.
        }
      Thread.sleep(random.nextInt(500))
      }
    })

    consumer.start()
    producer.start()

  }
}

//large container, multiple producers/consumers
//producer1 -> [_ _ _] -> consumer1
//producer2 ---^     +---> consumer2

object ProdConsV4 {

  class Consumer(id:Int, buffer: mutable.Queue[Int]) extends Thread {
    override def run(): Unit = {
      val random = new Random(System.nanoTime())

      while (true) {
        buffer.synchronized {

          /*
          * one producer, two consumers.
          * producer produces 1 value in the buffer.
          * both consumers are waiting.
          * producer calls notify, awakens one consumer.
          * consumer dequeues, calls notify, awakens the other consumer.
          * the other consumer awakens, tries dequeue, CRASH.
          * solution: use while instead of if to check buffer.isEmpty
          * */

          while(buffer.isEmpty) {
            println(s"[consumer $id] buffer empty, waiting...")
            buffer.wait() //waiting to be notified of new value in queue
          }

          //buffer is non-empty
          val newValue = buffer.dequeue() //extracting element from queue
          println(s"[consumer $id] consumed $newValue")

          //notify a producer

          /*
          * Scenario: 2 producers, 1 consumer, capacity = 1
          * prod1 produces value, then waits
          * prod2 sees buffer full, waits
          * consumer consumes value, notifies one producer (prod1)
          * consumer sees buffer empty, waits
          * prod1 produces value, calls notify - signal goes to producer 2
          * prod1 sees buffer full, waits
          * prod2 sees buffer full, waits
          * deadlock
          * Solution: use notify all waiting threads on the buffer
          * */

          buffer.notifyAll()
        }

        Thread.sleep(random.nextInt(500))
      }
    }
  }

  class Producer(id: Int, buffer: mutable.Queue[Int], capacity: Int) extends Thread {
    override def run(): Unit = {
      val random = new Random(System.nanoTime())
      var currentCount = 0
      while(true) {
        buffer.synchronized{
          //changed if to while -- same solution as in consumer.
          while(buffer.size == capacity) { //buffer full //TODO 1
            println(s"[producer $id] buffer is full, waiting...")
            buffer.wait() //waiting for notification that buffer has space to inject new value
          }

          //there is space in the buffer
          println(s"[producer $id] producing $currentCount")
          buffer.enqueue(currentCount)

          //wake up a consumer
          buffer.notifyAll()

          currentCount += 1
        }

        Thread.sleep(random.nextInt(500))
      }
    }
  }

  def start(nProducers: Int, nConsumers: Int, containerCapacity: Int): Unit = {
    val buffer: mutable.Queue[Int] = new mutable.Queue[Int]
    val producers = (1 to nProducers).map(id => new Producer(id, buffer, containerCapacity))
    val consumers = (1 to nProducers).map(id => new Consumer(id, buffer))

    producers.foreach(_.start())
    consumers.foreach(_.start())
  }
}
