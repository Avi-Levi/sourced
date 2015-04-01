package sourced.mongodb.serialization

import reactivemongo.bson.BSONValue

case class ValueHandler(defaultValue:Any, toValue: BSONValue => Any, toBSON: Any => BSONValue) {}
