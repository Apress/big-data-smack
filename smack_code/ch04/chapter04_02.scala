import akka.actor._

case object SendANewCat
case object LiveALife
case object BackToHeaven
case object LifeSpended {
  var remaining = 0// default Value
}

class God(indulged: ActorRef) extends Actor {

  def receive = {
    case SendANewCat =>
      println("GOD: Go!, you have seven lives")
      indulged ! LiveALife
    case LifeSpended =>
      if ( LifeSpended.remaining == 0){
        println("GOD: Time to Return!")
        indulged ! BackToHeaven
        context.stop(self)
      }
      else {
        println("GOD: one live spent, " + LifeSpended.remaining + " remaining.")
        indulged ! LiveALife
      }
    case _ => println("GOD: Sorry, I don't understand")
  }
}

class Cat extends Actor {
  var lives = 7 // All the cats created with 7 lives

  def receive = {
    case LiveALife =>
      println("CAT: Thanks God, I still have " + lives + " lives")
      lives -= 1
      LifeSpended.remaining = lives
      sender ! LifeSpended
    case BackToHeaven =>
      println("CAT: No more lives, going to Heaven")
      context.stop(self)
    case _ => println("CAT: Sorry, I don't understand")
  }
}
object CatLife extends App {
  val system = ActorSystem("CatLifeSystem")
  val silvester = system.actorOf(Props[Cat], name = "Silvester")
  val catsGod = system.actorOf(Props(new God(silvester)), name = "CatsGod")

  // God sends a Cat
  catsGod ! SendANewCat

  system.shutdown()
}
