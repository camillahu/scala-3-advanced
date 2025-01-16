package com.rockthejvm.playground.part1as

import scala.annotation.targetName
import scala.util.Try

object DarkSugars {

  //1- sugar for methods with one argument
  def singleArgMethod(arg:Int): Int = arg + 1

  val aMethodCall = singleArgMethod({
    //long code
    42
  })

  val aMethodCall_v2 = singleArgMethod{
    // long code
    43
  }

  //example: Try, Future
  val aTryInstance = Try{
    throw new RuntimeException()
  }

  //with hofs
  val anIncrementedList = List(1,2,3).map { x =>
    //code block
    x + 1
  }

  //2- single abstract method pattern
  trait Action {
    def act(x:Int): Int
  }

  val anAction = new Action {
    override def act(x: Int): Int = x + 1
  }
  //instead of this, try this:
  val anotherAction: Action = (x:Int) => x + 1

  //example Runnable
  val aThread = new Thread(new Runnable {
    override def run(): Unit = println("Hi, Scala, from another thread")
  })

  val aSweeterThread = new Thread(()=> println("Hi, Scala"))

  //3- methods ending in a : are RIGHT-ASSOCIATIVE
    // normally a method is evaluated from left to right, but these are evaluated in reverse.

  val aList = List(1,2,3)
  val aPrependedList = 0 :: aList // aList.::(0)
  val aBigList = 0 :: 1 :: 2 :: List(3,4) // List(3,4).::(2).::(1).::(0)

  class MySteam[T] {
    infix def -->:(value: T): MySteam[T] = this //impl not important
  }
  val myStream = 1 -->: 2 -->: 3 -->: 4 -->: new MySteam[Int]

  //4 - multi-word identifiers
  class Talker(name:String) {
    infix def `and then said`(gossip: String) = println(s"$name said $gossip")
  }

  val daniel = new Talker("Daniel")
  val danielsStatement = daniel `and then said` "I love Scala"

  //example HHTP libraries
  object `Content-Type` {
    val `application/json` = "application.json"
  }

  //5- infix types
  @targetName("Arrow") //for more readable bytecode + Java interop
  infix class -->[A, B]  //non-alphanumeric type
  val compositeType: Int --> String = new -->[Int, String]

  // 6- update()
  val anArray = Array(1,2,3,4) //mutable collection

  anArray.update(2,45)
  anArray(2) = 45 //same as above

  //7 - mutable fields
  class Mutable {
    private var internalMember: Int = 0
    def member = internalMember // "getter"
    def member_=(value: Int): Unit = internalMember = value // "setter"
  }

  val aMutableContainer = new Mutable
  aMutableContainer.member = 42 // same as aMutableContainer.member_=(42)

  // 8 -variable arguments (varargs)

  def methodWithVarargs(args: Int*) = { //star signals the compiler to accept any number of args
    //return num of args supplied
    args.length
  }

  val callWithZeroArgs = methodWithVarargs()
  val callWithOneArgs = methodWithVarargs(78)
  val callWithTwoArgs = methodWithVarargs(12, 34)

  val aCollection = List(1,2,3,4)
  val callWithDynamicAgrs = methodWithVarargs(aCollection*) //star unwraps the list into elements


  def main(args: Array[String]): Unit = {

  }
}
