package sourced.api.exceptions

class LoadStateFailureException(inner:Throwable) extends RuntimeException("failed loading event stream's state, see inner exception for details",inner){}
