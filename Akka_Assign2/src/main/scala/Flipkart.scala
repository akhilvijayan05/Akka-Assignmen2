import akka.actor.{Actor, ActorSystem, Props}
import akka.routing.FromConfig
import com.typesafe.config.ConfigFactory
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
  * Created by knoldus on 21/3/17.
  */
object Flipkart extends App{

//  val config = ConfigFactory.parseString(
//    """
//      |akka.actor.deployment {
//      | /poolRouter {
//      |   router = balancing-pool
//      |   nr-of-instances = 1
//      | }
//      |}
//    """.stripMargin
//  )
//
//  val system = ActorSystem("RouterSystem", config)
//  val router = system.actorOf(FromConfig.props(Props[PurchaseRequestHandler]), "poolRouter")
val system = ActorSystem("Book")
  val props = Props[PurchaseRequestHandler]
  val router = system.actorOf(props)
  val no_of_request=1
  for(i <- 1 to 10)
  router ! (no_of_request,Customer("Akhil","Delhi","1900234576876594","8877033455"))
//  router ! (no_of_request,Customer("Mahesh","Delhi","1800237976832547","8457033478"))
//  router ! (no_of_request,Customer("Prashant","Delhi","1800237976832547","8457033478"))
//  router ! (no_of_request,Customer("Kunal","Delhi","1800237976832547","8457033478"))
//  router ! (no_of_request,Customer("abc","Delhi","1800237976832547","8457033478"))



}

case class Customer(cus_name:String,address:String,credit_card_no:String,mobile_no:String)

class PurchaseRequestHandler extends Actor{

//  val config = ConfigFactory.parseString(
//    """
//      |akka.actor.deployment {
//      | /poolRouter {
//      |   router = round-robin-pool
//      |   nr-of-instances = 1
//      | }
//      |}
//    """.stripMargin
//  )
//
//  val system = ActorSystem("RouterSystem", config)
//  val validate = system.actorOf(FromConfig.props(Props[ValidationActor]), "poolRouter")
  val validate = context.actorOf(Props[ValidationActor])
  override def receive = {

    case (no_of_request,user:Customer)=>{

      println("Request Initialed")
      if(no_of_request==1)
        {

          validate ! user
        }
      else{
        println("Sry you cannot book more than one...")
      }
    }
  }
}

class ValidationActor extends Actor{

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

  val system = ActorSystem("RouterSystem", config)
  val purchase = system.actorOf(FromConfig.props(Props[PurchaseActor]), "poolRouter")
//  val purchase = context.actorOf(Props[PurchaseActor])
  override def receive={

    case user:Customer=>{

      if(count>0){
        println("Validate")
        count-=1
        purchase ! user
      }
      else{

        println(s"Sorry Out of stock....!!")
      }
    }
  }
}
class PurchaseActor extends Actor{

  //val purchase=new Purchase
  val purchase = context.actorOf(Props[Purchase])
  override def receive={

    case user:Customer=> {

      implicit val timeout = Timeout(5000 seconds)
      val f=purchase ask user
      Await.result(f,timeout.duration)
      //purchase ask user
 //     purchase.print(user)
    }
  }
}

class Purchase extends Actor{

  override def receive = {
    case user: Customer => {
      //    this.synchronized {
      //  case user:Customer=> {
      println("Thanks for booking !!, your details are...")
      println("Name=", user.cus_name)
      println("Address=", user.address)
      println("Mobile=", user.mobile_no)
    //       }
    //  }
  }
  }
}
