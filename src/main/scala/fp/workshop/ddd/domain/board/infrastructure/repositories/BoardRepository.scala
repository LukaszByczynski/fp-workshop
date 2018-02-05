package fp.workshop.ddd.domain.board.infrastructure.repositories

import cats._
import fp.workshop.ddd.domain.board.infrastructure.repositories.BoardRepository.DbBoard

private[board] trait BoardRepository[M[_]] {

  def findOne(id: String): M[Option[DbBoard]]

  def findAll(): M[Vector[DbBoard]]

  def store(author: DbBoard): M[DbBoard]

  def delete(id: String): M[Boolean]

}

private[board] object BoardRepository {

  final case class DbBoard(id: String, name: String)

  implicit def boardHandler[M[_]: Monad]: BoardRepository[M] = new BoardRepository[M] {

    private var kv = Map[String, DbBoard]()

    def findOne(id: String): M[Option[DbBoard]] = Monad[M].pure(kv.get(id))

    def findAll(): M[Vector[DbBoard]] = Monad[M].pure(kv.values.toVector)

    def store(board: DbBoard): M[DbBoard] = {
      kv = kv.updated(board.id, board)
      Monad[M].pure(board)
    }

    def delete(id: String): M[Boolean] = {
      kv = kv - id
      Monad[M].pure(true)
    }
  }
}
