package com.rockthejvm.part3async
import scala.collection.parallel.*
import scala.collection.parallel.CollectionConverters.*
import scala.collection.parallel.immutable.ParVector

object ParallelCollections {

  val aList = (1 to 1000000).toList
  val anIncrementedList = aList.map( _ + 1)

  //Parallel collections -- for faster processing

  val parList: ParSeq[Int] = aList.par
  val aParallelizedList = parList.map(_ + 1)
  // map, flatMao, filter, foreach, reduce, fold etc are available on the parallel version of collections too.
  /*
  * Parallel is applicable for
  - Seq
  - Vector
  - Array
  - Map
  -Set
  * */

  //parallel collection build explicitly/ yourself
  
  val aParVector = ParVector[Int](1,2,3,4,5,6)

  def measure[A](expression: => A): Long = {
    // checking how much time the expression takes to evaluate
    val time = System.currentTimeMillis()
    expression // forcing evaluation
    System.currentTimeMillis() - time
  }

  def compareListTransformation(): Unit = {
    val list = (1 to 30000000).toList
    println("list creation done")

    val serialTime = measure(list.map(_ + 1))
    println(s"serial time: $serialTime")

    val parallelTime = measure(list.par.map(_ + 1))
    println(s"parallel time: $parallelTime")

  }

  def demoUndefinedOrder(): Unit = {
    val list = (1 to 1000).toList
    val reduction = aList.reduce(_ - _) // usually bad idea to use non- associative operators such as -
    // [1, 2, 3].reduce(_-_) = 1 - 2 - 3 = -4
    // [1, 2, 3].reduce(_-_) = 1 - (2 - 3) = 2

    val parallelReduction = list.par.reduce(_ - _) //will not have the same results, order of elements is undefined.

    println(s"Sequential reduction $reduction")
    println(s"Parallel reduction $parallelReduction")
  }

  //for associative operations, the elements will always be pieced together in the right order even though they run on
  //different threads.
  //end result is deterministic

  def demoDefinedOrder(): Unit = {
    val strings = "I love parallel collections but I must be careful".split(" ").toList
    val concatenation = strings.reduce(_ + " " + _)
    val parConcatenation = strings.par.reduce(_ + " " + _)

    println(s"Sequential concatenation: $concatenation")
    println(s"Parallel concatenation: $parConcatenation")
  }

  //don't ever do this, but if you do, be aware of race conditions:
  def demoRaceConditions(): Unit = {
    var sum = 0
    (1 to 1000).toList.par.foreach(elem => sum += elem)
    //if par is removed this will print the correct value 500500.
    println(sum)
  }
  

  def main(args: Array[String]): Unit = {
//    compareListTransformation()
//    demoUndefinedOrder()
//    demoDefinedOrder()
    demoRaceConditions()
  }
}
