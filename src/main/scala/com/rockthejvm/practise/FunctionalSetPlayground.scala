package com.rockthejvm.practise

import scala.annotation.tailrec

abstract class FSet[A] extends (A => Boolean) {

  def contains(elem: A): Boolean

  def apply(elem: A): Boolean = contains(elem)

  infix def +(elem: A): FSet[A]

  infix def ++(anotherSet: FSet[A]): FSet[A]

  def map[B](f: A => B): FSet[B]

  def flatMap[B](f: A => FSet[B]): FSet[B]

  def filter(predicate: A => Boolean): FSet[A]

  def foreach(f: A => Boolean): Unit
}

case class Empty[A]() extends FSet[A] {
  override def contains(elem: A) = false

  infix def +(elem: A): FSet[A] = Cons(elem, this)

  infix def ++(anotherSet: FSet[A]): FSet[A] = anotherSet

  def map[B](f: A => B): FSet[B] = Empty()

  def flatMap[B](f: A => FSet[B]): FSet[B] = Empty()

  def filter(predicate: A => Boolean): FSet[A] = this

  def foreach(f: A => Boolean): Unit = ()
}

case class Cons[A](head: A, tail: FSet[A]) extends FSet[A] {
  override def contains(elem: A): Boolean = elem == head || tail.contains(elem)

  infix def +(elem: A): FSet[A] =
    if(contains(elem)) this
    else Cons(elem, this)

  infix def ++(anotherSet: FSet[A]): FSet[A] = tail ++ anotherSet + head

  def map[B](f: A => B): FSet[B] = tail.map(f) + f(head)

  def flatMap[B](f: A => FSet[B]): FSet[B] = tail.flatMap(f) ++ f(head)

  def filter(predicate: A => Boolean): FSet[A] = {
    val filteredTail = tail.filter(predicate)
    if(predicate(head)) filteredTail + head
    else filteredTail
  }

  def foreach(f: A => Boolean): Unit ={
    f(head)
    tail.foreach(f)
  }
}

object FSet {
  def apply[A](values: A*): FSet[A] = {
    @tailrec
    def buildSet(valuesSeq: Seq[A], acc: FSet[A]): FSet[A] =
      if(valuesSeq.isEmpty) acc
      else buildSet(valuesSeq.tail, acc + valuesSeq.head)

    buildSet(values, Empty())
  }
}

object FunctionalSetPlayground {


  def main(args: Array[String]): Unit = {
    val first5 = FSet(1,2,3,4,5)
    println(first5.contains(5)) //true
    println(first5(6)) //false
    println((first5 + 10).contains(10)) //true
    println(first5.map(_ * 2).contains(10)) //true
    println(first5.map(_ % 2).contains(1)) //true
    println(first5.flatMap(x => FSet(x, x+1)).contains(7)) //false
  }
}
