package fp.workshop.ddd.domain.board

import java.util.UUID

import cats._
import cats.data.EitherT
import cats.implicits._
import fp.workshop.ddd.domain.board.infrastructure.repositories.BoardRepository.DbBoard
import fp.workshop.ddd.domain.board.infrastructure.repositories.PostRepository.DbPost
import fp.workshop.ddd.domain.board.infrastructure.repositories.{BoardRepository, PostRepository}
import fp.workshop.ddd.infrastructure.domain.advert.{Advert, AdvertClient}
import fp.workshop.ddd.infrastructure.domain.author.{Author, AuthorClient}
import fp.workshop.oop.infrastructure.hermes.HermesClient
import monix.eval.Task

import scala.util.Try

class BoardService[M[_] : Monad](
  authorClient: AuthorClient,
  advertClient: AdvertClient
)(implicit boardRepository: BoardRepository[M], postRepository: PostRepository[M]) {

  def createBoard(name: String): M[Board] = {
    val dbBoard = DbBoard(UUID.randomUUID().toString, name)

    for {
      _ <- boardRepository.store(dbBoard)
    } yield Board(dbBoard.id, dbBoard.name)
  }

  def findBoard(boardId: String): M[Option[Board]] = {
    boardRepository.findOne(boardId).map(_.map(
      dbBoard => Board(dbBoard.id, dbBoard.name)
    ))
  }

  private[board] def publishPost(boardId: Board.ID, title: String, content: String, author: Author): M[Either[String, Post]] = {
    val uuid = UUID.randomUUID()

    val program = for {
      _ <- EitherT.liftF(postRepository.store(DbPost(uuid, boardId, author.id, title, content)))
      _ <- EitherT.fromEither[M](
        Try(HermesClient.publishEvent("post.created", s"$boardId:$uuid")).toEither.leftMap(_.toString)
      )
    } yield {
      Post(uuid.toString, title, content, author)
    }

    program.value
  }

  def findPosts(boardId: String): M[Vector[Post]] = {
    for {
      items <- postRepository.findAllMorBoard(boardId)
      authors <- Monad[M].pure(
        items.map(dbPost => authorClient.findOne(dbPost.authorId).getOrElse(throw new NoSuchElementException))
      )
    } yield {
      items.zip(authors).map {
        case (item, author) => Post(item.id.toString, item.title, item.content, author)
      }
    }
  }

  private[board] def findAdvertsForBoard(phrase: String)(implicit fK: (Task ~> M)): M[Vector[Advert]] = {
    fK(Task.fromFuture(advertClient.findAdvertsForPhrase(phrase)))
  }
}


object BoardService {

  import fp.workshop.ddd.domain.board.infrastructure.repositories.BoardRepository._
  import fp.workshop.ddd.domain.board.infrastructure.repositories.PostRepository._

  def create[M[_] : Monad] = new BoardService[M](
    new AuthorClient,
    new AdvertClient
  )

}