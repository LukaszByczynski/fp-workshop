package fp.workshop.ddd

import fp.workshop.ddd.domain.board.BoardService
import fp.workshop.ddd.infrastructure.domain.author.AuthorClient

import scala.concurrent.ExecutionContext

object DDDApp extends App {
  private implicit val ec = ExecutionContext.global

  val boardService = BoardService.create

  val authorClient = new AuthorClient
  val user1 = authorClient.findOne("1").get
  val user2 = authorClient.findOne("2").get

  val oopBoard = boardService.createBoard("OOP Programming")
  oopBoard.publishPost("First post", "content", user1)
  oopBoard.publishPost("Second post", "content", user2)

  val fpBoard = boardService.createBoard("FP Programming")
  fpBoard.publishPost("First post", "content", user1)

  boardService.findBoard(oopBoard.boardId).foreach(_.print())
  boardService.findBoard(fpBoard.boardId).foreach(_.print())
}