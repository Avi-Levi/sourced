package sourced.backend.views

import akka.actor.Actor
import sourced.backend.dispatchersIndex.{HandlerInfo, TopicsToStreamHandlersIndex}
import sourced.backend.views.messages.{UpdateViewsCompleted, UpdateViews}
import sourced.backend.views.storage.ViewsStorage

class UpdateViewsActor(storage:ViewsStorage) extends Actor{
  import context.dispatcher

  override def receive: Receive = {
    case m : UpdateViews => handleUpdateViews(m)
  }
  private def handleUpdateViews(m : UpdateViews) = {
    storage.update(m.streamId, m.viewIdToViewMetadataMap.keys, updateView) onComplete{
      case result => UpdateViewsCompleted(m.correlationKey,result)
    }

    def updateView(id: String, view: AnyRef): Unit = {
      val vi = m.viewIdToViewMetadataMap.get(id)
        .map(vm=>HandlerInfo(vm.topicToMethodsMap, () => view))
        .get
      
      val handlersIndex = new TopicsToStreamHandlersIndex(Array(vi), m.streamMetadata.getEventMetadata)

      m.events.foreach(handlersIndex.dispatch)
    }
  }
}
