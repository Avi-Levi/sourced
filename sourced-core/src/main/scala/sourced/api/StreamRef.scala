package sourced.api

trait StreamRef {
  def push(msg:AnyRef):Unit
}
