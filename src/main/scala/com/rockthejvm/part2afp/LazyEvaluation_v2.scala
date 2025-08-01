package com.rockthejvm.part2afp

object LazyEvaluation_v2 {

  val x: Int = {
    println("Hello")
    42
  } //this will print Hello to the console without having it in main
  // val x is evaluated at its definition place.

  lazy val LZx: Int = {
    println("Hello")
    42
  } //will delay the evaluation of the value until first use.
  //calling this twice will result in only the first evaluation,
  // as it is bound to the value it gets the first time its used.
  // therefore, println(x) println(x) will result in only one printing of "Hello 42"
  
  def main(args: Array[String]): Unit = {

  }
}
