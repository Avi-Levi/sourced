package sourced.client.api.exceptions

class MetadataNotFoundForStreamType(streamType:String) extends Throwable("metadata was not found for stream type: " + streamType)
