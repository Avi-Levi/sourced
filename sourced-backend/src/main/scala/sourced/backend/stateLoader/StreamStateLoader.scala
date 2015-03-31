package sourced.backend.stateLoader


import sourced.backend.metadata.StreamMetadata

import scala.concurrent._

trait StreamStateLoader{
  def loadStreamState(streamId:String, streamMetadata: StreamMetadata) : Future[LoadStateResponse]
}