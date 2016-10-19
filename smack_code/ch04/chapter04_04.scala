import akka.actor._

case class Hire(name: String)

case class Name(name: String)

class Boss extends Actor {
  def receive = {
    case Hire(name) =>
      // here the boss hire personnel
      println(s"$name is about to be hired")
      val employee = context.actorOf(Props[Employee], name = s"$name")
      employee ! Name(name)
    case _ => println(s"The Boss can't handle this message.")
  }
}

class Employee extends Actor {
  var name = "Employee name"

  override def postStop {
    println(s"I'm ($name) and Mr. Burns fired me: ${self.path}")
  }

  def receive = {
    case Name(name) => this.name = name
    case _ => println(s"The Employee $name can't handle this message.")
  }
}

object StartingActorsDemo extends App {
  val actorSystem = ActorSystem("StartingActorsSystem")
  val mrBurns = actorSystem.actorOf(Props[Boss], name = "MrBurns")

  // here the boss hires people
  mrBurns ! Hire("HomerSimpson")
  mrBurns ! Hire("FrankGrimes")

  // we wait some office cycles
  Thread.sleep(4000)

  // we look for Frank and we fire him
  println("Firing Frank Grimes ...")
  val grimes = actorSystem.actorSelection("../user/MrBurns/FrankGrimes")

  // PoisonPill, an Akka special message
  grimes ! PoisonPill
  println("now Frank Grimes is fired")
  //actorSystem.shutdown
}


