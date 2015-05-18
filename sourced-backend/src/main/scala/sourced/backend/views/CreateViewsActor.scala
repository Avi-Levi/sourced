package sourced.backend.views

import akka.actor.Actor
import sourced.backend.HandlersFactory
import sourced.backend.dispatchersIndex.{HandlerInfo, TopicsToStreamHandlersIndex}
import sourced.backend.views.messages.{CreateViews, CreateViewsCompleted}
import sourced.backend.views.storage.ViewsStorage

class CreateViewsActor(storage:ViewsStorage) extends Actor with HandlersFactory{
  import context.dispatcher

  override def receive: Receive = {
    case m:CreateViews =>
      val viewsToCreateInfos = m.viewsToCreate
        .map(vm => HandlerInfo(vm.topicToMethodsMap, () => createHandlerInstance(vm.viewHandlerClass)))

      val handlersIndex = new TopicsToStreamHandlersIndex(viewsToCreateInfos, m.streamMetadata.getEventMetadata)
      m.events.foreach(handlersIndex.dispatch)
      storage.save(m.streamId, handlersIndex.getUpdatedHandlers.toArray) onComplete{
        case result => CreateViewsCompleted(m.correlationKey,result)
      }
  }
}
