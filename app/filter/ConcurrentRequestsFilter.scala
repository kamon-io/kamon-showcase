package filter

import javax.inject.Inject

import akka.stream.Materializer
import kamon.Kamon
import kamon.util.SameThreadExecutionContext
import play.api.mvc.{Filter, RequestHeader, Result}

import scala.concurrent.Future


class ConcurrentRequestsFilter @Inject()(override implicit val mat: Materializer) extends Filter {
  val concurrentRequestsMMCounter = Kamon.metrics.minMaxCounter("concurrent-requests")

  override def apply(next: (RequestHeader) => Future[Result])(request: RequestHeader): Future[Result] = {
    concurrentRequestsMMCounter.increment()
    next(request).map { result =>
      concurrentRequestsMMCounter.decrement()
      result
    }(SameThreadExecutionContext)
  }

}
