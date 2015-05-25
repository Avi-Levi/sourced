package sourced.embedded

import akka.actor.ActorSystem
import sourced.backend.metadata.DefaultHandlerMetadataBuilder

object Implicits{
  implicit lazy val handlerMetadataBuilder= new DefaultHandlerMetadataBuilder
  implicit lazy val actorSystem = ActorSystem("embedded-sourced")
  implicit lazy val executionContext = actorSystem.dispatcher
}
