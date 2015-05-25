package sourced.backend.views.messages

import java.util.UUID
import sourced.backend.api.EventObject
import sourced.backend.metadata.StreamMetadata
import sourced.backend.views.ViewMetadata

case class UpdateViews(streamId:String, viewIdToViewMetadataMap:Map[String, ViewMetadata], events:Array[EventObject], streamMetadata: StreamMetadata, correlationKey:UUID)