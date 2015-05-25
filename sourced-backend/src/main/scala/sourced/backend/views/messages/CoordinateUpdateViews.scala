package sourced.backend.views.messages

import java.util.UUID

import sourced.backend.api.EventObject

case class CoordinateUpdateViews(correlationKey:UUID, events:Array[EventObject])