package sourced.backend.views.messages

import java.util.UUID

import scala.util.Try

trait OperationCompleted{val correlationKey:UUID}

case class UpdateViewsCompleted(correlationKey:UUID, result:Try[Int]) extends OperationCompleted
case class CreateViewsCompleted(correlationKey:UUID, result:Try[Int]) extends OperationCompleted
case class DeleteViewsCompleted(correlationKey:UUID, result:Try[Int]) extends OperationCompleted