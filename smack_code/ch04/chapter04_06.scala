
import akka.actor._

class ScapeGoat extends Actor {
  def receive = {
    case s:String => println("Message received: " + s)
    case _ => println("Uh?")
  }

  override def preStart {
    println("In preStart method")
  }

  override def postStop {
    println("In postStop method")
  }

  override def preRestart(reason: Throwable, message: Option[Any]) {
    println("In preRestart method")
  }

  override def postRestart(reason: Throwable) {
    println("In postRestart method")
  }
}

object Abbatoir extends App {
  val system = ActorSystem("Abbatoir")
  val sg = system.actorOf(Props[ScapeGoat], name = "ScapeGoat")
  sg ! "say goodbye"

  // finish him!
  sg ! Kill
  system.shutdown
}
