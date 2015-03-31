package sourced.client.api

import scala.concurrent.Future
import scala.util.Try

trait StreamOperations {
  def push(msg: AnyRef*) : Future[Try[Unit]]
}
