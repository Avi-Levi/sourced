package eventstore.api

import scala.concurrent.Future
import scala.util.Try

trait EventStream {
  def push(msg: AnyRef*) : Future[Try[Unit]]
}
