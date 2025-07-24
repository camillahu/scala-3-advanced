package com.rockthejvm.part4context

import scala.collection.parallel.*
import scala.collection.parallel.CollectionConverters.*

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
        def checkNum(acc: Int): Boolean = {
          if (acc <= 1) true
          else if (int % acc == 0) false
          else checkNum(acc - 1)
        }

        if (int <= 1) false
        else checkNum(int - 1)
      }
    }

    //1.2
    extension (int: Int) {
      def isPrime_v2: Boolean = {
        val aParList: ParSeq[Int] = (2 to int - 1).toList.par
        aParList.exists(n => int % n == 0)
      }
    }


    //2
//    sealed abstract class Tree[A]
//
//    case class Leaf[A](value: A) extends Tree[A]
//
//    case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]
//
//    extension [A, B](tree: Tree[A]) {
//      def map(f: A => B): Tree[B] = tree match {
//        case Leaf(value) => Leaf(f(value))
//        case Branch(left, right) => Branch(left.map(f), right.map(f))
//      }
//      def forall(predicate: A => Boolean): Boolean = tree match {
//        case Leaf(value) if predicate(value) => true
//        case Branch(left, right) if left.forall(predicate) && right.forall(predicate) => true
//        case _ => false
//      }
//      def sum(using combinator: Combinator[A]): A = tree match {
//        case Leaf(value) => value
//        case Branch(left, right) => Branch(left.sum(combinator), right.sum(combinator))
//      }
//    }

  def main(args: Array[String]): Unit = {
//    println(danielGreeting)
    println(50.isPrime_v2)
  }
}
