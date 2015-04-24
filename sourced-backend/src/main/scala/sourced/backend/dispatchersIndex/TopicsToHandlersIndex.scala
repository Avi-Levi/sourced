package sourced.backend.dispatchersIndex

trait TopicsToHandlersIndex {
  def dispatch(event:AnyRef):Unit
  def forEachInstance(f: AnyRef => Unit):Unit
  def getUpdatedHandlers : AnyRef
}
