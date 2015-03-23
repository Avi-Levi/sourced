package eventstore

import eventstore.api.StreamRef

trait WithStreamRef {
  private [eventstore] def setStreamRef(stream:StreamRef) = this._stream = stream
  private var _stream:StreamRef=null
  protected def stream = _stream
}
