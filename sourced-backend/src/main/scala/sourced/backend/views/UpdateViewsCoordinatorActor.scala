package sourced.backend.views

import java.util.UUID

import akka.actor.{Actor, ActorRef}
import sourced.backend.api.EventObject
import sourced.backend.metadata.StreamMetadata
import sourced.backend.views.messages._
import sourced.backend.views.storage.ViewsStorage

class UpdateViewsCoordinatorActor(streamId:String,viewsMetadata:Iterable[ViewMetadata], streamMetadata: StreamMetadata, storage:ViewsStorage,
                        deleteViewsActor:ActorRef, updateViewsActor:ActorRef, createViewsActor:ActorRef) extends Actor
  {

  val createTopicToViewsMetadata = viewsMetadata.map{vm => (vm.createTopic, vm)}.groupBy(_._1).map(x => (x._1, x._2.map(_._2)))
  val updateTopicToViewsMetadata = viewsMetadata.flatMap(vm=>vm.updateTopics.map((_,vm))).groupBy(_._1).map(x => (x._1, x._2.map(_._2)))
  val deleteTopicToViewsMetadata = viewsMetadata.map{vm => (vm.deleteTopic, vm)}.groupBy(_._1).map(x => (x._1, x._2.map(_._2)))
  val idToViewMetadata: Map[String, ViewMetadata] = viewsMetadata.map(vm=>(Utils.formatViewId(streamId, vm),vm)).toMap

  val requestsCorrelationRegistry = scala.collection.mutable.Map[UUID, (ActorRef, Int)]()

  override def receive: Receive = {
    case m : CoordinateUpdateViews => submitUpdateViews(m)
    case m : OperationCompleted => this.handleOperationCompleted(m)
  }

  private def handleOperationCompleted(m : OperationCompleted) = {
    var (originalSender,counter) = this.requestsCorrelationRegistry.get(m.correlationKey).get
    counter += 1
    if(counter == 3){
      this.requestsCorrelationRegistry.remove(m.correlationKey)
      originalSender ! CoordinateUpdateViewsCompleted(m.correlationKey)
    }else{
      this.requestsCorrelationRegistry.put(m.correlationKey,(originalSender,counter))
    }
  }
  private def submitUpdateViews(m: CoordinateUpdateViews): Unit = {
    var counter = 0

    if (this.updateViews(m.correlationKey, m.events)) counter += 1
    if (this.deleteViews(m.correlationKey, m.events)) counter += 1
    if (this.createViews(m.correlationKey, m.events)) counter += 1

    if (counter > 0) requestsCorrelationRegistry.put(m.correlationKey, (sender(), counter))
  }

  private def updateViews(correlationKey:UUID,newEvents:Array[EventObject]): Boolean = {
    val viewsToUpdate: Map[String, ViewMetadata] = newEvents
      .flatMap(e => streamMetadata.getEventMetadata(e.body.getClass).topics)
      .map(updateTopicToViewsMetadata.get)
      .filter(_.isDefined)
      .flatMap(_.get)
      .map {
        md =>
          val viewId = Utils.formatViewId(streamId, md)
          (viewId, md)
      }
      .toMap

    if (viewsToUpdate.nonEmpty) {
      updateViewsActor ! UpdateViews(streamId, viewsToUpdate,newEvents,streamMetadata, correlationKey)
      true
    }else{
      false
    }
  }
  
  private def deleteViews(correlationKey:UUID,newEvents:Array[EventObject]) : Boolean = {
    val viewIdsToDelete = newEvents
      .flatMap(e=>streamMetadata.getEventMetadata(e.body.getClass).topics)
      .map(createTopicToViewsMetadata.get)
      .filter(_.isDefined)
      .flatMap(_.get)
      .map(Utils.formatViewId(streamId,_))
    
    if(viewIdsToDelete.nonEmpty){
      deleteViewsActor ! DeleteViews(streamId,viewIdsToDelete, correlationKey)
      true
    }else{
      false
    }
  }
  
  private def createViews(correlationKey:UUID,newEvents:Array[EventObject]) : Boolean = {
    val viewsToCreate = newEvents
      .flatMap(e=>streamMetadata.getEventMetadata(e.body.getClass).topics)
      .map(createTopicToViewsMetadata.get)
      .filter(_.isDefined)
      .flatMap(_.get)

    if(viewsToCreate.nonEmpty){
      createViewsActor ! CreateViews(streamId, viewsToCreate.toArray,streamMetadata,newEvents, correlationKey)
      true
    } else {
      false
    }
  }
}