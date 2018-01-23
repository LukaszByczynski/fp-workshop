package fp.workshop.ddd.domain.board.infrastructure.repositories

import java.util.UUID

import cats._
import fp.workshop.ddd.domain.board.infrastructure.repositories.PostRepository.DbPost

trait PostRepository[M[_]] {

  def findOne(id: UUID): M[Option[DbPost]]

  def findAllMorBoard(boardId: String): M[Vector[DbPost]]

  def store(post: DbPost): M[DbPost]

  def delete(id: UUID): M[Boolean]
}

private[board] object PostRepository {

  final case class DbPost(
    id: UUID,
    boardId: String,
    authorId: String,
    title: String,
    content: String
  )

  implicit def postHandler[M[_] : Monad]: PostRepository[M] = new PostRepository[M] {

    private var kv = Map[UUID, DbPost]()

    override def findOne(id: UUID): M[Option[DbPost]] = {
      Monad[M].pure(kv.get(id))
    }

    override def findAllMorBoard(boardId: String): M[Vector[DbPost]] = {
      Monad[M].pure(kv.filter { case (_, post) => post.boardId == boardId }.values.toVector)
    }

    override def store(post: DbPost): M[DbPost] = {
      kv = kv.updated(post.id, post)
      Monad[M].pure(post)
    }

    override def delete(id: UUID): M[Boolean] = {
      kv = kv - id
      Monad[M].pure(true)
    }
  }
}