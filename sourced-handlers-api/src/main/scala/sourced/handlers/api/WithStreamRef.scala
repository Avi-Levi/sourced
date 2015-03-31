package sourced.handlers.api

trait WithStreamRef {
  private [sourced] def setStreamRef(stream:StreamRef) = this._stream = stream
  private var _stream:StreamRef=null
  protected def stream = _stream
}
