package com.rockthejvm.part2afp

object FunctionalCollections {

  //sets are functions A=> Boolean
  val aSet: Set[String] = Set("I", "Love", "Scala")
  val setContainsScala = aSet("Scala") //true

  //Seq extends PartialFunction[Int, A]
  val aSeq: Seq[Int] = Seq(1,2,3,4)
  val anElement = aSeq(2) //elem at index 2 is 3.
//  val aNonExistingElement = aSeq(100) // throw and OOBException

  //Map[K,V] "extends" PartialFunction[K, V]
  val aPhonebook = Map (
    "Alice" -> 123456,
    "Bob" -> 987654
  )
  val alicePhone = aPhonebook("Alice")
//  val danielsPhone = aPhonebook("Daniel") // throw

  def main(args: Array[String]): Unit = {

  }
}
