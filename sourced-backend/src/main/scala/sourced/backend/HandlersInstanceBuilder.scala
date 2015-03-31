package sourced.backend

trait HandlersInstanceBuilder {
  def createHandlerInstance(cls:Class[_]):AnyRef = cls.getConstructor().newInstance().asInstanceOf[AnyRef]
}
