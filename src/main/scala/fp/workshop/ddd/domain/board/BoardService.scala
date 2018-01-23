package fp.workshop.ddd.domain.board

import java.util.UUID

import fp.workshop.ddd.domain.board.Board.Id
import fp.workshop.ddd.domain.board.infrastructure.repositories.{BoardRepository, PostRepository}
import fp.workshop.ddd.domain.board.infrastructure.repositories.BoardRepository.{BoardRepositoryImpl, DbBoard}
import fp.workshop.ddd.domain.board.infrastructure.repositories.PostRepository.{DbPost, PostRepositoryImpl}
import fp.workshop.ddd.infrastructure.domain.advert.{Advert, AdvertClient}
import fp.workshop.ddd.infrastructure.domain.author.{Author, AuthorClient}
import fp.workshop.oop.OopApp.authorService
import fp.workshop.oop.infrastructure.hermes.HermesClient

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

class BoardService private(
  postRepository: PostRepository,
  boardRepository: BoardRepository,
  advertClient: AdvertClient,
  authorClient: AuthorClient
)(implicit ec: ExecutionContext) {

  def createBoard(name: String): Board = {
    val dbBoard = DbBoard(UUID.randomUUID().toString, name)
    boardRepository.store(dbBoard)
    Board(this, dbBoard.id, dbBoard.name)
  }

  def findBoard(boardId: String): Option[Board] = {
    boardRepository.findOne(boardId).map(
      dbBoard => Board(this, dbBoard.id, dbBoard.name)
    )
  }

  private[board] def publishPost(boardId: Id, title: String, content: String, author: Author): Post = {
    val uuid = UUID.randomUUID()
    Await.result(
      postRepository
        .store(DbPost(uuid, boardId, author.id, title, content))
        .map { _ =>
          HermesClient.publishEvent("post.created", s"$boardId:$uuid")
          Post(this, uuid.toString, title, content, author)
        },
      Duration.Inf
    )
  }

  private[board] def findPosts(boardId: Id): Vector[Post] = {
    Await.result(
      postRepository.findAllForBoard(boardId).map(_.map { dbPost =>
        val author = authorClient.findOne(dbPost.authorId).getOrElse(throw new NoSuchElementException)
        Post(this, dbPost.id.toString, dbPost.title, dbPost.content, author)
      }),
      Duration.Inf
    )
  }

  private[board] def findAdvertsForBoard(phrase: String): Vector[Advert] = {
    Await.result(
      advertClient.findAdvertsForPhrase(phrase),
      Duration.Inf
    )
  }
}

object BoardService {

  def create(implicit ec: ExecutionContext): BoardService = {
    new BoardService(
      new PostRepositoryImpl,
      new BoardRepositoryImpl,
      new AdvertClient,
      new AuthorClient
    )
  }
}