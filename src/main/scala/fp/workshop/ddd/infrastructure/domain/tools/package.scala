package fp.workshop.ddd.infrastructure.domain

package tools {

  import cats._
  import monix.eval.Task
  import monix.execution.Scheduler

  import scala.concurrent.Await
  import scala.concurrent.duration.Duration

  object instances {

    implicit val idToTaskK: (Task ~> Id) = new (Task ~> Id) {
      override def apply[A](fa: Task[A]): Id[A] = {
        Await.result(fa.runAsync(Scheduler.Implicits.global), Duration.Inf)
      }
    }
    implicit val parallelIdWithTask: Parallel[Id, Task] = new Parallel[Id, Task] {
      override def applicative: Applicative[Task] = implicitly[Applicative[Task]]

      override def monad: Monad[Id] = implicitly[Monad[Id]]

      override def sequential: ~>[Task, Id] = new (Task ~> Id) {
        override def apply[A](fa: Task[A]): Id[A] = {
          Await.result(fa.runAsync(Scheduler.Implicits.global), Duration.Inf)
        }
      }

      override def parallel: ~>[Id, Task] = new (Id ~> Task) {
        override def apply[A](fa: Id[A]): Task[A] = Task {
          fa
        }
      }
    }
  }

//  object ?? {
//
//    type Error = String
//    type TaskEither[M[_], A] = EitherT[M, Error, A]
//
////    def <~[M[_] : Monad, A](value: Either[String, A]): TaskEither[A] = EitherT.fromEither(value)
////
////    def <~[M[_] : Monad, A](value: M[Either[String, A]]): TaskEither[A] = EitherT(value)
////
////    def <~[M[_] : Monad, A](value: M[A])(implicit ev: A <:!< Either[_, _]): TaskEither[A] = EitherT.right(value)
//
//
////      def <~[M[_] : Monad, A](x: A): TaskEither[M, A] = EitherT.pure(x)
//      def <~[M[_] : Monad, A](x: Either[Error, A]): TaskEither[M, A] = EitherT.fromEither[M](x)
//      def <~[M[_] : Monad, A](x: M[A]): TaskEither[M, A] = EitherT.liftF(x)
//      def <~~[M[?] : Monad, A](x: M[Either[String, A]]): TaskEither[M, A] = EitherT(x)
////      def <~[M[_] : Monad, A](x: List[Either[Error, A]]): TaskEither[M, List[A]] = EitherT.fromEither[M](x.sequence)
////      def <~[M[_] : Monad, A, X: ClassManifest](x: List[M[A]]): TaskEither[M, List[A]] = EitherT.right(x.sequence)
////      def <~[M[_] : Monad, A, X: ClassManifest, Y: ClassManifest](x: List[TaskEither[M, A]]): TaskEither[M, List[A]] = x.sequence
//  }
}
