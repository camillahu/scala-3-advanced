package com.rockthejvm.part4context

object Givens_v2 {
  val aList = List(4,2,3,1)
  val anOrderedList = aList.sorted

  given descendingOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
  val anInverseOrderedList = aList.sorted(descendingOrdering)

  //custom sorting
  case class Person(name: String, age: Int)
  val people = List(Person("Alice", 29), Person("Sarah", 34), Person("Jim", 23))

  given personOrdering: Ordering[Person] = new Ordering[Person] {
    override def compare(x:Person, y: Person): Int =
      x.name.compareTo(y.name)
  }

  val sortedPeople = people.sorted

  //alt syntax
  object PersonAltSyntax {
    given personOrdering: Ordering[Person] with {
      override def compare(x: Person, y: Person): Int =
        x.name.compareTo(y.name)
    }
  }

  trait Combinator[A] {
    def combine(x: A, y: A): A
  }

  def combineAll[A](list: List[A])(using combinator: Combinator[A]): A =
    list.reduce(combinator.combine)

  given intCombinator: Combinator[Int] with {
    override def combine(x: Int, y: Int) = x + y
  }

  val firstSum = combineAll(List(1,2,3,4)) //intcombinator is passed automatically bc it is given.

  def main(args: Array[String]): Unit = {
    println(anOrderedList)
    println(anInverseOrderedList)
    println(sortedPeople)
  }
}
