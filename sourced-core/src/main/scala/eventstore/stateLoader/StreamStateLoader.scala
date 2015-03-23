package eventstore.stateLoader

import eventstore.metadata.StreamMetadata

import scala.concurrent._

trait StreamStateLoader{
  def loadStreamState(streamId:String, streamMetadata: StreamMetadata) : Future[LoadStateResponse]
}