package eventstore.api

trait StreamRef {
  def push(msg:AnyRef):Unit
}
