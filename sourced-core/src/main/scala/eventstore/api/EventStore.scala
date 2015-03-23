package eventstore.api

import eventstore.stream.StreamKey

import scala.util.Try

trait EventStore {
  def loadStream(key:StreamKey) : Try[EventStream]
  def registerStream(definition:StreamDefinition) : Unit
}
