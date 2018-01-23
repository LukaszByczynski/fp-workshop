package fp.workshop.oop.domain.board

trait BoardRepository {

  def findOne(id: String): Option[Board]

  def findAll(): Vector[Board]

  def store(author: Board): Board

  def delete(id: String): Boolean

}

class BoardRepositoryImpl extends BoardRepository {

  private var kv = Map[String, Board]()

  def findOne(id: String): Option[Board] = kv.get(id)

  def findAll(): Vector[Board] = kv.values.toVector

  def store(board: Board): Board = {
    kv = kv.updated(board.id, board)
    board
  }

  def delete(id: String): Boolean = {
    kv = kv - id
    true
  }
}
