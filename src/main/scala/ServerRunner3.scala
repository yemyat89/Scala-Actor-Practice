package runner

import akka.actor.{Props, ActorSystem}
import com.typesafe.config.ConfigFactory
import common.LocalDebug
import server.ServerActor

object ServerRunnerMain3 extends App {

  val dataFile = "data.txt"

  val config = ConfigFactory.parseString(
    """
      |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
      |akka.remote.enabled-transports = ["akka.remote.netty.tcp"]
      |akka.remote.netty.tcp.hostname = "127.0.0.1"
      |akka.remote.netty.tcp.port = 5152
    """
      .stripMargin
  )

  val actorSystem = ActorSystem("ServerActorSystem3", config)
  val friend = "akka.tcp://ServerActorSystem4@127.0.0.1:5153/user/ServerActor4"
  val serverActor = actorSystem.actorOf(Props(new ServerActor(dataFile, friend)), name="ServerActor3")

  println("Server3 ready.")

  serverActor ! LocalDebug

}



