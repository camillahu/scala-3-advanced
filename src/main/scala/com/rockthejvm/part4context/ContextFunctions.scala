package com.rockthejvm.part4context

import scala.concurrent.{ExecutionContext, Future}

object ContextFunctions {
  //scala 3 specific

  val aList = List(2,1,3,4)
  val sortedList = aList.sorted

  //defs can take using clauses
  def methodWithoutContextArguments(nonContextArg: Int)(nonContextArg2: String): String = ???
  def methodWithContextArguments(nonContextArg: Int)(using nonContextArg2: String): String = ???

  //eta-expansion
  val functionWithoutContextArguments = methodWithoutContextArguments
  // val func2 = methodWithContextArguments -- doesn't work

  //context function - can take given arguments as params -- quesionmark signifies that the argument being passed is a given instance
  val functionWithContextArguments: Int => String ?=> String = methodWithContextArguments

  val someResult = functionWithContextArguments(2)(using "scala") //replace the given argument with the argument passed here explicitly.

  // who do context functions exist?
  // - convert methods with using clauses to function values
  // - create HOFs with function values taking given instances as asguments
  // - being able to give execution context to the function at the place of execution, not having to give it where the function is defined. ex:

  //doesn't work without given execution context in scope:
  // val incrementAsync: Int => Future[Int] = x => Future(x + 1)

  //this will work:
  val incrementAsync: ExecutionContext ?=> Int => Future[Int] = x => Future(x + 1)

  def main(args: Array[String]): Unit = {

  }
}
