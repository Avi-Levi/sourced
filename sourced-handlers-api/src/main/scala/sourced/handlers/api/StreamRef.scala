package sourced.handlers.api

trait StreamRef {
  def push(msg:AnyRef):Unit
}
