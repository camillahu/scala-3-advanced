package com.rockthejvm.part3async

import java.util.concurrent.Executors
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Random, Success, Try}
import scala.concurrent.duration.*

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
//  aFuture.onComplete {
//    case Success(value) => println(s"I've completed with the meaning of life $value")
//    case Failure(exception) => println(s"My async computation failed $exception")
//  } //will be evaluated on some other thread-- have no control over when or which threads

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

  def sendMessageToBestFriend_v2(accountId: String, message: String): Unit = {
    val profileFuture = SocialNetwork.fetchProfile(accountId)
    profileFuture.flatMap { profile => // Future[Unit]
      SocialNetwork.fetchBestFriend(profile).map { bestFriend => // Future[Unit]
        profile.sendMessage(bestFriend, message) // unit
      }
    }
  }

  //with for comp-- best approach
  def sendMessageToBestFriend_v3(accountId: String, message: String): Unit = {
    for {
      profile <- SocialNetwork.fetchProfile(accountId)
      bestFriend <- SocialNetwork.fetchBestFriend(profile)
    } yield profile.sendMessage(bestFriend, message) // identical to v2
  }

  val janeProfileFuture = SocialNetwork.fetchProfile("rtjvm.id.2-jane")
  //map transforms value contained inside asynchronously
  val janeFuture: Future[String] = janeProfileFuture.map(profile => profile.name)

  //how to compose Futures together with flatMap without using oncomplete:
  val janesBestFriend: Future[Profile] = janeProfileFuture.flatMap(profile => SocialNetwork.fetchBestFriend(profile))

  val janesBestFriendFilter: Future[Profile] = janesBestFriend.filter(profile => profile.name.startsWith("Z"))

  //fallbacks -- handling errors with recover
  val profileNoMatterWhat = SocialNetwork.fetchProfile("unknown id").recover {
    case e: Throwable => Profile("rtjvm.id.0-dummy", "Forever alone")
  }

  //analogy-- recover is "map" and recoverWith is "flatMap"

  val aFetchedProfileNoMatterWhat: Future[Profile] = SocialNetwork.fetchProfile("unknown id").recoverWith {
    case e: Throwable => SocialNetwork.fetchProfile("rtjvm.if.0-dummy")
  } //if code before .recoverWith fails, the partial function will be run


  //with fallback to -- if both codes fail, the exception will be returned from the code before .fallBackTo
  val fallbackProfile: Future[Profile] = SocialNetwork.fetchProfile("unknown id")
    .fallbackTo(SocialNetwork.fetchProfile("rtjvm.if.0-dummy"))

  /*
    Block for a future -- should only be used in extreme scenarios:
    forces future to block before it is computed.
  * */

  case class User(name: String)
  case class Transaction(sender: String, receiver: String, amount: Double, status: String)

  object BankingApp {
    // "APIs"
    def fetchUser(name: String): Future[User] = Future {
      // simulate some DB fetching
      Thread.sleep(500)
      User(name)
    }

    def createTransaction(user: User, merchantName: String, amount: Double): Future[Transaction] = {
      // simulate payment
      Thread.sleep(1000)
      Transaction(user.name, merchantName, amount, "SUCCESS")
    }

    //"external API"

    def purchase(username: String, item: String, merchantName: String, price: Double):String = {
      // 1- fetch user
      // 2- create transaction
      // 3- Wait for transaction to finish

      val transactionStatusFuture: Future[String] = for {
        user <- fetchUser(username)
        transaction <-createTransaction(user, merchantName, price)
      } yield transaction.status
      
      //blocking call -- blocking thread until Future call has been completed.
      //seconds is an extension method in int which comes with the import scala.concurrent.duration.*
      Await.result (transactionStatusFuture, 2.seconds) // throws TimeoutException if the future doesn't finish within 2s
    }
  }


  def main(args: Array[String]): Unit = {
    
    println("purchasing...")
    println(BankingApp.purchase("daniel-234", "shoes", "merchant-987", 3.56))
    println("purchase complete")
    

//    sendMessageToBestFriend_v3("rtjvm.id.2-jane", "Hi best friend")


//    println(futureInstantResult)
//    //will be of type Option[Try[Int]] -- it inspects the value right now.
//    //may or may not get the result of computation because it might take a long time.
//    Thread.sleep(2000)
//    executor.shutdown()
  }
}
