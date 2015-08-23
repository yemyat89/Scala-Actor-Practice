package client

import common._
import java.util.concurrent.Executors
import akka.actor._
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import scala.concurrent.ExecutionContext
import akka.pattern.ask
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


class ClientActor() extends Actor {

  implicit val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(2000))
  implicit val timeout = Timeout(60 seconds)

  val server = context.actorSelection("akka.tcp://ServerActorSystem1@127.0.0.1:5150/user/ServerActor1")

  var twice = scala.collection.mutable.Map[Int, Int]()
  val parentActor = self

  def receive = {
    case LocalDebug =>
      //println("Client: All is ok.")
    case DoWork(count) =>

      for (i <- 1 to count) {
        println("Doing work for " + i)
        val x = ask(server, Request(i toString, None)).mapTo[Int]
        x.onComplete({
          case Success(r) =>
            //println("Server returns success " + r)
            parentActor ! WorkResult(i, r)
          case Failure(error) =>
            //println("Server returns error " + error)
        })
        val y = ask(server, Request(i toString, None)).mapTo[Int]
        y.onComplete({
          case Success(r) =>
            parentActor ! WorkResult(i, r)
          case Failure(error) =>
        })
      }
    case WorkResult(countId, newValue) =>
      twice.get(countId) match {
        case Some(value) =>
          twice.put(countId, value + newValue)

          twice.get(countId) match {
            case Some(finalValue) =>
              println("Final result  for client (" + countId
                + ") is " + finalValue)
            case None =>
          }
        case None =>
          twice.put(countId, newValue)
      }
  }
}

object ClientMain extends App {

  val config = ConfigFactory.parseString(
    """
      |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
      |akka.remote.enabled-transports = ["akka.remote.netty.tcp"]
      |akka.remote.netty.tcp.hostname = "127.0.0.1"
      |akka.remote.netty.tcp.port = 0
    """
      .stripMargin
  )

  val actorSystem = ActorSystem("ClientActorSystem", config)
  val client = actorSystem.actorOf(Props(new ClientActor()), name="ClientActor")

  println("Client ready")

  client ! LocalDebug

  client ! DoWork(4000)

}