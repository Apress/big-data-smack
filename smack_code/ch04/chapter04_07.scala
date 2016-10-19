import akka.actor._

class Child extends Actor {
  def receive = {
    case _ => println("Child received a message")
  }
}

class Dad extends Actor {
  // Dad actor create a child actor
  val child = context.actorOf(Props[Child], name = "Son")
  context.watch(child)

  def receive = {
    case Terminated(child) => println("This will not end here -_-")
    case _ => println("Dad received a message")
  }
}

object ChildMonitoring extends App {

  val system = ActorSystem("ChildMonitoring")

  // we create a Dad (and it will create the Child)
  val dad = system.actorOf(Props[Dad], name = "Dad")

  // look for child, then we kill it
  val child = system.actorSelection("/user/Dad/Son")

  child ! PoisonPill
  Thread.sleep(3000)

  println("Revenge!")
  system.shutdown
}

