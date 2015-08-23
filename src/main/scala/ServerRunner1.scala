package runner

import akka.actor.{Props, ActorSystem}
import com.typesafe.config.ConfigFactory
import common.LocalDebug
import server.ServerActor


object ServerRunnerMain1 extends App {

  val dataFile = "data.txt"

  val config = ConfigFactory.parseString(
    """
      |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
      |akka.remote.enabled-transports = ["akka.remote.netty.tcp"]
      |akka.remote.netty.tcp.hostname = "127.0.0.1"
      |akka.remote.netty.tcp.port = 5150
    """
      .stripMargin
  )

  val actorSystem = ActorSystem("ServerActorSystem1", ConfigFactory.load(config))
  val friend = "akka.tcp://ServerActorSystem2@127.0.0.1:5151/user/ServerActor2"
  val serverActor = actorSystem.actorOf(Props(new ServerActor(dataFile, friend)), name="ServerActor1")

  println("Server1 ready: " + serverActor.path)

  serverActor ! LocalDebug

}