package com.rockthejvm.practise

import javax.sql.rowset.Predicate
import scala.annotation.tailrec

//Write a lazily evaluated, potentially INFINITE linked list
abstract class LzList[A] {
  def isEmpty: Boolean
  def head: A
  def tail: LzList[A]

  //utils
  infix def #::(element: A): LzList[A] //prepending
  infix def ++(another: LzList[A]): LzList[A] // TODO warning

  //classics
  def foreach(f: A => Unit):Unit
  def map[B](f:A => B):LzList[B]
  def flatMap[B](f: A => LzList[B]):LzList[B]
  def filter(predicate: A => Boolean): LzList[A]
  def withFilter(predicate: A => Boolean): LzList[A] = filter(predicate)

  def take(n:Int): LzList[A] //takes first n elements from this lazy list
  def takeAsList(n:Int): List[A]
  def toList: List[A] //use this carefully, can overflow
}

case class LzEmpty[A]() extends LzList[A] {
  def isEmpty: Boolean = true

  def head: A = throw new NoSuchElementException("Empty LzList has no head")

  def tail: LzList[A] = throw new NoSuchElementException("Empty LzList has no tail")

  //utils
  infix def #::(element: A): LzList[A] = new LzCons[A](element, LzEmpty())

  infix def ++(another: LzList[A]): LzList[A] = another

  //classics
  def foreach(f: A => Unit): Unit = ()

  def map[B](f: A => B): LzList[B] = LzEmpty[B]()

  def flatMap[B](f: A => LzList[B]): LzList[B] = LzEmpty[B]()

  def filter(predicate: A => Boolean): LzList[A] = this

  def take(n: Int): LzList[A] = throw new UnsupportedOperationException("Cannot take from Empty LzList")

  def takeAsList(n: Int): List[A] = throw new UnsupportedOperationException("Cannot take from Empty LzList")

  def toList: List[A] = List()
}

class LzCons[A](hd: => A, tl: => LzList[A]) extends LzList[A] {
  def isEmpty: Boolean = false

  lazy val head: A = hd

  lazy val tail: LzList[A] = tl

  //utils
  infix def #::(element: A): LzList[A] = new LzCons[A](element, this)

  infix def ++(another: LzList[A]): LzList[A] = new LzCons(this.head, this.tail++another)

  //classics
  def foreach(f: A => Unit): Unit = {
    f(this.head)
    tail.foreach(f)
  }

  def map[B](f: A => B): LzList[B] = {
    new LzCons[B](f(this.head), this.tail.map(f))
  }

  def flatMap[B](f: A => LzList[B]): LzList[B] = {
    f(head) ++ tail.flatMap(f)
  }

  def filter(predicate: A => Boolean): LzList[A] = {
    if(!predicate(head)) tail.filter(predicate)
    else head #:: tail.filter(predicate)
  }

  def take(n: Int): LzList[A] = {
      if (n == 0) this
      else this.head #:: tail.take(n-1)
  }

  def takeAsList(n: Int): List[A] = {
    @tailrec
    def collect(lz:LzList[A], count:Int, aList:List[A]):List[A] = {
      if(count == 0) aList
      else collect(lz.tail, count-1, lz.head :: aList)
    }
    collect(this.take(n), n, List.empty)
  }
 

  def toList: List[A] =  ???//use this carefully, can overflow
}

object LzList {
  def generate[A](start: A)(generator: A => A): LzList[A] = ???
  def from[A](list: List[A]): LzList[A] = ???
}

object LzListPlayground {
  def main(args: Array[String]): Unit = {
    val naturals = LzList.generate(1)(n => n + 1) //INFINITE list of natural numbers
  }
}
