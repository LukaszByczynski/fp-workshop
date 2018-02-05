package fp.workshop.ddd.domain.board

import cats._
import cats.implicits._
import fp.workshop.ddd.infrastructure.domain.advert.Advert
import fp.workshop.ddd.infrastructure.domain.author.Author
import monix.eval.Task

final case class Board private (
  boardId: Board.ID,
  name: String
)

object Board {
  type ID = String

  class BoardExtensions(board: Board) {

    def posts[M[_]: Monad](implicit boardService: BoardService[M]): M[Vector[Post]] = {
      boardService.findPosts(board.boardId)
    }

    def adverts[M[_]: Monad](implicit boardService: BoardService[M], fK: (Task ~> M)): M[Vector[Advert]] = {
      boardService.findAdvertsForBoard(board.name)
    }

    def publishPost[M[_]: Monad](
      title: String,
      content: String,
      author: Author
    )(implicit boardService: BoardService[M]): M[Either[String, Post]] = {
      boardService.publishPost(board.boardId, title, content, author)
    }

    def print[M[_]: Monad](implicit boardService: BoardService[M], fK: (Task ~> M)): M[Unit] = {
      for {
        items <- posts[M].map(_.map(post =>
          s"=======================\n${post.author.name}: ${post.title}\n${post.content}"))
        adverts <- adverts[M].map(_.map(advert => s"adv ${advert.title}"))
      } yield {
        items.foreach(println)
        adverts.foreach(println)
      }
    }
  }

  implicit def boardExtensions(board: Board): BoardExtensions = {
    new BoardExtensions(board)
  }

}
