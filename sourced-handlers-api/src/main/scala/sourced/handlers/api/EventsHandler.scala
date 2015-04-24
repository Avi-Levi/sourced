package sourced.handlers.api

trait EventsHandler {
  private [sourced] def setStreamRef(stream:StreamRef) : Unit = {
    this._stream = stream
  }
  private var _stream:StreamRef=_
  protected def stream = _stream
}
