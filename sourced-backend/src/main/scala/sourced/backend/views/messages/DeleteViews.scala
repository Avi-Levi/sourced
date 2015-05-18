package sourced.backend.views.messages

import java.util.UUID

case class DeleteViews(streamId:String, viewIdsToDelete:Array[String], correlationKey:UUID)