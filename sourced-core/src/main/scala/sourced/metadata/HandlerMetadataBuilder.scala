package sourced.metadata

trait HandlerMetadataBuilder {
  def forHandlerClasses(handlersClasses:Iterable[Class[_]]) : Array[HandlerMetadata]
}
