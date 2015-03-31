package sourced.embedded

import sourced.backend.metadata.DefaultHandlerMetadataBuilder

object Implicits{
  implicit lazy val handlerMetadataBuilder= new DefaultHandlerMetadataBuilder
}
