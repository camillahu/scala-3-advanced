package com.rockthejvm.part5ts

object SelfTypes {

  trait Instrumentalist {
    def play(): Unit
  }

  trait Singer { self: Instrumentalist => // self-type -- whatever implements Singer, must also implement Instrumentalist
                //name "self" can be anything, usually called self
                // DO NOT confuse this with a lambda, not at all the same thing.

    //rest of api
    def sing(): Unit
  }

  class LeadSinger extends Singer with Instrumentalist { // ok
    override def play(): Unit = ???
    override def sing(): Unit = ???
  }

//  class Vocalist extends Singer { -- will not compile, illegal inheritance because we've not extended Instrumentalist
//
//  }

  val jamedHetfield = new Singer with Instrumentalist:
    override def sing(): Unit = ???
    override def play(): Unit = ???

  class Guitarist extends Instrumentalist {
    override def play(): Unit = println("some guitar solo")
  }

  val ericClapton = new Guitarist with Singer  { //ok - extending Guitarist <: Instrumentalist
    override def sing(): Unit = println("lalala")
  }

  // self-types vs inheritance
  class A
  class B extends A //B "is an" A.

  trait T
  trait S { self: T =>} //S "requires a" T.

  // self-types for an alternative to Dependency Injection = "cake pattern":
  class Component {
    // main general API
  }
  class ComponentA extends Component
  class ComponentB extends Component
  class DependentComponent(val component: Component) // regular dependency injection

  // cake pattern
  trait ComponentLayer1 {
    //API
    def actionLayer1(x: Int): String
  }

  trait ComponentLayer2 { self: ComponentLayer1 =>
    // some other API
    def actionLayer2(x: String): Int
  }
  trait Application { self: ComponentLayer1 with ComponentLayer2 =>
    //your main API
  }

  //example - a photo taking app API in the style of Instagram:

  // layer 1 - small components
  trait Picture extends ComponentLayer1
  trait Stats extends ComponentLayer1

  // layer 2 - compose
  trait ProfilePage extends ComponentLayer2 with Picture
  trait Analytics extends ComponentLayer2 with Stats

  // layer 3 - main app
  trait AnalyticsApp extends Application with Analytics
  // dependencies are specified in layers, like baking a cake
  // when you put the pieces together, you can pick a possible implementation from each layer

  // another example on where you might use self-types: preserve the "this" instance
  class SingerWithInnerClass { self => // self-type with no type requirements, self == this

    class Voice {
      def sing() = this.toString // this == the voice, use "self" to refer to outer instance
    }
  }

  // cyclical inheritance does not work
//  class X extends Y
//  class Y extends X -- will not compile

//cyclical dependencies
  trait X { self: Y => }
  trait Y { self: X => }
  trait Z extends X with Y // all good




  def main(args: Array[String]): Unit = {

  }
}
