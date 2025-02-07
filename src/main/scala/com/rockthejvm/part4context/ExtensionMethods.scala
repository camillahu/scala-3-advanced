package com.rockthejvm.part4context

object ExtensionMethods {
  //making custom methods for specific types

  case class Person(name: String) {
    def greet: String = s"Hi, my name is $name, nice to meet you!"
  }

  extension ( string: String)
    def greetAsPerson: String = Person(string).greet

  val danielGreeting = "Daniel".greetAsPerson

  //generic extension methods
  extension [A](list: List[A])
    def ends: (A,A) = (list.head, list.last)

  val aList = List(1,2,3,4)
  val firstLast = aList.ends

  // reason 1 to use extension methods -- make APIs very expressive.
  // reason 2 -- enhance certain types with new capabilities (contextual abstraction)
  // can make methods with capabilities that are only available for some types but not for others.
  
  //it makes super powerful code thats easy to read in large code bases and saves up space. 
  
  trait Combinator[A] {
    def combine(x: A, y: A): A
  }

  extension [A](list: List[A])
    def combineAll(using combinator: Combinator[A]): A =
      list.reduce(combinator.combine)

  given intCombinator: Combinator[Int] with
    override def combine(x:Int, y: Int) = x + y

  val firstSum = aList.combineAll // works because there is a given combinator int in scope, sum is 10
  val someStrings = List("I", "love", "Scala")
  // val stringsSum = someStrings.combineAll //does not compile without given combinator string in scope. 
  
  //group extension methods together
  object GroupedExtensions { // (wrapped in new object to avoid double definitions) 
    extension [A](list: List[A]) {
      def ends: (A, A) = (list.head, list.last)
      def combineAll(using combinator: Combinator[A]): A =
        list.reduce(combinator.combine)
    }
  } 
  
  // call extension methods directly
  val firstLast_v2 = ends(aList) //same as aList.ends 
  
  //exercises 
  
  //1
  extension (int: Int) {
    def isPrime: Boolean = {
      val checkNum = (acc: Int) => match acc {
        case f if acc <= 1 => false
        case p if int % acc == 0 => true
        case _ => checkNum(acc-1) 
      }
      checkNum(int - 1)
      
    }
  }

  def main(args: Array[String]): Unit = {
    println(danielGreeting)
  }
}
