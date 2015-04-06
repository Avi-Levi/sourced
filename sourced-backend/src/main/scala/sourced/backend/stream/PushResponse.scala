package sourced.backend.stream

import scala.concurrent.Future

case class PushResponse(whenCommitted:Future[Unit], whenViewsUpdated:Future[Unit]) {}
