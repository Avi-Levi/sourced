package sourced.mongodb.serialization

class CreateObjectInstanceException(cls:Class[_], t:Throwable) extends RuntimeException(s"failed creating instance of class ${cls.getName}, see inner exception", t){}
