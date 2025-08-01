package com.rockthejvm.part4context

object TypeClasses {

  //Example: Small library to serialize some data to a standard format (HTML)

  // V1: The object-oriented way:
  trait HTMLWritable {
    def toHtml: String
  }

  case class User(name: String, age: Int, email: String) extends HTMLWritable {
    override def toHtml: String = s"<div>$name ($age yo) <a href=$email/></div>"
  }

  val bob = User("Bob", 43, "bob@rockthejvm.com")
  val bobToHtml = bob.toHtml

  //Drawbacks of the OO way:
  // - only available for the types we write
  // - can only provide ONE implementation

  // V2: Scala specific - pattern matching, simplest to implement
  object HTMLSerializerPM {
    def serializeToHtml(value: Any) = value match {
      case User(name, age, email) => s"<div>$name ($age yo) <a href=$email/></div>"
      case _ => throw new IllegalArgumentException("data structure not supported")
    }
  }

  //Drawbacks:
  // - lost type safety because we can pass ANY type as an argument, can get errors
  // - need to modify a SINGLE piece of code every time
  // - still ONE implementation

  //V3 - type class instances for the supported types

  // part 1 - type class definition
    trait HTMLSerializer[T] {
      def serialize(value:T): String
    }

  //part 2 - type class instances for the supported types
    given userSerializer: HTMLSerializer[User] with {
      override def serialize(value: User) = {
        val User(name, age, email) = value
        s"<div>$name ($age yo) <a href=$email/></div>"
      }
    }

    val bob2Html_v2 = userSerializer.serialize(bob)

    // Benefits:
    // - can define serializers for other types OUTSIDE the "library" because the definition is separate from the type class itself.
    // - multiple serializers for the same type, pick whichever you want.

    //Examples:
    import java.util.Date
    given dateSerializer: HTMLSerializer[Date] with {
      override def serialize(date: Date) = s"<div>${date.toString}</div>"
    }

    object SomeOtherSerializerFunctionality { // organize givens properly
      given partialUserSerializer: HTMLSerializer[User] with {
        override def serialize(user: User) = s"<div>${user.name}</div>"
      }
    }

  //part 3 - using the type class (user-facing API)
    object HTMLSerializer  {
      def serialize[T](value: T)(using serializer: HTMLSerializer[T]): String =
        serializer.serialize(value)

      def apply[T](using serializer: HTMLSerializer[T]): HTMLSerializer[T] = serializer
      //surfaces out the given instance in scope for that type class
    }

    val bob2Html_v3 = HTMLSerializer.serialize(bob)
    val bob2Html_v4 = HTMLSerializer[User].serialize(bob)

  //part 4 - define extension methods specific for our type class - makes the code more expressive and readable
    object HTMLSyntax {
      extension [T](value: T)
        def toHTML(using serializer: HTMLSerializer[T]): String = serializer.serialize(value)
    }

    import HTMLSyntax.*
    val bob2Html_v5 = bob.toHTML

  def main(args: Array[String]): Unit = {
    println(bob2Html_v2)
    println(bob2Html_v3)
    println(bob2Html_v5)
  }
}
