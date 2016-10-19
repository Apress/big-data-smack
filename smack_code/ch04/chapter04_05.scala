import akka.actor._

/*
class Scapegoat extends Actor {
  def receive = {
    case s:String => println("Message received: " + s)
    case _ => println("What?")
  }
}

object StopExample extends App {

  val system = ActorSystem("StopExample")
  val sg = system.actorOf(Props[Scapegoat], name = "ScapeGoat")
  sg ! "ready?"

  // stop our crash dummy
  system.stop(sg)
  system.shutdown
}


*/