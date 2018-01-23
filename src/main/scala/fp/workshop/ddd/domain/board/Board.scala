package fp.workshop.ddd.domain.board

import fp.workshop.ddd.infrastructure.domain.advert.Advert
import fp.workshop.ddd.infrastructure.domain.author.Author
import monix.eval.Task

final case class Board private(
  private val boardService: BoardService,
  boardId: Board.Id,
  name: String
) {

  def posts: Task[Vector[Post]] = {
    boardService.findPosts(boardId)
  }

  def adverts: Task[Vector[Advert]] = {
    boardService.findAdvertsForBoard(name)
  }

  def publishPost(title: String, content: String, author: Author): Task[Post] = {
    boardService.publishPost(boardId, title, content, author)
  }

  def print(): Task[Unit] = {
    Task
      .map2(
        boardService
          .findPosts(boardId)
          .map(_.map(post =>
            s"=======================\n${post.author.name}: ${post.title}\n${post.content}"
          )),
        boardService
          .findAdvertsForBoard(name)
          .map(_.map(advert => s"adv ${advert.title}"))
      ) {
        case (posts, adverts) =>
          posts.foreach(println)
          adverts.foreach(println)
      }
  }
}

object Board {
  type Id = String
}