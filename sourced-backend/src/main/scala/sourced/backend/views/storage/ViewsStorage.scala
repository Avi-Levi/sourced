package sourced.backend.views.storage

import scala.concurrent.Future

trait ViewsStorage {
  def save(streamId:String, viewHandlers:Array[AnyRef]):Future[Int]
  def update(streamId:String, viewIds:Iterable[String], f:(String,AnyRef) => Unit):Future[Int]
  def delete(streamId:String, viewsIds:Iterable[String]):Future[Int]
}
