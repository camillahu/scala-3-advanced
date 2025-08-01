package com.rockthejvm.practise

import java.util.Date

object JSONSerialization {

  /* USE CASE:
     We are designing a small library for an application that allows users to share content
      and serializes those to JSON for a web UI to interoperate.

      Users, blog posts, feeds

  */

  case class User(name: String, age: Int, email: String)
  case class Post(content: String, createdAt: Date)
  case class Feed(user: User, posts: List[Post])

  /*
    1 - intermediate data: numbers, strings, lists, objects
    2 - type class to convert data to intermediate data
    3 - serialize to JSOm
  * */


  //STEP 1:
  sealed trait JSONValue {
    def stringify: String
  }

  final case class JSONString(value: String) extends JSONValue {
    override def stringify: String = "\"" + value + "\""
  }

  final case class JSONNumber(value: Int) extends JSONValue {
    override def stringify = value.toString
  }

  final case class JSONArray(values: List[JSONValue]) extends JSONValue {
    override def stringify: String = values.map(_.stringify).mkString("[", ",", "]") //creating a json representation of an array
  }

  final case class JSONObject(values: Map[String, JSONValue]) extends  JSONValue {
    override def stringify: String = values.map {
      case (key, value) => "\"" + key + "\":" + value.stringify
    }
      .mkString("{", ",", "}")
  }

  val data = JSONObject(Map(
    "user" -> JSONString("Daniel"),
    "posts" -> JSONArray(List(
      JSONString("Scala is awesome!"),
      JSONNumber(42)
    ))
  ))

  //STEP 2:

  //part 1 - type class definition
  trait JSONConverter[T] {
    def convert(value: T): JSONValue
  }

  // part 2 - type class instances for the types we want to support: String, Int, Date, User, Post, Feed
  given stringConverter: JSONConverter[String] with
    override def convert(value: String): JSONValue = JSONString(value)

  given intConverter: JSONConverter[Int] with
    override def convert(value: Int): JSONValue = JSONNumber(value)

  given dateConverter: JSONConverter[Date] with
    override def convert(value: Date): JSONValue = JSONString(value.toString)

  given userConverter: JSONConverter[User] with
    override def convert(user: User) = JSONObject(Map(
      "name" -> JSONConverter[String].convert(user.name),
      "age" -> JSONConverter[Int].convert(user.age),
      "email" -> JSONConverter[String].convert(user.email)
    ))

  given postConverter: JSONConverter[Post] with
    override def convert(post: Post): JSONValue = JSONObject(Map(
      "content" -> JSONConverter[String].convert(post.content),
      "createdAt" -> JSONConverter[String].convert(post.createdAt.toString)
    ))

  given feedConverter: JSONConverter[Feed] with
    override def convert(feed: Feed): JSONValue = JSONObject(Map(
      "user" -> JSONConverter[User].convert(feed.user),
      "posts" -> JSONArray(feed.posts.map(post => JSONConverter[Post].convert(post)))
    ))

  // par3 3 - user-facing API
  object JSONConverter {
      def convert[T](value: T)(using converter: JSONConverter[T]): JSONValue =
        converter.convert(value)

      def apply[T](using instance: JSONConverter[T]): JSONConverter[T] = instance
  }

  //example
  val now = new Date(System.currentTimeMillis())
  val john = User("John", 34, "john@rockthejvm.com")
  val feed = Feed(john, List(
    Post("Hello, I'm learning type classes", now),
    Post("Look at this cute puppy!", now)
  ))

  //part 4 - extension methods
  object JSONSyntax {
    extension[T](value: T) {
      def toIntermediate(using converter: JSONConverter[T]): JSONValue =
        converter.convert(value)

      def toJSON(using converter: JSONConverter[T]): String =
        toIntermediate.stringify
    }
  }

  def main(args: Array[String]): Unit = {
    import JSONSyntax.*
    println(feed.toJSON)
  }
}
