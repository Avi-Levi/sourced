package sourced.backend.exceptions

class StreamDefinitionExistsException(streamType:String) extends RuntimeException(s"a definition already exists for stream type: $streamType"){}
