package fp.workshop.ddd.domain.board

import fp.workshop.ddd.infrastructure.domain.advert.Advert
import fp.workshop.ddd.infrastructure.domain.author.Author

final case class Board private(
  private val boardService: BoardService,
  boardId: Board.Id,
  name: String
) {

  def posts: Vector[Post] = {
    boardService.findPosts(boardId)
  }

  def adverts: Vector[Advert] = {
    boardService.findAdvertsForBoard(name)
  }

  def publishPost(title: String, content: String, author: Author): Post = {
    boardService.publishPost(boardId, title, content, author)
  }

  def print(): Unit = {
    boardService
      .findPosts(boardId)
      .map(post =>
        s"=======================\n${post.author.name}: ${post.title}\n${post.content}"
      )
      .foreach(println)
    boardService.findAdvertsForBoard(name).foreach(advert => println(s"adv ${advert.title}"))
  }
}

object Board {
  type Id = String
}