package com.rockthejvm.part5ts

object Variance {

  class Animal
  class Dog(name: String) extends Animal

  // Variance question: if Dog extends Animal, then should a List[Dog] "extend" List[Animal]?

  // for List, YES - List is COVARIANT
  val lassie = new Dog("Lassie")
  val hachi = new Dog("Hachi")
  val laika = new Dog("Laika")

  val anAnimal: Animal = lassie // ok, Dog <: Animal
  val myDogs: List[Animal] = List(lassie, hachi, laika) // ok - List is COVARIANT: a list of dogs is a list of animals

  // define covariant types
  class MyList[+A] // MyList is COVARIANT in A
  val aListOfAnimals: MyList[Animal] = new MyList[Dog]

  // if the answer to the covariant question is NO, the type is INVARIANT
  trait Semigroup[A] { // no marker = INVARIANT
    def combine(x: A, y: A): A

  }

  //java generics are all invariant
  // val aJavaList: java.util.ArrayList[Animal] = new java.util.ArrayList[Dog] - type mismatch

  // Hell MO - CONTRAVARIANCE
  // if Dog <: Animal, then Vet[Animal] <: Vet[Dog]
  // - a vet who can heal all animals can heal a dog, but a vet who can heal just dog, cannot heal all animals.
  trait Vet[-A] { // contravariant in A
    def heal(animal:A): Boolean
  }

  val myVet: Vet[Dog] = new Vet[Animal] {
    override def heal(animal: Animal) = {
      println("Hey there, you're all good...")
      true
    }
  }
  val healLaika = myVet.heal(laika)

  // Rule of thumb picking variant:
  // - if your type PRODUCES or RETRIEVES a value (e.g. a list), then it should be COVARIANT +
  // - if your type ACTS ON or CONSUMES a value (e.g. a vet), them it should be CONTRAVARIANT -
  // - otherwise, INVARIANT

  // 1 - which types should be invariant, contravariant, covariant
  class RandomGenerator[+A]  //Covariant
  class MyOption[+A] //Covariant
  class JSONSerializer[-A] //Contravariant
  trait MyFunction[-A, +B] //Contravariant and Covariant

  //2 - add variance modifiers to "library"
  abstract class LList[+A] {
    def head: A
    def tail: LList[A]
  }

  case object EmptyList extends LList[Nothing] {
    override def head = throw new NoSuchElementException
    override def tail = throw new NoSuchElementException
  }

  case class Cons[+A](override val head: A, override val tail: LList[A]) extends LList[A]

  val aList: LList[Int] = EmptyList // fine - even though it has type Nothing
  val anotherList: LList[String] = EmptyList // also fine
  // because Nothing <: A, then a LList[Nothing] <: LList[A]

  def main(args: Array[String]): Unit = {

  }
}
