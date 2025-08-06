package com.rockthejvm.part4context

object Implicits {
  //scala 2 contextual abstractions

  // given/using - the ability to pass arguments automatically (implicitly) by the compiler
  trait Semigroup[A] {
    def combine(x: A, y: A): A
  }

  def combineAll[A](list: List[A])(implicit semigroup: Semigroup[A]): A = {
    list.reduce(semigroup.combine)
  }

  implicit val intSemigroup: Semigroup[Int] = new Semigroup[Int] {
    override def combine(x: Int, y: Int) = x + y
  }

  val sumOf10 = combineAll((1 to 10).toList)

  //implicit arg (scala 2) -> using clause (scala 3)
  //implicit val (scala 2) -> given declaration (scala 3)



  // extension methods

  implicit class MyRichInteger(number: Int) {
    //extension methods here
    def isEven = number % 2 == 0
  }

  val questionOfMyLife = 23.isEven // new MyRichInteger(23).isEven

  //implicit class (scala 2) -> extension methods (scala 3)


  // implicit conversions

  case class Person(name: String) {
    def greet(): String = s"Hi, my name is $name."
  }

  // implicit conversion - SUPER DANGEROUS
  implicit def string2Person(x: String): Person = Person(x)
  val danielSaysHi = "Daniel".greet() // string2Person("Daniel").greet()

  //implicit def => synthesize NEW implicit values
  implicit def semigroupOfOption[A](implicit semigroup: Semigroup[A]): Semigroup[Option[A]] = new Semigroup[Option[A]] {
    override def combine(x: Option[A], y: Option[A]) = for {
      valueX <- x
      valueY <- y
    } yield semigroup.combine(valueX, valueY)
  }

  def main(args: Array[String]): Unit = {

  }
}
