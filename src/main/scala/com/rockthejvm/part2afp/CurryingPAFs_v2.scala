package com.rockthejvm.part2afp

object CurryingPAFs_v2 {

  val superAdder: Int => Int => Int =
    x => y => x + y

  val add3: Int => Int = superAdder(3)
  val eight = add3(5)
  val eight_v2 = superAdder(3)(5)

  //curried method
  def curriedAdder(x:Int)(y: Int): Int =
    x + y

  // methods != function values
  //METHODS are invokable members of classes or objects.
  //FUNCTION VALUES are instances of functional traits with an apply method(Function1, Function2 etc)

  val add4 = curriedAdder(4)
  //eta expansion - turn a method into a function value(lambda)
  val nine = add4(5)

  def increment(x: Int): Int = x + 1
  val aList = List(1,2,3)
  val anIncrementedList = aList.map(increment)
  //another example of eta-expansion- compiler converts increment to lambda

  //way to control how the compiler does eta-expansion for you:
  //underscores are powerful

  //below obtains the equivalent of x => concatenator("...defined string", x, "...another defined string")
  def concatenator(a: String, b: String, c: String): String = a + b + c
  val insertName = concatenator("Hello, my name is ", _: String, ", I'm going to show you a nice trick")

  val danieldGreeting = insertName("Daniel")
  val fillInTheBlanks = concatenator(_: String, "Daniel", _: String)

  //exercises
  val simpleAddFunction = (x: Int, y: Int) => x + y
  def simpleAddMethod(x: Int, y: Int) = x + y
  def curriedAddMethod(x: Int)( y: Int) = x + y

  //1 obtain an add7 function x => x + 7 from the three definitions above

  val add7_v1 = simpleAddFunction(_:Int, 7)
  val add7_v2 = simpleAddMethod(_: Int, 7)
  val add7_v3 = curriedAddMethod(7)

  //2 process a list of numbers and return their string representations under different formats
  // step 1: create a curried formatting method with a formatting string and a value
  // step 2: process a list of numbers with various formats
  // ex :
  val piWith2Dec = "%4.2F".format(Math.PI)

  def curriedFormatter(fmt: String)(value: Double) = fmt.format(value)
  
  def main(args: Array[String]): Unit = {
//    println(List(Math.PI, Math.E, 1, 9.8, 13e-12).map(curriedFormatter("%4.2f")))
//    println(List(Math.PI, Math.E, 1, 9.8, 13e-12).map(curriedFormatter("%8.6f")))
  }
}
