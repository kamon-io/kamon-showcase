package controllers

import javax.inject._

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import kamon.Kamon
import kamon.metric.instrument.Histogram
import kamon.util.{NanoInterval, RelativeNanoTimestamp}
import play.api._
import play.api.mvc._
import akka.pattern.ask
import akka.routing.{RoundRobinGroup, RoundRobinPool}
import akka.util.Timeout

import scala.concurrent.{ExecutionContext, Future, blocking, duration}
import duration._
import scala.util.Random
/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(ec: ExecutionContext, as: ActorSystem) extends Controller {
  implicit val timeout = Timeout(30 seconds)
  val blockingCodeSleep = Kamon.metrics.histogram("blocking-code-sleep")

  val nonBlockingActors = for(i <- 1 to 8) yield {
    as.actorOf(Props[NonBlockingActor], s"fast-actor-$i")
  }

  val blockingActors = for(i <- 1 to 8) yield {
    as.actorOf(Props(new BlockingActor(300)).withDispatcher("custom-dispatcher"), s"slow-actor-$i")
  }

  val slowerBlockingActor = as.actorOf(Props(new BlockingActor(1500)), s"lagging-actor")



  def index = Action { implicit request =>
    Ok(views.html.index())
  }

  def blockingCode = Action { implicit request =>
    measure {
      Thread.sleep(50)
    }(blockingCodeSleep)

    Ok("")
  }

  def blockingCodeAsync = Action.async { implicit request =>
    Future {
      measure {
        Thread.sleep(50)
      }(blockingCodeSleep)

      Ok("")
    }(ec)
  }

  def managedBlocking = Action.async { implicit request =>
    Future {
      blocking {
        measure {
          Thread.sleep(50)
        }(blockingCodeSleep)

        Ok("")
      }
    }(ec)
  }

  def fastActor = Action.async { implicit request =>
    (Random.shuffle(nonBlockingActors).head ? "hey")
      .mapTo[String]
      .map(s => Ok(s))(ec)
  }

  def slowActor = Action.async { implicit request =>
    (Random.shuffle(blockingActors).head ? "hey")
      .mapTo[String]
      .map(s => Ok(s))(ec)
  }


  def laggingActor = Action { implicit request =>
    slowerBlockingActor ! "lad"
    Ok("")
  }

  def waster = Action { implicit request =>
    measure {
      val x = for(_ <- 1 to 100) yield {
        Array.ofDim[Byte](20480)
      }

      x.forall(_ != 0)
    }(blockingCodeSleep)

    Ok("")

  }


  private def measure[T](c: => T)(histogram: Histogram): T = {
    val before = RelativeNanoTimestamp.now
    val result = c
    histogram.record(NanoInterval.since(before).nanos)

    result
  }
}


class BlockingActor(blockTime: Long) extends Actor {
  override def receive: Receive = {
    case anything => {
      Thread.sleep(blockTime)
      sender ! "done"
    }
  }
}

class NonBlockingActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case anything =>
      sender ! "done"
      log.info("done at the non-blocking actor.")
  }
}

class SchedulerActor extends Actor {
  override def receive: Receive = {
    ???
  }
}