package sourced.backend.exceptions

class StreamDefinitionMissingException(streamType:String) extends RuntimeException(s"a definition for stream type: $streamType was not found"){}
