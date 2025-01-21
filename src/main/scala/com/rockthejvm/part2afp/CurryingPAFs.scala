package com.rockthejvm.part2afp

object CurryingPAFs {
  //currying
  val superAdder: Int => Int => Int =
    x => y => x + y

  val add3: Int => Int = superAdder(3) // y => 3 +y
  val eight = add3(5) //8
  val eight_v2 = superAdder(3)(5)

  //curried methods
  def curriedAdder(x:Int)(y: Int): Int = x + y


  //methods are not the same as function values
  //converting methods to functions
  val add4 = curriedAdder(4) //eta-expansion
  val nine = add4(5) //9


  //underscores are powerful: allow you to decide the shapes of lambdas obtained by methods
  def concatenator(a: String, b: String, c: String): String = a + b + c
  val insertName = concatenator(
    "Hello, my name is",
    _:String,
    "I'm going to show you a nice Scala trick."
  ) // x => concatenator("....", x, "...."

  val danielsGreeting = insertName("Daniel")
  val fillInTheBlanks = concatenator(_:String, "Daniel", _:String) // (x, y) => concatenator(x, "Daniel", y)
  val danielsGreeting_v2 = fillInTheBlanks("Hi,", "how are you?")

  //exercises
  // 1
  val simpleAddFunction = (x:Int, y: Int) => x + y
  def simpleAddMethod(x:Int, y: Int) = x + y
  def curriedMethod(x:Int)(y:Int) = x + y

  val add7 = (x:Int) => simpleAddFunction(x, 7)
  val add7_v1 = simpleAddFunction(_:Int, 7)
  val add7_v2 = simpleAddMethod(_:Int, 7)
  val add7_v3 = curriedMethod(7)
  val add7_v4 = simpleAddFunction.curried(7)

  //2
  def curriedFormatter(fmt: String)(value: Double) = fmt.format(value)

  // mathods vs functions + by-name vs 0-lambdas
  def byName(n: => Int) = n + 1
  def byLambda( f: () => Int) = f() + 1

  def method: Int = 42
  def parenMethod(): Int = 42

  byName(23) //ok
  byName(method) //43. not eta-expanded -- method is invoked here
  byName(parenMethod()) // 43
  // byName(parenMethod) // will not compile
  byName((() => 42)()) //43
  // byName(() => 42) //not allowed-- instance of function 1, not int

  //byLambda(23) // not allowed
  //byLambda(method) // not allowed -- method does not have parenthesis/ arg list
  byLambda(parenMethod) //eta-expansion is done
  byLambda(() => 42)
  byLambda(() => parenMethod()) //this is what byLambda(parenMethod) is changed to in eta-expansion

  def main(args: Array[String]): Unit = {
    println(add7_v1(3))
    println(add7_v2(3))
    println(add7_v3(3))
    println(add7_v4(3))

    println(List(Math.PI, Math.E, 1, 9.8, 13e-12).map(curriedFormatter("%4.2f")))
    println(List(Math.PI, Math.E, 1, 9.8, 13e-12).map(curriedFormatter("%8.6f")))
    println(List(Math.PI, Math.E, 1, 9.8, 13e-12).map(curriedFormatter("%14.12f")))
  }
}
