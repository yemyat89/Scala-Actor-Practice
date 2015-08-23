package runner

import akka.actor.{Props, ActorSystem}
import com.typesafe.config.ConfigFactory
import common.LocalDebug
import server.ServerActor

object ServerRunnerMain2 extends App {

  val dataFile = "data.txt"

  val config = ConfigFactory.parseString(
    """
      |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
      |akka.remote.enabled-transports = ["akka.remote.netty.tcp"]
      |akka.remote.netty.tcp.hostname = "127.0.0.1"
      |akka.remote.netty.tcp.port = 5151
    """
      .stripMargin
  )

  val actorSystem = ActorSystem("ServerActorSystem2", config)
  val friend = "akka.tcp://ServerActorSystem3@127.0.0.1:5152/user/ServerActor3"
  val serverActor = actorSystem.actorOf(Props(new ServerActor(dataFile, friend)), name="ServerActor2")

  println("Server2 ready.")

  serverActor ! LocalDebug

}



