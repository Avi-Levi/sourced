package sourced.client.api

import scala.util.Try

trait SourcedClientFactory {
  def streamClient(key:StreamKey) : Try[StreamClient]
}
