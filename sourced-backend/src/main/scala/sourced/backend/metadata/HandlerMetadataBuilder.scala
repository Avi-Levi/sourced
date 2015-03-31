package sourced.backend.metadata

trait HandlerMetadataBuilder {
  def forHandlerClasses(handlersClasses:Iterable[Class[_]]) : Array[HandlerMetadata]
}
