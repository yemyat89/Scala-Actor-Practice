package runner

import akka.actor.{Props, ActorSystem}
import com.typesafe.config.ConfigFactory
import common.LocalDebug
import server.ServerActor

object ServerRunnerMain4 extends App {

  val dataFile = "data.txt"

  val config = ConfigFactory.parseString(
    """
      |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
      |akka.remote.enabled-transports = ["akka.remote.netty.tcp"]
      |akka.remote.netty.tcp.hostname = "127.0.0.1"
      |akka.remote.netty.tcp.port = 5153
    """
      .stripMargin
  )

  val actorSystem = ActorSystem("ServerActorSystem4", config)
  val friend = ""
  val serverActor = actorSystem.actorOf(Props(new ServerActor(dataFile, friend)), name="ServerActor4")

  println("Server2 ready.")

  serverActor ! LocalDebug

}



