package com.rockthejvm.part1as

import scala.annotation.tailrec

object Recap {

  // values, types, expressions
  val aCondition = false
  val anIfExpression = if (aCondition) 42 else 55

  val aCodeBlock = {
    if(aCondition) 54
    else 78
  }

  //types
  val theUnit = println("hello Scala") //Unit = ()

  //functions
  def aFunction(x:Int): Int = x + 1

  //recursion: stack and tail
  @tailrec def factorial(n:Int, acc: Int): Int =
    if(n <= 0) acc
    else factorial(n -1, n*acc)

  val fact10 = factorial(10,1)

  //OOP
  class Animal
  class Dog extends Animal
  val aDog: Animal = new Dog

  trait Carnivore {
    infix def eat(a: Animal): Unit
  }

  class Crocodile extends Animal with Carnivore {
    override infix def eat(a: Animal): Unit = println("I'm a croc nomnom")
  }

  //method notation
  val aCroc = new Crocodile
  aCroc.eat(aDog)
  aCroc eat aDog //infix position (operator position)

  //anonymous classes
  val aCarnivore = new Carnivore {
    override infix def eat(animal: Animal): Unit = println("I'm a carnivore")
  }

  // generics
  abstract class LList[A] {
    //type A is known inside the implementation
  }

  //singletons and companions
  object LList
  //companion object, can read each others private fields and methods.
  // Used for instance-independent ("static") fields/methods.

  //case classes
  case class Person(name: String, age:Int)

  // enums -- contain finite amount of possible elements
  enum BasicColors {
    case RED, GREEN, BLUE //only instances allowed
  }

  //exceptions
  def throwSomeException(): Int =
    throw new RuntimeException()

  val aPotentialFaliure = try {
    //code that might fail
    throwSomeException()
  } catch {
    case e: Exception => "I caught an exception"
  } finally {
    // closing resources
    println("some important logs")
  }

  //FP
  val incrementer = new Function1[Int, Int] {
    override def apply(v1: Int): Int = v1 + 1
  }
  val two = incrementer(1) //apply method on incrementer

  //lambdas
  val anonymousIncrementer = (x:Int) => x +1

  //higher-order functions
  val anIncrementerList = List(1,2,3).map(anonymousIncrementer) // [2,3,4]

  //for-comprehension
  val pairs = for {
    number <- List(1,2,3)
    char <- List('a', 'b')
  } yield s"$number-$char"

  // Scala collections

  //options, try
  val anOption: Option[Int] = Option(42)

  //pattern matching
  val x = 2
  val order = x match {
    case 1 => "first"
    case 2 => "second"
    case _ => "not important"
  }

  val bob = Person("Bob", 22)
  val greeting = bob match {
    case Person(n, _) => s"Hi, my name is $n"
  }


  def main(args: Array[String]): Unit = {

  }
}
