package fp.workshop.part2

import cats.effect.IO.timer
import cats.effect.{Effect, IO, _}
import fs2.StreamApp
import org.http4s.HttpRoutes
import org.http4s.server.blaze.BlazeBuilder

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

object ServerMain extends StreamApp[IO] {
  override def stream(args: List[String], requestShutdown: IO[Unit]): fs2.Stream[IO, StreamApp.ExitCode] = ServerStream.stream[IO]
}

object ServerStream {
  def jsonService[F[_]: Effect]: HttpRoutes[F] = new services.DummyService[F].jsonService


  def stream[F[_]: ConcurrentEffect](implicit ec: ExecutionContext): fs2.Stream[F, StreamApp.ExitCode] = {
    BlazeBuilder[F]
      .bindHttp(8080, "0.0.0.0")
      .mountService(jsonService, "/")
      .serve
  }
}
