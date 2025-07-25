package com.rockthejvm.practise

abstract class FSet[A] extends (A => Boolean) {
  //main api
  def contains(elem: A): Boolean
  def apply(elem: A): Boolean = contains(elem)
  
  infix def +(elem: A): FSet[A] //add
  infix def ++(anotherSet: FSet[A]): FSet[A] //union
  
  def map[B](f: A => B): FSet[B]
  def flatMap[B](f: A => FSet[B]): FSet[B]
  def filter(predicate: A => Boolean): FSet[A]
  def foreach(f: A => Unit): Unit
  
}

case class Empty[A]() extends FSet[A] {
  override def contains(elem: A): Boolean = false

  override infix def +(elem: A): FSet[A] = Cons(elem, this)
  override infix def ++(anotherSet: FSet[A]): FSet[A] = anotherSet

  override def map[B](f: A => B): FSet[B] = Empty()
  override def flatMap[B](f: A => FSet[B]): FSet[B] = Empty()
  override def filter(predicate: A => Boolean): FSet[A] = this
  override def foreach(f: A => Unit): Unit = ()
}

case class Cons[A](head: A, tail: FSet[A]) extends FSet[A] {
  override def contains(elem: A): Boolean = elem == head || tail.contains(elem)
  
  override infix def +(elem: A): FSet[A] = {
    if(contains(elem)) this
    else Cons(elem, this)
  }

  override infix def ++(anotherSet: FSet[A]): FSet[A] = tail ++ anotherSet + head

  override def map[B](f: A => B): FSet[B] = tail.map(f) + f(head)

  override def flatMap[B](f: A => FSet[B]): FSet[B] = tail.flatMap(f) ++ f(head)

  override def filter(predicate: A => Boolean): FSet[A] = {
    val filteredTail = tail.filter(predicate)
    if(predicate(head)) filteredTail + head
    else filteredTail
  }

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }
}

object FSet {
  def apply[A](values: A*): FSet[A] = {
    def buildSet(valuesSeq: Seq[A], acc: FSet[A]): FSet[A] =
      if(valuesSeq.isEmpty) acc
      else buildSet(valuesSeq.tail, acc + valuesSeq.head)

    buildSet(values, Empty())
  }
}

object FunctionalSetPlayground_v2 {

  def main(args: Array[String]): Unit = {
    val first5 = FSet(1,2,3,4,5)
    println(first5.contains(5))
  }

}
