package com.rockthejvm.playground.part1as

object AdvancedPatternMatching {
  /*
  * Pattern matching:
  * - objects
  * - constants
  * - wildcards
  * - variables
  * - infix patterns
  * - lists
  * - case classes
  * */

  class Person(val name: String, val age: Int)

  object Person {
    def unapply(person: Person): Option[(String, Int)] = // person match {case Person(string, int) => ...}
      //unlocks ability to use pattern match, impl not important.
      if (person.age < 21) None
      else Some((person.name, person.age))

    //define unapply for other types
    def unapply(age: Int): Option[String] = // int match {case Person(String) => ... }
      if (age < 21) Some("minor")
      else Some("legally allowed to drink")
  }

  val daniel = new Person("Daniel", 102)
  val danielPM = daniel match {
    //unapply method needs to be added to Person to unlock a pattern for matching
    //must have a signature like this: Person.unapply(daniel) => Option((n,a))
    case Person(n, a) => s"Hi there, I'm $n"
  }

  val danielsLegalStatus = daniel.age match {
    case Person(status) => s"Daniel's legal drinking status is $status"
  }

  //boolean patterns
  object even {
    def unapply(arg: Int): Boolean = arg % 2 == 0
  }

  object singleDigit {
    def unapply(arg: Int): Boolean = arg > -10 && arg < 10
  }

  val n: Int = 4
  val matchProperty = n match {
    case even() => "an even number"
    case singleDigit() => "a one digit number"
    case _ => "no special property"
  }

  // infix patterns
  infix case class Or[A, B](a: A, b: B)

  //this is matchable because case classes automatically have comp objects with unapply
  val anEither = Or(2, "two")
  val humanDescription = anEither match {
    case number Or string => s"$number is written as $string"
  }

  val aList = List(1, 2, 3)
  val listPM = aList match {
    case 1 :: 2 :: rest => "a list starting with 1 and 2"
    case 1 :: rest => "a list starting with 1"
    case _ => "some uninteresting list"
  }

  //decomposition sequences
  val vararg = aList match {
    case List(1, _*) => "list starting with 1 and a variable amount of elements"
    case _ => "some other list"
  }

  // creating own patterns

  abstract class MyList[A] {
    def head: A = throw new NoSuchElementException

    def tail: MyList[A] = throw new NoSuchElementException
  }

  case class Empty[A]() extends MyList[A]

  case class Cons[A](override val head: A, override val tail: MyList[A]) extends MyList[A]

  //with home-made collections, you need to add a comp object with the
  //unapplySeq method to make it eligible for PM with varargs
  object MyList {
    def unapplySeq[A](list: MyList[A]): Option[Seq[A]] =
      if(list == Empty()) Some(Seq.empty)
      else unapplySeq(list.tail).map(restOfSequence => list.head +: restOfSequence)
  }

  val myList: MyList[Int] = Cons(1, Cons(2, Cons(3, Empty())))
  val varargCustom = myList match {
    case MyList(1, _*) => "list starting with 1"
    case _ => "some other list"
  }

  // custom return type for unapply
  // doesn't need to return an option, but needs to return a Type that has isEmpty and get methods.
  // basically never used
  abstract class Wrapper[T] {
    def isEmpty: Boolean
    def get: T
  }

  object PersonWrapper {
    def unapply(person: Person): Wrapper[String] = new Wrapper[String] {
      override def isEmpty = false
      override def get: String = person.name
    }
  }

  val weirdPersonPM = daniel match {
    case PersonWrapper(name) => s"Hey my name is $name"
  }

  def main(args: Array[String]): Unit = {
    println(danielPM)
    println(danielsLegalStatus)
    println(matchProperty)
    println(listPM)
    println(vararg)
  }
}
