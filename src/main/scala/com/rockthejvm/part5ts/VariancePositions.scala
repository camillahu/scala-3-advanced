package com.rockthejvm.part5ts

object VariancePositions {

  class Animal
  class Dog extends Animal
  class Cat extends Animal
  class Crocodile extends Animal

  // 1- type bounds
  class Cage[A <: Animal] // A must be a subtype of animal
  // val aCage = new Cage[String] --- not ok, String is not a subtype of animal

  class WeirdContainer[A >: Animal] // A must be a supertype of animal

  // 2 - variance positions
  // class Vet[-T](val favoriteAnimal: T) -- compiler won't let us, type of val/var fields are in Covariant position

  //example explaining  why this doesn't work:
  /*
    val garfield = new Cat
    val theVet: Vet[Animal] = new Vet[Animal](garfield)
    val aDogVet: Vet[Dog] = theVet --- possible, theVet is Vet[Animal]
    val aDog: Dog = aDogVet.favoriteAnimal -- must be a Dog, type conflict!
  * */

  // types of var fields are ALSO in contravariant position:
  // class MutableOption[+T](var contents: T)

  /*

    val maybeAnimal: MutableOption[Animal] = new MutableOption[Dog](new Dog)
    maybeAnimal.contents = new Cat --- type conflict!
  * */


  // types of method arguments are in CONTRAVARIANT position
  //  class MyList[+T] {
  //    def add(element: T): MyList[T] = ???
  //  }

  /*
  * val animals: MyList[Animal] = new MyList[Cat]
  * val biggerListOfAnimals = animals.add(new Dog] --- type conflict!
  * */

  //this will work because Vet CONSUMES the value:
  class Vet[-T] {
    def heal(animal: T): Boolean = true
  }

  // method return types are in COVARIANT position
  //  abstract class Vet2[-T] {
  //    def rescueAnimal(): T
  //  }

  /*
  * val vet: Vet2[Animal] = new Vet2[Animal] {
    override def rescueAnimal(): Animal = new Cat
  }
    val lassiesVet: Vet2[Dog] = vet // Vet2[Animal]
  val rescueDog: Dog = lassiesVet.rescueAnimal() // must return a Dog, returns a Cat --- type conflict!
  * */

  // 3- solving variance position problems

  abstract class LList[+A] {
    def head: A
    def tail: LList[A]
    def add[B >: A](element: B): LList[B] //widen the type, make a new type that is a subtype of original type.
  }

  // val animals: List[Cat] = list of cats
  // val newAnimals: Lis[Animal] = animals.add(new Dog)

  class Vehicle
  class Car extends Vehicle
  class Supercar extends Car
  class RepairShop[-A <: Vehicle] {
    def repair[B <: A](vehicle: B): B = vehicle //narrowing the type, make a new type that is a supertype of original type.
  }

  val myRepairShop: RepairShop[Car] = new RepairShop[Vehicle]
  val myBeatupVW = new Car
  val freshCar = myRepairShop.repair(myBeatupVW) // works, returns a car
  val damagedFerrari = new Supercar
  val freshFerrari = myRepairShop.repair(damagedFerrari) // works, returns a supercar




  def main(args: Array[String]): Unit = {

  }
}
