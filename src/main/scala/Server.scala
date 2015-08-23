package server

import common._
import java.util.concurrent.Executors
import akka.actor.Actor
import scala.concurrent.{Future, ExecutionContext}
import scala.io.Source
import akka.pattern.pipe
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global


class ServerActor(dataFilePath: String, friendActorPath: String) extends Actor {

  var counter = 2000
  implicit val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(2000))

  var dataMap = scala.collection.mutable.Map[String, Row]()
  for (line <- Source.fromFile(dataFilePath).getLines()) {
    val row = line .split(",")
    val x = Row(row(0), row(1).trim.toInt, row(2).trim.toInt)
    dataMap += (row(0) -> x)
  }

  def receive = {
    case Request(idString, origin) =>

      if (counter > 0) {
        counter -= 1

        //println("Started: Server's counter value = " + counter)

        val parentActor = self
        val f: Future[Int] = Future {

          println("Request recieved: " + idString)
          Thread.sleep(dataMap(idString).sleep * 1000)
          println("Response for: " + idString + ", and the Row instance is "
            + dataMap(idString))
          dataMap(idString).value
        }
        f.onComplete {
          case Success(result) =>
            parentActor ! ServerFutureDone
            //println("Future result is " + result)
            result
          case Failure(error) =>
          //println("Server failed to respond: " + idString)
        }
        origin match {
          case Some(originalClient) =>
            f pipeTo originalClient
          case None =>
            f pipeTo sender
        }

      } else {
        if (!friendActorPath.isEmpty){
          val friendServer = context.actorSelection(friendActorPath)
          println("Busy but can get help from " + friendActorPath)

          origin match {
            case Some(originalClient) =>
              friendServer ! Request(idString, Some(originalClient))
            case None =>
              friendServer ! Request(idString, Some(sender))
          }

        } else {
          println("Very busy and there will be some waits and delays.")
        }
      }

    case ServerFutureDone =>
      counter += 1
    //println("End: Server's counter value = " + counter)

    case LocalDebug =>
    //println("Server: All is ok")
  }

}
