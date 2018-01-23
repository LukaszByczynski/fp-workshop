package fp.workshop.ddd

import fp.workshop.ddd.domain.board.BoardService
import fp.workshop.ddd.infrastructure.domain.author.AuthorClient
import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

object DDDApp extends App {
  private implicit val ec = ExecutionContext.global

  val boardService = BoardService.create

  val authorClient = new AuthorClient
  val user1 = authorClient.findOne("1").get
  val user2 = authorClient.findOne("2").get

  val oopBoardTask = for {
    oopBoard <- boardService.createBoard("OOP Programming")
    _ <- Task.gather(Seq(
      oopBoard.publishPost("First post", "content", user1),
      oopBoard.publishPost("Second post", "content", user2)
    ))
  } yield oopBoard

  val fpBoardTask = for {
    fpBoard <- boardService.createBoard("FP Programming")
    _ <- fpBoard.publishPost("First post", "content", user1)
  } yield fpBoard

  val task = for {
    oopBoard <- oopBoardTask
    fpBoard <- fpBoardTask
    boards <- Task
      .gather(Seq(
        boardService.findBoard(oopBoard.boardId),
        boardService.findBoard(fpBoard.boardId)
      ))
      .map(_.flatten)
    _ <- Task.gather(boards.map(_.print()))
  } yield {}

  Await.result(task.runAsync(Scheduler.Implicits.global), Duration.Inf)
}