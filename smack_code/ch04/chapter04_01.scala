
import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props

class GreeterActor extends Actor {
  def receive = {
    case "en" => println("Good day")
    case "es" => println("Buen dia")
    case "fr" => println("Bonjour")
    case "de" => println("Guten Tag")
    case "pt" => println("Bom dia")
    case _ => println(":(")
  }
}

object GreeterTest extends App {

  // always build the ActorSystem
  val actorSystem = ActorSystem("MultilangSystem")

  // create the actor
  val greeter = actorSystem.actorOf(Props[GreeterActor], name = "GreeterActor")

  // send the actor some messages
  greeter ! "en"
  greeter ! "es"
  greeter ! "fr"
  greeter ! "de"
  greeter ! "pt"
  greeter ! "zh-CN"


  // shut down the actor system
  actorSystem.shutdown
}