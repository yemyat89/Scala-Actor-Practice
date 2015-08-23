package common

import akka.actor.ActorRef

case class Request(idString: String, originalClient: Option[ActorRef])
case class DoWork(count: Int)
case class WorkResult(countId: Int, newValue: Int)
case class Row(index: String, sleep: Int, value: Int)

case object LocalDebug
case object ServerFutureDone