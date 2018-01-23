package fp.workshop.ddd.domain.board

import java.util.UUID

import fp.workshop.ddd.domain.board.Board.Id
import fp.workshop.ddd.domain.board.infrastructure.repositories.BoardRepository.{BoardRepositoryImpl, DbBoard}
import fp.workshop.ddd.domain.board.infrastructure.repositories.PostRepository.{DbPost, PostRepositoryImpl}
import fp.workshop.ddd.domain.board.infrastructure.repositories.{BoardRepository, PostRepository}
import fp.workshop.ddd.infrastructure.domain.advert.{Advert, AdvertClient}
import fp.workshop.ddd.infrastructure.domain.author.{Author, AuthorClient}
import fp.workshop.oop.infrastructure.hermes.HermesClient
import monix.eval.Task

import scala.concurrent.ExecutionContext

class BoardService private(
  postRepository: PostRepository,
  boardRepository: BoardRepository,
  advertClient: AdvertClient,
  authorClient: AuthorClient
) {

  def createBoard(name: String): Task[Board] = {
    Task {
      val dbBoard = DbBoard(UUID.randomUUID().toString, name)
      boardRepository.store(dbBoard)
      Board(this, dbBoard.id, dbBoard.name)
    }
  }

  def findBoard(boardId: String): Task[Option[Board]] = Task {
    boardRepository.findOne(boardId).map(
      dbBoard => Board(this, dbBoard.id, dbBoard.name)
    )
  }

  private[board] def publishPost(boardId: Id, title: String, content: String, author: Author): Task[Post] = {
    val uuid = UUID.randomUUID()

    Task
      .fromFuture(postRepository.store(DbPost(uuid, boardId, author.id, title, content)))
      .flatMap(_ => Task {
        HermesClient.publishEvent("post.created", s"$boardId:$uuid")
        Post(this, uuid.toString, title, content, author)
      }.onErrorRestart(4))
  }

  private[board] def findPosts(boardId: Id): Task[Vector[Post]] = {
    Task
      .fromFuture(postRepository.findAllForBoard(boardId))
      .flatMap { items =>
        Task {
          items.map { dbPost =>
            val author = authorClient.findOne(dbPost.authorId).getOrElse(throw new NoSuchElementException)
            Post(this, dbPost.id.toString, dbPost.title, dbPost.content, author)
          }
        }
      }
  }

  private[board] def findAdvertsForBoard(phrase: String): Task[Vector[Advert]] = {
    Task.fromFuture {
      advertClient.findAdvertsForPhrase(phrase)
    }
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