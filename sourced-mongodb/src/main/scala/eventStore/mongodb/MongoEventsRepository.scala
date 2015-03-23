package eventStore.mongodb

import eventstore.events.{EventObject, EventsRepository}
import play.api.libs.iteratee.Iteratee
import reactivemongo.api._
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.{BSONDocument, _}
import play.api.libs.iteratee.Enumerator

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, _}
import scala.util.{Failure, Success, Try}
class MongoEventsRepository(private val config: MongoConfig) extends EventsRepository{

  implicit val handler = Macros.handler[EventObject]

  val collection = connect(config)
  override def iterate(streamId: String, handleEvent: (EventObject) => Unit): Future[Long] = {
    val p = promise[Long]()

    val q = BSONDocument("streamId" -> streamId)
    collection
      .find(q)
      .cursor[EventObject]
      .enumerate().apply(Iteratee.foreach(handleEvent)).onComplete{
      case Success(x) =>
        var count = 0
        Iteratee.foreach{
          count += 1
          handleEvent(_)
        }
        p.success(count)
      case Failure(t) => p.failure(t)
    }

    p.future
  }

  override def save(events: Iterable[EventObject]): Future[Try[Unit]] = {
    collection.bulkInsert(Enumerator.enumerate(events)).transform(x=>null, t=>t)
  }

  private def connect(config: MongoConfig) : BSONCollection = {

    val driver = new MongoDriver
    val connection = driver.connection(config.nodes)
    val db = connection(config.dbName)
    db.collection[BSONCollection](config.collectionName)
  }
}