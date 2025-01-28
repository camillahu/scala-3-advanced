package com.rockthejvm.practise

import scala.annotation.tailrec

//Write a lazily evaluated, potentially INFINITE linked list
abstract class LzList[A] {
  def isEmpty: Boolean
  def head: A
  def tail: LzList[A]

  //utils
  infix def #::(element: A): LzList[A] //prepending
  infix def ++(another: => LzList[A]): LzList[A]

  //classics
  def foreach(f: A => Unit):Unit
  def map[B](f:A => B):LzList[B]
  def flatMap[B](f: A => LzList[B]):LzList[B]
  def filter(predicate: A => Boolean): LzList[A]
  def withFilter(predicate: A => Boolean): LzList[A] = filter(predicate)

  def take(n:Int): LzList[A] //takes first n elements from this lazy list
  def takeAsList(n:Int): List[A] = take(n).toList
  def toList: List[A] = {
    @tailrec
    def toListAux(remaining: LzList[A], aList: List[A]): List[A] = {
      if(remaining.isEmpty) aList.reverse
      else toListAux(remaining.tail, remaining.head :: aList)
    }
    toListAux(this, List.empty)
  } //use this carefully, can overflow
}

case class LzEmpty[A]() extends LzList[A] {
  def isEmpty: Boolean = true

  def head: A = throw new NoSuchElementException("Empty LzList has no head")

  def tail: LzList[A] = throw new NoSuchElementException("Empty LzList has no tail")

  //utils
  infix def #::(element: A): LzList[A] = new LzCons[A](element, this)

  infix def ++(another: => LzList[A]): LzList[A] = another

  //classics
  def foreach(f: A => Unit): Unit = ()

  def map[B](f: A => B): LzList[B] = LzEmpty()

  def flatMap[B](f: A => LzList[B]): LzList[B] = LzEmpty()

  def filter(predicate: A => Boolean): LzList[A] = this

  def take(n: Int): LzList[A] = {
    if (n == 0) this
    else throw new RuntimeException(s"cannot taks $n elements from an empty lazy list.")
  }
}

class LzCons[A](hd: => A, tl: => LzList[A]) extends LzList[A] {
  def isEmpty: Boolean = false

  override lazy val head: A = hd

  override lazy val tail: LzList[A] = tl

  //utils
  infix def #::(element: A): LzList[A] = new LzCons[A](element, this)

  infix def ++(another: => LzList[A]): LzList[A] = new LzCons(this.head, this.tail++another)

  //classics
  def foreach(f: A => Unit): Unit = {
    @tailrec
    def foreachTailRec(lzList: LzList[A]): Unit =
      if (lzList.isEmpty) ()
      else {
        f(lzList.head)
        foreachTailRec(lzList.tail)
      }
    foreachTailRec(this)
  }

  def map[B](f: A => B): LzList[B] = {
    new LzCons[B](f(this.head), this.tail.map(f))
  }

  def flatMap[B](f: A => LzList[B]): LzList[B] = {
    f(head) ++ tail.flatMap(f) //preserves lazy evaluation
  }

  def filter(predicate: A => Boolean): LzList[A] = {
    // not using head #:: tail.filter(predicate) because it's not lazy
    if(predicate(head)) new LzCons(head, tail.filter(predicate))
    else tail.filter(predicate)
  }

  def take(n: Int): LzList[A] = {
      if (n <= 0) LzEmpty()
      else if (n == 1) new LzCons(head, LzEmpty())
      // not using head #:: tail.take(n-1) because it's not lazy
      else new LzCons(head, tail.take(n - 1))
  }

}


object LzList {
  def empty[A]: LzList[A] = LzEmpty()

  def generate[A](start: A)(generator: A => A): LzList[A] = {
    new LzCons(start, LzList.generate(generator(start))(generator))
  }

  def from[A](list: List[A]): LzList[A] = list.reverse.foldLeft(LzList.empty) { (currentLzList, newElement) =>
    new LzCons(newElement, currentLzList)
  }

  def apply[A](values: A*) = LzList.from(values.toList)

  def fibonacci: LzList[BigInt] = {
    def fibo(first: BigInt, second: BigInt): LzList[BigInt] =
      new LzCons[BigInt](first, fibo(second, first + second))

    fibo(1,2)
  }

  def eratosthenes: LzList[Int] = {
    def isPrime(n: Int): Boolean = {
      @tailrec
      def isPrimeTailRec(potentialDivisor: Int): Boolean = {
        if (potentialDivisor < 2) true
        else if (n % potentialDivisor == 0) false
        else isPrimeTailRec(potentialDivisor - 1)
      }

      isPrimeTailRec(n / 2)
    }

    def sieve(numbers: LzList[Int]): LzList[Int] = {
      if (numbers.isEmpty) numbers
      else if (!isPrime(numbers.head)) sieve(numbers.tail)
      else new LzCons[Int](numbers.head, sieve(numbers.tail.filter(_ % numbers.head != 0)))
    }

    val naturalsFrom2 = LzList.generate(2)(_ + 1)
    sieve(naturalsFrom2)
  }
}



object LzListPlayground {
  def main(args: Array[String]): Unit = {
    val naturals = LzList.generate(1)(n => n + 1) //INFINITE list of natural numbers
//    println(naturals.head) // 1
//    println(naturals.tail.head) // 2
//    println(naturals.tail.tail.head) // 3
//
//    val first50k = naturals.take(50000)
//    first50k.foreach(println)
//    val first50kList = first50k.toList
//    println(first50kList)

//    //test classics
//    println(naturals.map(_ *2).takeAsList(100))
//    println(naturals.flatMap(x => LzList(x, x + 1)).takeAsList(100))
//    println(naturals.filter(_ < 10).takeAsList(9))
//    // println(naturals.filter(_ < 10).takeAsList(10)) // crash with SO or infinite rec
//
//    val combinationsLazy = for {
//      number <- LzList(1,2,3)
//      string <- LzList("black", "white")
//    } yield s"$number-$string"
//    println(combinationsLazy.toList)
//
//    val fibonacci =  LzList.generate((0, 1))(fib => (fib._2, fib._1 + fib._2)).map(fib => fib._1 + fib._2)
//    println(fibonacci.takeAsList(30))

    val fiboSolution = LzList.fibonacci
    println(fiboSolution.takeAsList(100))

    // not working
//    def isPrime(n: Int, l: LzList[Int]): Boolean = {
//      def checkNum(newList: LzList[Int]): Boolean = {
//        if (newList.isEmpty) false
//        else if (n % newList.head == 0) true
//        else checkNum(newList.tail)
//      }
//      checkNum(l)
//    }
//
//    val primeList = LzList.generate((2, LzCons(2, LzEmpty()))){ case (n, primes) =>
//      if (isPrime(n+1, primes)) (n+1, new LzCons[Int](n+1, primes))
//      else (n + 1, primes)
//    }
//
//    println(primeList.map(_._1)takeAsList(10))

    val primeSolution = LzList.eratosthenes
    println(primeSolution.takeAsList(100))
  }
}
