package com.rockthejvm.part5ts

object PathDependentTypes {

  class Outer {
    class Inner
    object InnerObject
    type InnerType

    def process(arg: Inner) = println(arg)
    def processGeneral(arg: Outer#Inner) = println(arg) //can be run with all the inner types we define
  }

  val outer = new Outer
  val inner = new outer.Inner // outer.Inner is a separate TYPE = path-dependent type which is dependent on an instance of Inner.

  val outerA = new Outer
  val outerB = new Outer
  // val inner2: outerA.Inner = new outerB.Inner -- type mismatch! path-dependent types are different.

  //correct way:
  val innerA = new outerA.Inner
  val innerB = new outerB.Inner

  // outerA.process(innerB) -- type mismatch!

  //correct way:
  outer.process(inner)

  //n parent-type: Outer#Inner
  outerA.processGeneral(innerA) // ok
  outerA.processGeneral(innerB) // ok, outerB.Inner <: Outer#Inner

  // why are these things useful?
  // - type-checking/type inference (for example in libraries)
  // - type-level programming

  // methods with dependent types: return a different compile-time type depending on the argument (without generics)

  trait Record {
    type Key
    def defaultValue: Key
  }

  class StringRecord extends Record {
    override type Key = String
    override def defaultValue = ""
  }

  class IntRecord extends Record {
    override type Key = Int
    override def defaultValue = 0
  }

  // user-facing API -- this gets rid of having to use generics.

  def getDefaultIdentifier(record: Record): record.Key = record.defaultValue

  // compiler can figure out the type depending on the value passed to it.

  val aString: String = getDefaultIdentifier(new StringRecord) // a String
  val anInt: Int = getDefaultIdentifier(new IntRecord) // an Int

  val getIdentifierFunc: Record => Record#Key = getDefaultIdentifier //eta-expansion

  def main(args: Array[String]): Unit = {

  }
}
