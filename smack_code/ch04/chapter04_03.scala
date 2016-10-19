
import akka.actor._

case object GetAngry

class Hulk extends Actor {
  println("in the Hulk constructor")

  override def preStart {
    println("in the Hulk preStart")
  }

  override def postStop {
    println("in the Hulk postStop")
  }

  override def preRestart(reason: Throwable, message: Option[Any]) {
    println("in the Hulk preRestart")
    println(s" preRestart message: ${message.getOrElse("")}")
    println(s" preRestart reason: ${reason.getMessage}")
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable) {
    println("in the Hulk postRestart")
    println(s" postRestart reason: ${reason.getMessage}")
    super.postRestart(reason)
  }

  def receive = {
    case GetAngry => throw new Exception("ROAR!")
    case _ => println("Hulk received a message...")
  }
}

object LifecycleTest extends App {
  val system = ActorSystem("LifeCycleSystem")
  val hulk = system.actorOf(Props[Hulk], name = "TheHulk")
  println("sending Hulk a message")
  hulk ! "Hulk, What is your secret?"
  Thread.sleep(5000)
  println("making Hulk get angry")
  hulk ! GetAngry
  Thread.sleep(5000)
  println("stopping Hulk")
  system.stop(hulk)
  println("shutting down Life Cycle system")
  system.shutdown
}