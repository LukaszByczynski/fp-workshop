package fp.workshop.ddd

import cats._
import cats.data.EitherT
import cats.implicits._
import fp.workshop.ddd.domain.board.{Board, BoardService}
import fp.workshop.ddd.infrastructure.domain.author.AuthorClient
import monix.eval.Task

object DDDApp extends App {

  import fp.workshop.ddd.domain.board.Board._

  val authorClient = new AuthorClient
  val user1 = authorClient.findOne("1").get
  val user2 = authorClient.findOne("2").get

  def oopBoardTaskProgram[M[_]: Monad, A[_]](
    implicit boardService: BoardService[M],
    P: Parallel[M, A]): EitherT[M, String, Board] = {
    for {
      oopBoard <- EitherT.liftF(boardService.createBoard("OOP Programming"))
      _ <- EitherT.liftF[M, String, Unit](
        (
          oopBoard.publishPost[M]("First post", "content", user1),
          oopBoard.publishPost[M]("Second post", "content", user2)
        ).parMapN { case _ => () })
    } yield {
      oopBoard
    }
  }

  def fpBoardTaskProgram[M[_]: Monad](implicit boardService: BoardService[M]): EitherT[M, String, Board] = {
    for {
      fpBoard <- EitherT.liftF(boardService.createBoard("MP Programming"))
      _       <- EitherT(fpBoard.publishPost[M]("Mirst post", "content", user1))
    } yield {
      fpBoard
    }
  }

  /*_*/
  def program[M[_]: Monad, A[_]](
    implicit boardService: BoardService[M],
    P: Parallel[M, A],
    fK: (Task ~> M)): EitherT[M, String, String] = {
    for {
      oopBoard <- oopBoardTaskProgram[M, A]
      fpBoard  <- fpBoardTaskProgram[M]
      boards <- List(oopBoard.boardId, fpBoard.boardId)
        .traverse(id => EitherT.fromOptionF(boardService.findBoard(id), "Board not found!"))
      _ <- boards.traverse(board => EitherT.liftF[M, String, Unit](board.print[M]))
    } yield {
      fK.toString;
      "ok"
    }
  }

  import fp.workshop.ddd.infrastructure.domain.tools.instances._
  implicit val boardService2: BoardService[Id] = BoardService.create[Id]

  val result = program[Id, Id]
  println("end: => " + result)
}
