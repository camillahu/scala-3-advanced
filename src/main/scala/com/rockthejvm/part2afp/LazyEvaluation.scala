package com.rockthejvm.part2afp

object LazyEvaluation {

  val x: Int = {
    println("Hello")
    42
  }

  //lazy delays the evaluation of a value until the first use
  //evaluation occurs once
  lazy val lazyX: Int = {
    println("Lazy Hello")
    42
  } //does not print anything until x is used for the first time

  //example witg call by need  = call by name + lazy values

  def byNameMethod(n: => Int): Int = {
    n + n + n + 1
  }

  def retrieveMagicValue() = {
    println("waiting...")
    Thread.sleep(1000)
    42
  }

  def demoByName(): Unit = {
    println(byNameMethod(retrieveMagicValue()))
    // retrieveMagicValue() + retrieveMagicValue() + retrieveMagicValue() + 1
  }

  def byNeedMethod(n: => Int): Int = {
    lazy val lazyN = n //memoization -- a technique where results are stored to avoid doing the same computations many times
    lazyN + lazyN + lazyN + 1 
  }

  def demoByNeed(): Unit = {
    println(byNeedMethod(retrieveMagicValue()))
  }

  //example with filter

  def lessThan30(i: Int): Boolean = {
    println(s"$i is less than 30?")
    i < 30
  }

  def greaterThan20(i: Int): Boolean = {
    println(s"$i is less than 20?")
    i > 20
  }

  val numbers = List(1, 25, 40, 5, 23)

  def demoFilter(): Unit = {
    val lt30 = numbers.filter(lessThan30)
    val gt20 = lt30.filter(greaterThan20)
    println(gt20)
    //filter will print:
    // 1 is less than 30?
    //25 is less than 30?
    //1 is less than 20?
    //25 is less than 20?
  }

  def demoWithFilter(): Unit = {
    val lt30 = numbers.withFilter(lessThan30)
    val gt20 = lt30.withFilter(greaterThan20)
    println(gt20.map(x => x))
    //needs map to print bco withFilter
    //withFilter uses lazy and will evaluate one number against both predicates before going to the next one.
    //ex: 1 is less than 30?
    //1 is less than 20?
    //25 is less than 30?
    //25 is less than 20?
  }

  def demoWithForComprehension(): Unit = {
    val forComp = for {
      n <- numbers if lessThan30(n) && greaterThan20(n)
    }yield n
    println(forComp)
  }


  def main(args: Array[String]): Unit = {
//    println(x)
//    println(s"Lazy $lazyX")
//    println(s"Lazy $lazyX")
//    demoByName() //waiting x3
//    demoByNeed() //waiting x1

//    demoFilter()
//    demoWithFilter()
    demoWithForComprehension()
  }
}
