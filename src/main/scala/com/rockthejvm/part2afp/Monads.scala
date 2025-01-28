package com.rockthejvm.part2afp

import scala.annotation.targetName

object Monads {

  def listStory(): Unit = {
    val aList = List(1, 2, 3)
    val listMultiply = for {
      x <- List(1, 2, 3)
      y <- List(4, 5, 6)
    } yield x * y

    val listMultiply_v2 = List(1, 2, 3).flatMap(x => List(4, 5, 6).map(y => x * y))

    val f = (x: Int) => List(x, x + 1)
    val g = (x: Int) => List(x, 2 * x)
    val pure = (x: Int) => List(x) //same as the list "constructor"

    //List satisfies these flatMap properties:

    // prop 1: left identity
    val leftIdentity = pure(42).flatMap(f) == f(42) //for every x, for every f

    // prop 2: right identity
    val rightIdentity = aList.flatMap(pure) == aList //for every list

    //prop 3: associativity -- ensures that flatMap is called on f first AND THEN g.

    /*
    * [1,2,3].flatMap(x => [x, x+1]) = [1, 2, 2, 3, 3, 4]
    * [1, 2, 2, 3, 3, 4].flatMap(x => [x, 2*x]) = [1, 2, 2, 4,   2, 4, 3, 6,   3, 6, 4, 8]
    * [1,2,3].flatMap(f).flatMap(g) = [1, 2, 2, 4, 2, 4, 3, 6, 3, 6, 4, 8]
    *
    *     ==
    *
    * [1, 2, 2, 4] = f(1).flatmap(g)
    * [2, 4, 3, 6] = f(1).flatmap(g)
    * [3, 6, 4, 8] = f(1).flatmap(g)
    * [1,2, 2,4, 2,4, 3,6, 3,6, 4,8] = f(1).flatMap(g) ++ f(2).flatMap(g) ++ f(3).flatMap(g)
    * [1, 2, 3].flatMap(x => f(x).flatMap(g))
    *
    * */

    val associativity = aList.flatMap(f).flatMap(g) == aList.flatMap(x => f(x).flatMap(g))
  }

  def optionStory(): Unit = {
    val anOption = Option(42)
    val optionString = for {
      lang <- Option("Scala")
      ver <- Option(3)
    } yield s"$lang- $ver"

    val optionString_v2 = Option("Scala").flatMap(lang => Option(3).map(ver => s"$lang-$ver"))

    val f = (x: Int) => Option(x + 1)
    val g = (x: Int) => Option(2 * x)
    val pure = (x: Int) => Option(x) //same as Option "constructor"

    //Option satisfies these flatMap properties:

    // prop 1: left identity
    val leftIdentity = pure(42).flatMap(f) == f(42) // for any x, for any option

    //prop 2: right identity
    val rightIdentity = anOption.flatMap(pure) == anOption // for any Option

    //prop 3: associativity -- ensures that flatMap is called on f first AND THEN g.
    /*
    * anOption.flatMap(f).flatMap(g) = Option(42).flatMap(x => Option(x + 1)).flatMap(x => Option(2 * x)
    * = Option(43).flatMap(x => Option(2 * x)
    * = Option(86)
    *
    * anOption.flatMap(f).flatMap(g) == anOption.flatMap(x => Option(x + 1).flatMap(y => 2 * y)))
    * = Option(42).flatMap(x => Option(2 * x)
    * = Option(86)
    * */

    val associativity = anOption.flatMap(f).flatMap(g) == anOption.flatMap(x => f(x).flatMap(g)) //for any option, f and g

  }

  //MONADS -- ability to chain dependant computations

  // monads wrap  ANY computations that might produce side effects, not just values.
  // anything we pass to it will not be preformed at construction phase.
  // if we want to preform what we pass, we need ro call unsafeRun

  case class PossiblyMonad[A](unsafeRun: () => A) {
    def map[B](f: A => B): PossiblyMonad[B] =
      PossiblyMonad(() => f(unsafeRun()))

    def flatMap[B](f: A => PossiblyMonad[B]): PossiblyMonad[B] =
      PossiblyMonad(() => f(unsafeRun()).unsafeRun())
  }

  object PossiblyMonad {
    @targetName("pure")
    def apply[A](value: => A): PossiblyMonad[A] =
      new PossiblyMonad(() => value)

  }

  def aMonadStory(): Unit = {

        val f = (x: Int) => PossiblyMonad(x + 1)
        val g = (x: Int) => PossiblyMonad(2 * x)
        val pure = (x: Int) => PossiblyMonad(x)
        val possiblyMonad = PossiblyMonad(42)

        //exercise: is this a monad? Yes.


        val leftIdentity =  pure(42).flatMap(f) == f(42)
        val rightIdentity = possiblyMonad.flatMap(pure) == possiblyMonad
        val associativity = possiblyMonad.flatMap(f).flatMap(g) == possiblyMonad.flatMap(x => f(x).flatMap(g))

        println(leftIdentity)
        println(rightIdentity)
        println(associativity)
        // ^^ false negative.

        //real test: values produced -- we need to put unsafe run on both sides of equality check.
        val leftIdentity_v2 = pure(42).flatMap(f).unsafeRun() == f(42).unsafeRun()
        val rightIdentity_v2 = possiblyMonad.flatMap(pure).unsafeRun() == possiblyMonad.unsafeRun()
        val associativity_v2 = possiblyMonad.flatMap(f).flatMap(g).unsafeRun() == possiblyMonad.flatMap(x => f(x).flatMap(g)).unsafeRun()

        println(leftIdentity_v2)
        println(rightIdentity_v2)
        println(associativity_v2)
  }

  def possiblyMonadExample(): Unit = {
    val aPossiblyMonad = PossiblyMonad {
      println("printing my first possibly monad")
      //do computations
      42
    }
    val anotherPM = PossiblyMonad {
      println("my second PM")
      "Scala"
    }
    val aResult = aPossiblyMonad.unsafeRun()
    println(aResult)
  }


    def main(args: Array[String]): Unit = {
      possiblyMonadExample()

    }
  }
