package fp.workshop.ddd.domain.board.infrastructure.repositories

import fp.workshop.ddd.domain.board.infrastructure.repositories.BoardRepository.DbBoard

private[board] trait BoardRepository {

  def findOne(id: String): Option[DbBoard]

  def findAll(): Vector[DbBoard]

  def store(author: DbBoard): DbBoard

  def delete(id: String): Boolean

}

private[board] object BoardRepository {
  final case class DbBoard(id: String, name: String)

  class BoardRepositoryImpl extends BoardRepository {

    private var kv = Map[String, DbBoard]()

    def findOne(id: String): Option[DbBoard] = kv.get(id)

    def findAll(): Vector[DbBoard] = kv.values.toVector

    def store(board: DbBoard): DbBoard = {
      kv = kv.updated(board.id, board)
      board
    }

    def delete(id: String): Boolean = {
      kv = kv - id
      true
    }
  }
}