package sourced.client.api

import scala.concurrent.Future

trait StreamClient {
  def push(msg: AnyRef*) : Future[Unit]
}
