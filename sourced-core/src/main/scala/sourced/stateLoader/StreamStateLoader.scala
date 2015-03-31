package sourced.stateLoader

import sourced.metadata.StreamMetadata

import scala.concurrent._

trait StreamStateLoader{
  def loadStreamState(streamId:String, streamMetadata: StreamMetadata) : Future[LoadStateResponse]
}