package sourced.backend.views.storage

import scala.concurrent.Future

trait ViewsStorage {
  def save(streamId:String, viewHandlers:Array[AnyRef]):Future[Unit]
  def update(viewIds:Iterable[String], f:(String,AnyRef) => Unit):Future[Unit]
  def delete(ids:Iterable[String]):Future[Unit]
}
