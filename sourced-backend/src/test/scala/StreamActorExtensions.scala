import akka.testkit.TestActorRef
import sourced.backend.dispatchersIndex.TopicsToStreamHandlersIndex
import sourced.backend.stream.EventStreamActor

object StreamActorExtensions {
  implicit class ExtendedStreamActor(actor:TestActorRef[EventStreamActor]){
    val instance = actor.underlyingActor

    def index = instance.handlersIndex.asInstanceOf[TopicsToStreamHandlersIndex]
    def headTestHandler = index.handlersInstances.head.instance .asInstanceOf[TestHandler]
  }
}
