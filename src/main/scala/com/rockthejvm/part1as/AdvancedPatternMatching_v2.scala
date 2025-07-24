package com.rockthejvm.part1as

object AdvancedPatternMatching_v2 {

  class Person(val name: String, val age: Int)

  // if you want to match on a self-made class, you need the unapply method in a companion object
  // the unapply is the conditions that the match will run on.
  // you can also overload the unapply method to take different args.

  object Person {
    def unapply(person:Person): Option[(String, Int)] =
      if (person.age < 21) None
      else Some((person.name, person.age))

    def unapply(age: Int): Option[String] = {
      if (age < 21) Some("minor")
      else Some("legally allowed to drink")
    }
  }

  val daniel = new Person("Daniel", 102)
  val danielPM = daniel match {
    case Person(n, a) => s"Hi there I'm $n"
  }

  val danielsLegalStatus = daniel.age match {
    case Person(status) => s"Daniels legal drinking status is $status"
  }

  //boolean patterns
  object even {
    def unapply(arg: Int): Boolean = arg % 2 == 0
  }

  object singleDigit {
    def unapply(arg:Int): Boolean = arg > -10 && arg < 10
  }

  val n: Int = 43
  val mathProperty = n match {
    case even() => "an even number" //compiler checks if there is a pattern matching extractor (unapply method) in the even object
    case singleDigit() => "a one digit number" //compiler checks if there is a pattern matching extractor (unapply method) in the singleDigit object
    case _ => "no special property"
  }

  //infix patterns
  infix case class Or[A, B](a: A, b: B)
  val anEither = Or(2, "two")
  val humanDescriptionEither = anEither match {
    case number Or string => s"$number is written as $string"
  }

  val aList = List(1,2,3)
  val listPM = aList match {
    case 1 :: rest => "a list starting with 1"
    case _ => "some uninteresting list"
  }

  // decompose sequences
  val vararg = aList match {
    case List(1, _*) => "list starting with 1"
    case _ => "soime other list"
  }

  abstract class MyList[A] {
    def head: A = throw new NoSuchElementException
    def tail: MyList[A] = throw new NoSuchElementException
  }
  case class Empty[A]() extends MyList[A]
  case class Cons[A](override val head: A, override val tail: MyList[A]) extends MyList[A]

  object MyList {
    def unapplySeq[A](list: MyList[A]): Option[Seq[A]] =
      if(list == Empty()) Some(Seq.empty)
      else unapplySeq(list.tail).map(restOfSequence => list.head +: restOfSequence)
  }

  val myList: MyList[Int] = Cons(1, Cons(2, Cons(3, Empty()))) //List(1,2,3)

  val varargCustom = myList match {
    case MyList(1, _*) => "list starting with 1"
    case _ => "soime other list"
  }

  //custom return type for unapply
  abstract class Wrapper[T] {
    def isEmpty: Boolean
    def get: T
  }

  object PersonWrapper {
    def unapply(person: Person): Wrapper[String] = new Wrapper[String] {
      override def isEmpty = false
      override def get = person.name
    }
  }

  val weirdPersonPM = daniel match {
    case PersonWrapper(name) => s"Hey my name is $name"
  }

  def main(args: Array[String]): Unit = {
    println(danielPM)
    println(danielsLegalStatus)
    println(mathProperty)
  }

}
