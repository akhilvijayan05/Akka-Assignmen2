
import akka.actor.{ActorSystem, Props}
import akka.testkit._
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}
import com.typesafe.config.ConfigFactory


object FlipkartSpec {

  val testSystem = {
    val config = ConfigFactory.parseString(
      """
        |akka.loggers = [akka.testkit.TestEventListener]
      """.stripMargin
    )
    ActorSystem("test-system", config)
  }
}
import FlipkartSpec._

class FlipkartSpec extends TestKit(testSystem) with WordSpecLike
  with BeforeAndAfterAll with MustMatchers with ImplicitSender{

  override protected def afterAll(): Unit = {
    system.terminate()
  }

  "PurchaseRequestHandler" must {
    "Invalid request" in {
      val dispatcherId = CallingThreadDispatcher.Id
      val props = Props[PurchaseRequestHandler].withDispatcher(dispatcherId)

      val ref = system.actorOf(props)
      //val ref = TestActorRef[Validate]
      EventFilter.info(message = "Unkown Request", occurrences = 1).intercept {
        ref ! "hi"
      }
    }

    "Success Request" in {
      val dispatcherId = CallingThreadDispatcher.Id
      val props = Props[PurchaseRequestHandler].withDispatcher(dispatcherId)

      val ref = system.actorOf(props)
      //val ref = TestActorRef[Validate]
      EventFilter.info(message = "Request Initiated", occurrences = 1).intercept {
        ref ! (1,Customer("","","",""))
      }
    }

    "Respond when user is asking for more than one item" in {
      val dispatcherId = CallingThreadDispatcher.Id
      val props = Props[PurchaseRequestHandler].withDispatcher(dispatcherId)

      val ref = system.actorOf(props)
      //val ref = TestActorRef[Validate]
      EventFilter.info(message = "Sry you cannot book more than one...", occurrences = 1).intercept {
        ref ! (2,Customer("","","",""))
      }
    }
  }

  "ValidationActor" must {
//    "Respond when user is asking for more than one item" in {
//      val dispatcherId = CallingThreadDispatcher.Id
//      val props = Props[ValidationActor].withDispatcher(dispatcherId)
//
//      val ref = system.actorOf(props)
//      //val ref = TestActorRef[Validate]
//      EventFilter.info(message = "Sorry Out of stock....!!", occurrences = 1).intercept {
//        ref ! (2,Customer("", "", "", ""))
//      }
//    }

    "Invalid Details" in {
      val dispatcherId = CallingThreadDispatcher.Id
      val props = Props[ValidationActor].withDispatcher(dispatcherId)

      val ref = system.actorOf(props)
      //val ref = TestActorRef[Validate]
      EventFilter.info(message = "Invalid UserDetails", occurrences = 1).intercept {
        ref ! ""
      }
    }


  }

  "PurchaseActor" must {
    "Booking Details" in {
      val dispatcherId = CallingThreadDispatcher.Id
      val props = Props[PurchaseActor].withDispatcher(dispatcherId)

      val ref = system.actorOf(props)
      //val ref = TestActorRef[Validate]
      EventFilter.info(message = "Thanks for booking !!, your details are...", occurrences = 1).intercept {
        ref ! (Customer("", "", "", ""))
      }
    }

    "Invalid Details" in {
      val dispatcherId = CallingThreadDispatcher.Id
      val props = Props[PurchaseActor].withDispatcher(dispatcherId)

      val ref = system.actorOf(props)
      //val ref = TestActorRef[Validate]
      EventFilter.info(message = "Wrong User Details", occurrences = 1).intercept {
        ref ! ""
      }
    }


  }
}