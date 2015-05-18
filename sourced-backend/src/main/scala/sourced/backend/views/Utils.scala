package sourced.backend.views

object Utils {
  def formatViewId(streamId:String, vm:ViewMetadata) = streamId + "-" + vm.viewHandlerClass.getSimpleName
}
