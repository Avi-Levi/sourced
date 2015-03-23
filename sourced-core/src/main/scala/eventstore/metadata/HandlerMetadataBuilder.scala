package eventstore.metadata

trait HandlerMetadataBuilder {
  def forHandlerClasses(handlersClasses:Iterable[Class[_]]) : Array[HandlerMetadata]
}
