package sourced.backend.views

import akka.actor.Actor
import sourced.backend.views.messages.{UpdateViewsCompleted, DeleteViews}
import sourced.backend.views.storage.ViewsStorage

class DeleteViewsActor(storage:ViewsStorage) extends Actor {
  import context.dispatcher

  override def receive: Receive = {
    case m:DeleteViews =>
      storage.delete(m.streamId, m.viewIdsToDelete) onComplete{
        case result => UpdateViewsCompleted(m.correlationKey,result)
      }
  }
}