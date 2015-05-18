package sourced.backend.views.messages

import java.util.UUID

import sourced.backend.events.EventObject
import sourced.backend.metadata.StreamMetadata
import sourced.backend.views.ViewMetadata

case class CreateViews(streamId:String, viewsToCreate:Array[ViewMetadata], streamMetadata:StreamMetadata, events:Array[EventObject], correlationKey:UUID)