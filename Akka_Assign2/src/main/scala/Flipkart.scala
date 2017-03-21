import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.routing.FromConfig
import com.typesafe.config.ConfigFactory
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
  * Created by knoldus on 21/3/17.
  */
object Flipkart extends App {

val system = ActorSystem("Book")
  val props = Props[PurchaseRequestHandler]
  val router = system.actorOf(props)
  for(i <- 1 to 10)
  router ! (1,Customer("Akhil","Delhi","1900234576876594","8877033455"))
//  router ! (no_of_request,Customer("Mahesh","Delhi","1800237976832547","8457033478"))
//  router ! (no_of_request,Customer("Prashant","Delhi","1800237976832547","8457033478"))
//  router ! (no_of_request,Customer("Kunal","Delhi","1800237976832547","8457033478"))
//  router ! (no_of_request,Customer("abc","Delhi","1800237976832547","8457033478"))



}

case class Customer(cus_name:String,address:String,credit_card_no:String,mobile_no:String)

class PurchaseRequestHandler extends Actor with ActorLogging{

  val validate = context.actorOf(Props[ValidationActor])
  override def receive = {

    case (no_of_request,user:Customer)=>{

      if(no_of_request==1)
        {

          validate ! user
        }
      else{
        log.error("Sry you cannot book more than one...")
      }
    }
  }
}

class ValidationActor extends Actor with ActorLogging{

  var count=8

  val config = ConfigFactory.parseString(
    """
      |akka.actor.deployment {
      | /poolRouter {
      |   router = round-robin-pool
      |   nr-of-instances = 500
      | }
      |}
    """.stripMargin
  )

 val purchase = context.actorOf(Props[PurchaseActor],"poolRouter")
  override def receive={

    case user:Customer=>{

      if(count>0){
        count-=1
        implicit val timeout = Timeout(1000 seconds)
        val f=purchase ? user
        Await.result(f,timeout.duration)
      }
      else{

        log.error(s"Sorry Out of stock....!!")
      }
    }
  }
}
class PurchaseActor extends Actor with ActorLogging{

  //val purchase=new Purchase
  //val purchase = context.actorOf(Props[Purchase])
  override def receive={

    case user:Customer=> {
      log.info("Thanks for booking !!, your details are...")
      log.info(s"Name= ${user.cus_name}")
      log.info(s"Address=${user.address}")
      log.info(s"Mobile= ${user.mobile_no}")
      sender() ! "Ok"
    }
  }
}


