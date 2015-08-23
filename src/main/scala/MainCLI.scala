package cli

import java.net.{UnknownHostException, InetAddress}

import akka.actor.{Props, ActorSystem}
import client.ClientActor
import com.typesafe.config.ConfigFactory
import common.DoWork
import server.ServerActor

object MainCLI {

  def main(args: Array[String]) {

    args(0) match {
      case "server" =>

        val dataFile = "data.txt"

        val port = args(1)
        val name = args(2)
        val actorSystemName = args(3)
        val friendString = args(4)

        var localAddr = "127.0.0.1"
        try {
          localAddr = InetAddress.getLocalHost().getHostAddress()
        } catch {
          case e: UnknownHostException =>
        }


        val config = ConfigFactory.parseString(
          s"""
            |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
            |akka.remote.enabled-transports = ["akka.remote.netty.tcp"]
            |akka.remote.netty.tcp.hostname = "$localAddr"
            |akka.remote.netty.tcp.port = $port
          """
            .stripMargin
        )

        val actorSystem = ActorSystem(actorSystemName, ConfigFactory.load(config))
        val friend = friendString
        val serverActor = actorSystem.actorOf(Props(new ServerActor(dataFile, friend)), name=name)

        println(name + " ready: " + serverActor.path)


      case "client" =>

        val serverPath = args(1)
        val workCount = args(2).toInt

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
        val client = actorSystem.actorOf(Props(new ClientActor(
          serverPath)), name="ClientActor")

        println("Client ready")

        client ! DoWork(workCount)

      case "debug" =>

        println(InetAddress.getLocalHost().getHostAddress())

    }

  }

}