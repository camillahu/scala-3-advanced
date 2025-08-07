package com.rockthejvm.part5ts

object AdvancedInheritance {

  // composite types can be used on their own
  trait Writer[T] {
    def write(value:T): Unit
  }

  trait Stream[T] {
    def foreach(f: T => Unit): Unit
  }

  trait Closeable {
    def close(status: Int): Unit
  }

  //instead of this:
  // class MyDataStream extends Writer[String] with Stream[String] with Closeable { ... }

  //you can do this:
  def processStream[T](stream: Writer[T] with Stream[T] with Closeable): Unit = {
    stream.foreach(println)
    stream.close(0)
  }

  // 2 - diamond problem

  trait Animal  { def name: String}
  trait Lion extends Animal { override def name = "Lion"}
  trait Tiger extends Animal { override def name = "Tiger"}
  class Liger extends Lion with Tiger

  def demoLiger(): Unit = {
    val liger = new Liger
    println(liger.name)
  }

  //will print Tiger because it is the last override.

  // 3 - the super problem

  trait Cold { // cold colors
    def print() = println("cold")
  }

  trait Green extends Cold {
    override def print(): Unit = {
      println("green")
      super.print()
    }
  }

  trait Blue extends Cold {
    override def print(): Unit = {
      println("blue")
      super.print()
    }
  }

  class Red {
    def print() = println("red")
  }

  class White extends Red with Green with Blue {
    override def print(): Unit = {
      println("white")
      super.print()
    }
  }

  def demoColorInheritance(): Unit = {
    val white = new White
    white.print()
  } //Will not print red as we might think. Will print everything but red because of how the compiler works

  def main(args: Array[String]): Unit = {

  }
}
