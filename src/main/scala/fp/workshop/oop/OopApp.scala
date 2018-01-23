package fp.workshop.oop

import fp.workshop.oop.domain.advert.AdvertClient
import fp.workshop.oop.domain.author.AuthorService
import fp.workshop.oop.domain.board.{Board, BoardRepositoryImpl}
import fp.workshop.oop.domain.post.{CreatePost, PostService}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

object OopApp extends App {

  private implicit val ec = ExecutionContext.global

  val advertClient = new AdvertClient()
  val authorService = new AuthorService()
  val boardRepository = new BoardRepositoryImpl()
  val postService = new PostService()

  def createBoards(): Unit = {
    boardRepository.store(Board("1", "OOP Programming"))
    boardRepository.store(Board("2", "FP Programming"))

    postService.createPost(CreatePost("1", "1", "First post", ""))
    postService.createPost(CreatePost("1", "2", "Second post", ""))

    postService.createPost(CreatePost("2", "1", "FP rules", ""))
    ()
  }

  def printBoard(boardId: String): Unit = {
    val posts = Await.result(
      for {
        board <- Future.successful(boardRepository.findOne(boardId))
        posts <- postService.findAllForBoard(boardId)
        adverts <- advertClient.findAdvertsForPhrase(board.map(_.name).getOrElse(""))
      } yield {
        posts.map { post =>
          val author = authorService.findOne(post.authorId).map(_.name).getOrElse("unknown")
          s"=======================\n$author: ${post.title}\n${post.content}\nAdv: ${adverts.map(_.title).mkString(" ")}"
        }
      },
      Duration.Inf
    )

    posts.foreach(println)
  }

  createBoards()
  printBoard("1")
  printBoard("2")
  printBoard("3")
}
