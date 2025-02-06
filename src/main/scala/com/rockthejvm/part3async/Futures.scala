package com.rockthejvm.part3async

import java.util.concurrent.Executors
import scala.concurrent.{Await, ExecutionContext, Future, Promise}
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

    def createTransaction(user: User, merchantName: String, amount: Double): Future[Transaction] = Future {
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

  //PROMISES -- made to pass futures around to be completed manually elsewhere.
  //Controllable wrappers over a future, while futures are "read-only".


//  val promise = Promise[Int]()
//  val futureInside: Future[Int] = promise.future
//
//  //thread 1 - "consumer": monitor the future for completion
//  futureInside.onComplete{
//    case Success(value) => println(s"[consumer] I've just been completed with $value")
//    case Failure(ex) => ex.printStackTrace()
//  }
//
//  // thread 2 - "producer"
//  val producerThread = new Thread(() => {
//    println("[producer] crunching numbers...")
//    //fulfill the promise
//    promise.success(42)
//    println("[producer] I'm done")
//  })
//
//  producerThread.start()



  //Exercises

  //1
  private val aPromise_v2 = Promise[Int]()
  val aFuture_v2: Future[Int] = aPromise_v2.future
  aPromise_v2.success(2)

  //2
  def inSequence[A, B](first: Future[A], second: Future[B]): Future[B] = {
    first.flatMap(_ => second)
  }

  private val firstFuture = Future[Int].apply(3 + 1)
  private val secondFuture = Future[Int].apply(10)
  private val futureOutput= inSequence(firstFuture, secondFuture)

  //3
//  def first[A](f1: Future[A], f2: Future[A]): Future[A] = {
//    val aPromise = Promise[A]()
//    f1.onComplete(aPromise.tryComplete)
//    f2.onComplete(aPromise.tryComplete)
//
//    aPromise.future
//  }
//
//  val fu1: Future[Int] = Future(1 + 1)
//  val fu2: Future[Int] = Future(1 + 2)
//  val firstCall = first(fu1, fu2)
  
  
//  //4
//  def last[A](f1: Future[A], f2: Future[A]): Future[A] = {
//    val aPromise = Promise[A]()
//    f1.onComplete(_ => f2.onComplete(r2 => aPromise.complete(r2)))
//    f2.onComplete(_ => f1.onComplete(r1 => aPromise.complete(r1)))
//
//    aPromise.future
//  }

  //5
//  def retryUntil[A] (action: () => Future[(A)], predicate: A => Boolean): Future[A] = {
//    action().flatMap{result =>
//      if(predicate(result)) Future.successful(result)
//      else retryUntil(action, predicate)}
//  }
//
//  def randomInt(): Future[Int] = {
//    val random = new Random
//    Future(random.nextInt(2000))
//  }
//
//  def isEven(n:Int): Boolean = n % 2 == 0



  //solutions
  //1
  def completeImmediately[A](value:A): Future[A] = Future(value) //async completion asap
  def completeImmediately_v2[A](value:A): Future[A] = Future.successful(value) //synchronous completion

  //3
  def first[A](f1: Future[A], f2: Future[A]): Future[A] = {
    val aPromise = Promise[A]()
    f1.onComplete(r1 => aPromise.tryComplete(r1))
    f2.onComplete(r2 => aPromise.tryComplete(r2))

    aPromise.future
  }

  //4
  def last[A](f1: Future[A], f2: Future[A]): Future[A] = {
    val bothPromise = Promise[A]()
    val lastPromise = Promise[A]()

    def checkAndComplete(result: Try[A]): Unit =
      if(!bothPromise.tryComplete(result))
        lastPromise.complete(result)

    f1.onComplete(checkAndComplete)
    f2.onComplete(checkAndComplete)

    lastPromise.future
  }

  def testFirstAndLast = {
    lazy val fast = Future {
      Thread.sleep(100)
      1
    }

    lazy val slow = Future {
      Thread.sleep(200)
      2
    }

    first(fast, slow).foreach(result => println(s"FIRST: $result"))
    last(fast, slow).foreach(result => println(s"LAST: $result"))
  }

  //5

  def retryUntil[A](action: () => Future[(A)], predicate: A => Boolean): Future[A] = {
    action().filter(predicate)
      .recoverWith {
        case _ => retryUntil(action, predicate)
      }
  }

  def testRetries(): Unit = {
    val random = new Random()
    val action = () => Future {
      Thread.sleep(100)
      val nextValue = random.nextInt(100)
      println(s"Generated $nextValue")
      nextValue
    }
    val predicate = (x: Int) => x < 10

    retryUntil(action, predicate).foreach(finalResult => println(s"Setteled on $finalResult"))
  }


  def main(args: Array[String]): Unit = {
    testRetries()



    Thread.sleep(2000)
    executor.shutdown()

//    aFuture_v2.onComplete {
//      case Success(v) => println(s"Future completed with value $v")
//      case _ => println("Future failed")
//    }

//    futureOutput.onComplete {
//      case Success(value) => println(s"Future completed with value $value")
//      case Failure(exception) => println(s"Future failed with exception: $exception")
//    }

    
//    println("purchasing...")
//    println(BankingApp.purchase("daniel-234", "shoes", "merchant-987", 3.56))
//    println("purchase complete")
    

//    sendMessageToBestFriend_v3("rtjvm.id.2-jane", "Hi best friend")


//    println(futureInstantResult)
//    //will be of type Option[Try[Int]] -- it inspects the value right now.
//    //may or may not get the result of computation because it might take a long time.
//    Thread.sleep(2000)
//    executor.shutdown()
  }
}
