package com.rockthejvm.part2afp

object PartialFunctions_v2 {

  val aPartialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  }

  val canCallOn37 = aPartialFunction.isDefinedAt(37)
  val liftedPF = aPartialFunction.lift

  val anotherPF: PartialFunction[Int, Int] = {
    case 45 => 86
  }

  val pfChain = aPartialFunction.orElse[Int, Int](anotherPF)


  //HOFs accept PFs as args:
  val aList = List(1,2,3,4)
  val aChangedList = aList.map {
    case 1 => 4
    case 2 => 3
    case 3 => 45
    case 4 => 67
    case _ => 0
  }

  //process collections example
  case class Person(name: String, age: Int)
  val someKids = List(
    Person("Alice", 3),
    Person("Bobbie", 5),
    Person("Jane", 4)
  )

  val kidsGrowingUp = someKids.map {
    case Person(name, age) => Person(name, age + 1)
  }
}
