package com.rockthejvm.part2afp

import scala.annotation.targetName

object Monads_v2 {

  // a monad is a data structure that describes a computation without preforming it

  val f = (x: Int) => PossiblyMonad(x + 1)
  val g = (x: Int) => PossiblyMonad(2 * x)
  val aPossiblyMonad = PossiblyMonad(2)
  val pure = (x: Int) => PossiblyMonad(x)

  //exercise

  // a PossiblyMonad wraps any computation that might produce side effects.
  // we have no control over the side effects produced by unsafeRun.
  case class PossiblyMonad[A](unsafeRun: () => A) {
    def map[B](f: A => B): PossiblyMonad[B] =
      PossiblyMonad(() => f(unsafeRun()))

    def flatMap[B]( f: A => PossiblyMonad[B]): PossiblyMonad[B] =
      PossiblyMonad(() => f(unsafeRun()).unsafeRun())
  }

  object PossiblyMonad {
    @targetName("pure")
    def apply[A](value: => A): PossiblyMonad[A] =
      new PossiblyMonad(() => value)
  }

  val leftIdentity = pure(2).flatMap(f) == f(2)
  val rightIdentity = aPossiblyMonad.flatMap(pure) == aPossiblyMonad

  //associativity tests that the functions will be applied in the exact order we specified
  val associativity = aPossiblyMonad.flatMap(f).flatMap(g) == aPossiblyMonad.flatMap(x => f(x).flatMap(g))
  // will give false negative tests because the functions/methods constructs a new lambda
  // each time, which will always be different values, even if the values are the same.
  // ex: PossiblyMonad(3) != PossiblyMonad(3)

  // the real test needs to test the values produced with our unsafeRun and side effect ordering
  val leftIdentity_v2 = pure(2).flatMap(f).unsafeRun() == f(2).unsafeRun()
  val rightIdentity_v2 = aPossiblyMonad.flatMap(pure).unsafeRun() == aPossiblyMonad.unsafeRun()
  val associativity_v2 = aPossiblyMonad.flatMap(f).flatMap(g).unsafeRun() == aPossiblyMonad.flatMap(x => f(x).flatMap(g)).unsafeRun()

  def main(args: Array[String]): Unit = {
    println(
      s" left: $leftIdentity_v2" +
      s" right: $rightIdentity_v2" +
      s" associative: $associativity_v2"
    )
  }
}
