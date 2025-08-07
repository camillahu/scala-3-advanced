package com.rockthejvm.part5ts

object TypeMembers {

  class Animal
  class Dog extends Animal
  class Cat extends Animal

  class AnimalCollection {
    // val, var, def, class, trait, object -- you can also add abstract type members
    type AnimalType
    type BoundedAnimal <: Animal // abstract type member with a type bound
    type SuperBoundedAnimal >: Dog <: Animal
    type AnimalAlias = Cat // type alias
    type NestedOption = List[Option[Option[Int]]] // often used to alias complex/nested types
  }

  // using type members
  val ac = new AnimalCollection
  val anAnimal: ac.AnimalType = ???

  // val cat: ac.BoundedAnimal = new Cat -- does not compile, BoundedAnimal might be Dog.
  val aDog: ac.SuperBoundedAnimal = new Dog // ok, Dog <: SuperBoundedAnimal
  val aCat: ac.AnimalAlias = new Cat // ok, Cat == AnimalAlias

  // why is this useful?
  // - establish relationships between types
  // - alternative to generics

  class LList[T] {
    def add(element: T): LList[T] = ???
  }

  class MyList {
    type T
    def add(element: T): MyList = ???
  }

  // .type - surface the type of something
  type CatType = aCat.type
  val newCat: CatType = aCat

  def main(args: Array[String]): Unit = {

  }
}
