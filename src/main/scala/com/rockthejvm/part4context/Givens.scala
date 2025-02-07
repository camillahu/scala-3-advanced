package com.rockthejvm.part4context

object Givens {

  //list sorting
  val aList = List(4, 3, 2, 1)
  val anOrderedList = aList.sorted // (descendingOrdering) gets passed as arg because it is given

  //Ordering type is a glorified compare function.
  given descendingOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
  val anInverseOrderedList = aList.sorted(descendingOrdering)

  //when a given value is defined in a scope, the "magical" methods in the same scope will get it as an arg by default.

  //custom sorting
  case class Person(name: String, age: Int)
  val people = List(Person("Alice", 29), Person("Sarah", 34), Person("Jim", 23))

  given personOrdering: Ordering[Person] = new Ordering[Person] {
    override def compare(x: Person, y: Person): Int =
      x.name.compareTo(y.name)
  }

  val sortedPeople = people.sorted //(personOrdering) is automatically passed by the compiler.

  //equivalent of the above, just different syntax:
  object PersonAltSyntax { // (wrapped in new object to avoid double definitions) 
    given personOrdering: Ordering[Person] with {
      override def compare(x: Person, y: Person): Int =
        x.name.compareTo(y.name)
    }
  }

  //using clauses -- make a "magical" method like sorted by requiring an ordering of a particular type:
  trait Combinator[A] {
    def combine(x: A, y: A): A
  }

  //how to be able to write combineAll(List(1,2,3,4)) or combineAll(people) -- using keyword combined with a given:

  def combineAll[A](list: List[A])(using combinator: Combinator[A]): A =
    list.reduce(combinator.combine)

  given intCombinator: Combinator[Int] with {
    override def combine(x: Int, y: Int) = x + y
  }
  val firstSum = combineAll(List(1,2,3,4)) //(intCombinator) <-- passed automatically
//  val combineAllPeople = combineAll(people) // does not compile - no Combinator[Person] in scope.

  //very powerful because you can change the behavior of a program depending on where in the scope it is called,
  //and which types that are used.
  //contextual abstractions then means that the function will only work in the presence of the right
  //combinator for the right type.

  //context bound -- means that there is a given combinator of your type in scope

  def combineInGroupsOf3[A](list: List[A])(using Combinator[A]): List[A] =
    list.grouped(3).map(group => combineAll(group) /*(given Combinator[A]) passed by the compiler)*/).toList

  //alt syntax -- put Combinator in type, dont need using clause, but means the same:
    // A: Combinator means -- there is a given Combinator[A] in scope
  def combineInGroupsOf3_v3[A: Combinator](list: List[A]): List[A] =
    list.grouped(3).map(group => combineAll(group) /*(given Combinator[A]) passed by the compiler)*/).toList

  // synthesize new given instance based on existing ones
  given listOrdering(using intOrdering: Ordering[Int]): Ordering[List[Int]] with {
  override def compare(x: List[Int], y: List[Int]): Int =
    x.sum - y.sum
  }

  val listOfLists = List(List(1,2), List(1,1), List(3,4,5))
  val nestedListsOrdered = listOfLists.sorted

  // ... with generics
  given listOrderingBasedOnCombinator[A](using ord: Ordering[A])(using combinator: Combinator[A]): Ordering[List[A]] with {
    override def compare(x: List[A], y: List[A]) =
      ord.compare(combineAll(x), combineAll(y))
  } //powerful because it is available for any type of which you have an ordering and combiner in scope.


  // pass a regular value instead of the given in scope:
  val myCombinator = new Combinator[Int] {
    override def combine(x: Int, y: Int) = x * y
  }

  val listProduct = combineAll(List(1,2,3,4))(using myCombinator)

  //exercises

  //1
//  given optionOrdering[A](using ord: Ordering[A]): Ordering[Option[A]] with {
//    override def compare(x: Option[A], y: Option[A]) = (x, y) match {
//      case (None, None) => 0
//      case (None, _) => -1
//      case (_, None) => 1
//      case (Some(a), Some(b)) => ord.compare(a, b)
//    }
//  }

  //2
//  given optionOrdering_v2[A: Ordering]: Ordering[Option[A]] with {
//    override def compare(x: Option[A], y: Option[A]) = (x, y) match {
//      case (None, None) => 0
//      case (None, _) => -1
//      case (_, None) => 1
//      case (Some(a), Some(b)) => fetchGivenValue[Ordering[A]].compare(a, b)
//    }
//  }
//
//  //method to summon the ordering belonging to the correct type. Will only work if the correct ordering is in scope.
//  //this is how the summon method in scala library works:
//  def fetchGivenValue[A](using theValue: A): A = theValue

  //2 with summon
  given optionOrdering_v3[A: Ordering]: Ordering[Option[A]] with {
    override def compare(x: Option[A], y: Option[A]) = (x, y) match {
      case (None, None) => 0
      case (None, _) => -1
      case (_, None) => 1
      case (Some(a), Some(b)) => summon[Ordering[A]].compare(a, b)
    }
  }


  def main(args: Array[String]): Unit = {
    println(anOrderedList) // [1, 2, 3, 4] without given, [4, 3, 2, 1] when given.
    println(anInverseOrderedList) // [4, 3, 2, 1]
    println(List(Option(1), Option.empty[Int], Option(3), Option(-1000)).sorted)
  }
}
