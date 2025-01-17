package com.rockthejvm.part2afp

object PartialFunctions {

  val aFunction: Int => Int = x => x + 1

  val aFussyFunction = (x: Int) =>
    if(x == 1) 42
    else if (x == 2) 56
    else if (x == 5) 999
    else throw new RuntimeException("no suitable cases possible")

  val aFussyFunction_v2 = (x: Int) => x match {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  }

  // partial function
  val aPartialFunction: PartialFunction[Int, Int] = { //instance of PartialFunction -- x => x match {...}
    case 1 => 42
    case 2 => 56
    case 5 => 999
  }

  //utils on PFs
  val canCallOn37 = aPartialFunction.isDefinedAt(37) //returns boolean
  val liftedPF = aPartialFunction.lift //int => option[int]

  val anotherPF: PartialFunction[Int, Int] = {
    case 45 => 86
  }
  val pfChain = aPartialFunction.orElse[Int,Int](anotherPF) //checks aPartialFunction first. If it doesn't match, checks anotherPF.

  // HOFs accepts PFs as args
  val aList = List(1,2,3,4)
  val aChangedList = aList.map(x => x match {
    case 1 => 4
    case 2 => 3
    case 3 => 45
    case 4 => 67
    case _ => 0
  })

  val aChangedList_v2 = aList.map({ //possible because PartialFunction[A, B] extends Function[A, B]
    case 1 => 4
    case 2 => 3
    case 3 => 45
    case 4 => 67
    case _ => 0
  })

  val aChangedList_v3 = aList.map{
    case 1 => 4
    case 2 => 3
    case 3 => 45
    case 4 => 67
    case _ => 0
  }

  case class Person(name:String, age: Int)
  val someKids = List(
    Person("Alice", 3),
    Person("Bobbie", 5),
    Person("Jane", 4)
  )
  //partial functions are great for processing data like this.
  val kidsGrowingUp = someKids.map {
    case Person(name,age) => Person(name, age +1)
  }

  def main(args: Array[String]): Unit = {
    println(aPartialFunction(2))
     // println(aPartialFunction(33)) -- will fail with exception
    println(canCallOn37)
    println(liftedPF(37))
    println(liftedPF(2))
    println(pfChain(45))
  }
}
