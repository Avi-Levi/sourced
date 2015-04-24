package sourced.backend.views

import sourced.backend.HandlersFactory
import sourced.backend.dispatchersIndex.{HandlerInfo, TopicsToStreamHandlersIndex}
import sourced.backend.events.EventObject
import sourced.backend.metadata.StreamMetadata
import sourced.backend.views.storage.ViewsStorage

import scala.concurrent.Future

/* design how the extension knows about the steam id*/
class UpdateViews(streamId:String,viewsMetadata:Iterable[ViewMetadata], streamMetadata: StreamMetadata, storage:ViewsStorage) extends HandlersFactory{
  val createTopicToViewsMetadata = viewsMetadata.map{vm => (vm.createTopic, vm)}.groupBy(_._1).map(x => (x._1, x._2.map(_._2)))
  val updateTopicToViewsMetadata = viewsMetadata.flatMap(vm=>vm.updateTopics.map((_,vm))).groupBy(_._1).map(x => (x._1, x._2.map(_._2)))
  val deleteTopicToViewsMetadata = viewsMetadata.map{vm => (vm.deleteTopic, vm)}.groupBy(_._1).map(x => (x._1, x._2.map(_._2)))
  
  def onEventsCommitted(newEvents:Iterable[EventObject]) : Future[Unit] = {
    
    (createViews(newEvents))

    Future.successful()
  }

  private def formatViewId(vm:ViewMetadata) = streamId + "-" + vm.viewHandlerClass.getSimpleName

  private def updateViews(newEvents:Iterable[EventObject]): Option[Future[Unit]] = {
    val viewsToUpdate = newEvents
      .flatMap(e => streamMetadata.getEventMetadata(e.body.getClass).topics)
      .map(updateTopicToViewsMetadata.get)
      .filter(_.isDefined)

    def handleViewToUpdate(newEvents: Iterable[EventObject], viewsToUpdate: Iterable[Option[Iterable[ViewMetadata]]]): Future[Unit] = {
      val idToViewMetadata = viewsToUpdate
        .flatMap(_.get)
        .map { vm =>
        (formatViewId(vm), vm)
      }
        .toMap

      def updateView(id: String, view: AnyRef): Unit = {
        idToViewMetadata.get(id)
          .map(vm => HandlerInfo(vm.topicToMethodsMap, () => view))
          .map { vi =>
          val handlersIndex = new TopicsToStreamHandlersIndex(Array(vi), streamMetadata.getEventMetadata)
          newEvents.foreach(handlersIndex.dispatch)
        }
      }

      storage.update(idToViewMetadata.keys, updateView)
    }

    if (viewsToUpdate.nonEmpty) Some(handleViewToUpdate(newEvents, viewsToUpdate)) else None
  }
  
  private def deleteViews(newEvents:Iterable[EventObject]) : Option[Future[Unit]] = {
    val viewsToDelete = newEvents
      .flatMap(e=>streamMetadata.getEventMetadata(e.body.getClass).topics)
      .map(createTopicToViewsMetadata.get)
      .filter(_.isDefined)

    def handleViewsToDelete(newEvents:Iterable[EventObject], viewsToDelete: Iterable[Option[Iterable[ViewMetadata]]]) : Future[Unit]={
      val viewIdsToDelete =
        viewsToDelete
          .flatMap(_.get)
          .map(formatViewId)

      storage.delete(viewIdsToDelete)
    }

    if(viewsToDelete.nonEmpty) Some(handleViewsToDelete(newEvents,viewsToDelete)) else None
  }
  
  private def createViews(newEvents:Iterable[EventObject]) : Option[Future[Unit]]={
    val viewsToCreate = newEvents
      .flatMap(e=>streamMetadata.getEventMetadata(e.body.getClass).topics)
      .map(createTopicToViewsMetadata.get)
      .filter(_.isDefined)

    def handleViewsToCreate(newEvents:Iterable[EventObject], viewsToCreate: Iterable[Option[Iterable[ViewMetadata]]]) : Future[Unit]={
      val viewsToCreateInfos = viewsToCreate
        .flatMap(_.get)
        .map(vm => HandlerInfo(vm.topicToMethodsMap, () => createHandlerInstance(vm.viewHandlerClass)))

      val handlersIndex = new TopicsToStreamHandlersIndex(viewsToCreateInfos, streamMetadata.getEventMetadata)
      newEvents.foreach(handlersIndex.dispatch)
      storage.save(streamId, handlersIndex.getUpdatedHandlers.toArray)
    }

    if(viewsToCreate.nonEmpty) Some(handleViewsToCreate(newEvents,viewsToCreate)) else None
  }
}
