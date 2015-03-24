package eventStore.mongodb

import eventStore.mongodb.serialization.Serializer
import eventstore.events.{EventObject, EventsRepository}
import play.api.libs.iteratee.{Enumerator, Iteratee}
import reactivemongo.api._
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.{BSONDocument, _}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, _}
import scala.util.{Failure, Success, Try}

class MongoEventsRepository(private val config: MongoConfig) extends EventsRepository{

  /*implicit val handler = Macros.handler[EventObject]*/

  val collection = connect(config)
  override def iterate(streamId: String, handleEvent: (EventObject) => Unit): Future[Long] = {
    val p = promise[Long]()

    var count = 0

    val q = BSONDocument("streamId" -> streamId)
    collection
      .find(q)
      .cursor[BSONDocument]
      .enumerate().apply(Iteratee.foreach{doc =>
        count += 1
        handleEvent(Serializer.toEventObject(doc))
      })
      .onComplete{
      case Success(x) => p.success(count)
      case Failure(t) => p.failure(t)
    }

    p.future
  }

  override def save(events: Iterable[EventObject]): Future[Try[Unit]] = {
    collection.bulkInsert(Enumerator.enumerate(events.map(Serializer.toDocument))).transform(x=>null, t=>t)
  }

  private def connect(config: MongoConfig) : BSONCollection = {
    val driver = new MongoDriver
    val connection = driver.connection(config.nodes)
    val db = connection(config.dbName)
    db.collection[BSONCollection](config.collectionName)
  }
}