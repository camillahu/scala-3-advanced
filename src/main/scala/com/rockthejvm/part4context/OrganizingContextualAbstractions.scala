package com.rockthejvm.part4context

object OrganizingContextualAbstractions {

  val aList = List(2,3,1,4)
  val anOrderedList = aList.sorted

  // how compiler fetches givens and extention methods:
  // 1- looks in LOCAL SCOPE - takes first priority
  given reverseOrdering: Ordering[Int] with {
    override def compare(x: Int, y: Int) = y - x
  }

  // 2 - looks in IMPORTED SCOPE in the absence of a defined given in local scope
  case class Person(name: String, age: Int)
  val persons = List(
    Person("Steve", 30),
    Person("Amy", 22),
    Person("John", 67)
  )

  object PersonGivens {
    given ageOrdering: Ordering[Person] with
      override def compare(x: Person, y: Person) = y.age - x.age

    extension (p: Person)
      def greet(): String = s"Hello, I'm ${p.name}, I'm so glad to meet you!"
  }

    //ways to import the given ageOrdering when it's out of scope like above:

    // a- import explicitly
      // import PersonGivens.ageOrdering

    // b- import a given for a particular type
     // import PersonGivens.given Ordering[Person]

    // c - import all givens (more general)
    // import PersonGivens.given

    //warning: import PersonGivens.* does NOT also import given instances!

  // 3 - companion of all types involved in method signature:
    // - Ordering
    // - List
    // - Person

  object Person {
    given byNameOrdering: Ordering[Person] with
      override def compare(x: Person, y: Person) =
        x.name.compareTo(y.name)

    extension (p: Person)
      def greet(): String = s"Hello, I'm ${p.name}"
  }

  val sortedPersons = persons.sorted

  // Good practice tips:
  // 1- When you have a "default" given/ only one given that makes sense for the type, add it in the companion object of the type.
  // 2 - When you have MANY possible givens, but ONE that is dominant, add that in the companion object, and keep the rest in separate objects to import where you need them.
  // 3 - When you have MANY possible givens, but none is dominant, keep them in separate objects to import where you need them.

  // Same principles apply to extension methods as well. Only difference is that import PersonGivens.* will include extension methods, but it does not include implicits.


  // exercises
  //Create given instances for Ordering[Purchase]:
  // total price - descending 50% usage, (nUnits * unitPrice)
  // unit count - descending 25% usage,
  // unit price - ascending 25% usage

  case class Purchase(nUnits: Int, unitPrice: Double)

  object Purchase {
    given totalPriceOrdering: Ordering[Purchase] with
      override def compare(x: Purchase, y: Purchase) = {
        val yTotalPrice = y.nUnits * y.unitPrice
        val xTotalPrice = x.nUnits * x.unitPrice

        if(xTotalPrice == yTotalPrice) 0
        else if (xTotalPrice < yTotalPrice) -1
        else 1
      }
  }

  object unitCountPurchase {
    given unitCountPurchaseOrdering: Ordering[Purchase] = Ordering.fromLessThan((x,y) => y.nUnits > x.nUnits)
  }

  object unitPricePurchase {
    given unitPricePurchaseOrdering: Ordering[Purchase] = Ordering.fromLessThan((x,y) => x.unitPrice < y.unitPrice)
  }

  def main(args: Array[String]): Unit = {
    println(sortedPersons)
    import PersonGivens.*
    println(Person("Daniel", 99).greet())
  }
}
