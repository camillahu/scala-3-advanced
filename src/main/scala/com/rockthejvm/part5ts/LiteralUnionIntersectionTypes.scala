package com.rockthejvm.part5ts

object LiteralUnionIntersectionTypes {

  // 1- literal types
  val aNumber = 3
  val three: 3 = 3 //3, which is a subtype of int is now its own type.

  def passNumber(n: Int) = println(n)
  passNumber(45) // ok
  passNumber(three) // ok because 3 <: Int

  def passStrict(n: 3) = println(n)
  passStrict(3)
  passStrict(three)
  // passStrict(45) -- not ok, Int isn't a subtype of 3.

  // available for double, boolean, strings
  val pi: 3.14 = 3.14
  val truth: true = true
  val favLang: "Scala" = "Scala"

  // literal types can be used as type arguments (just like any other types)
  def doSomethingWithYourLife(meaning: Option[42]) = meaning.foreach(println)

  //2 - union types
  val truthor42: Boolean | Int = 43

  def ambivalentMethod(arg: String | Int) = arg match {
    case _: String => "a string"
    case _: Int => "a number"
  }

  val aNumberDescription = ambivalentMethod(56) //ok
  val aStringDescription = ambivalentMethod("Scala") // ok

  // type inference with union types - chooses lowest common ancestor of the two types instead of String | Int
  val stringOrInt = if (43 > 0) "a string" else 45 //any
  val stringOrInt_v2: String | Int = if (43 > 0) "a string" else 45 //String | Int when specified

  // union types are useful for compile time checking with null
  type Maybe[T] = T | Null //not null itself, but the Null type.
  def handleMaybe(someValue: Maybe[String]): Int =
    if(someValue != null) someValue.length //flow typing, only available if one of the union types is Null.
    else 0

  // 3 - intersection types
  class Animal
  trait Carnivore
  class Crocodile extends Animal with Carnivore

  val carnivoreAnimal: Animal & Carnivore = new Crocodile


  trait Gadget {
    def use(): Unit
  }

  trait Camera extends Gadget {
    def takePicture() = println("smile")
    override def use() = println("snap")
  }

  trait Phone extends Gadget {
    def makePhoneCall() = println("calling...")
    override def use() = println("ring")
  }

  //can call methods from both interfaces
  def useSmartDevice(sp: Camera & Phone): Unit = {
    sp.takePicture()
    sp.makePhoneCall()
    sp.use() // which use() is being called? can't tell from implementation alone
  }

  class SmartPhone extends Phone with Camera //diamond problem
  class CameraWithPhone extends Camera with Phone

  //intersection types + covariance
  trait HostConfig
  trait HostController {
    def get: Option[HostConfig]
  }

  trait PortConfig
  trait PortController {
    def get: Option[PortConfig]
  }

  def getConfigs(controller: HostController & PortController): Option[HostConfig & PortConfig] = controller.get
  // code compiles even though they have conflicting method definitions of get()
  // returns both, possible because option is covariant.

  def main(args: Array[String]): Unit = {
    useSmartDevice(new SmartPhone)
    useSmartDevice(new CameraWithPhone)
  }
}
