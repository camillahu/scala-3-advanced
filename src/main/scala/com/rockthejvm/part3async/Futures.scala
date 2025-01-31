package com.rockthejvm.part3async

import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Random, Success, Try}

object Futures {

  def calculateMeaningOfLife(): Int = {
    // simulate long compute
    Thread.sleep(1000)
    42
  }

  //thread pool (java specific)
  val executor = Executors.newFixedThreadPool(4)
  // thread pool (Scala-specific) -- wrapper over executor service (from java)
  given executionContext: ExecutionContext = ExecutionContext.fromExecutorService(executor)

  // a future = an async computation that will finish at some point
  val aFuture: Future[Int] = Future.apply(calculateMeaningOfLife()) //given executionContext will be passed here

  //why this type?
  // - we don't know if we have a value(Option)
  // - if we do, that can be a failed computation(Try)
  val futureInstantResult: Option[Try[Int]] = aFuture.value

  //callbacks
  aFuture.onComplete {
    case Success(value) => println(s"I've completed with the meaning of life $value")
    case Failure(exception) => println(s"My async computation failed $exception")
  } //will be evaluated on some other thread-- have no control over when or which threads

  /*
  * Functional composition
  * */

  case class Profile(id:String, name: String) {
    def sendMessage(anotherProfile: Profile, message: String) =
      println(s"$this.name sending message to  ${anotherProfile.name}: $message")
  }

  object SocialNetwork {
    //"database"
    val names = Map(
      "rtjvm.id.1-daniel" -> "Daniel",
      "rtjvm.id.2-jane" -> "Jane",
      "rtjvm.id.3-mark" -> "Mark"
    )

    //friends "database"
    val friends = Map(
      "rtjvm.id.2-jane" -> "rtjvm.id.3-mark"
    )

    val random = new Random()

    //"API"
    def fetchProfile(id: String): Future[Profile] = Future {
      //fetch something from the database
      Thread.sleep(random.nextInt(300)) //simulate time delay
      Profile(id, names(id))
    }

    def fetchBestFriend(profile: Profile): Future[Profile] = Future {
      Thread.sleep(random.nextInt(400)) //simulate time delay
      val bestFriendId = friends(profile.id)
      Profile(bestFriendId, names(bestFriendId))
    }
  }

  // problem: sending a message to my best friend
  def sendMessageToBestFriend(accountId: String, message: String): Unit = {
    //1 - call fetchProfile
    //2 - call fetchBestFriend
    //3 - call profile.sendMessage(bestFriend)

    val profileFuture = SocialNetwork.fetchProfile(accountId)
    profileFuture.onComplete {
      case Success(profile) => //code block (not scala 3 specific)
        val friendProfileFuture = SocialNetwork.fetchBestFriend(profile)
        friendProfileFuture.onComplete{
          case Success(friendProfile) => profile.sendMessage(friendProfile, message)
          case Failure(e) => e.printStackTrace()
        }
      case Failure(ex) => ex.printStackTrace()
    }
  }

  //onComplete is a hassle
  //solution: Functional composition

  def main(args: Array[String]): Unit = {
    println(futureInstantResult)
    //will be of type Option[Try[Int]] -- it inspects the value right now.
    //may or may not get the result of computation because it might take a long time.
    Thread.sleep(2000)
    executor.shutdown()
  }
}
