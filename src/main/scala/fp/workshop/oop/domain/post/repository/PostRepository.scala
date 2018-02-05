package fp.workshop.oop.domain.post.repository

import java.util.UUID

import scala.concurrent.Future

private[post] trait PostRepository {

  def findOne(id: UUID): Future[Option[DbPost]]

  def findAllForBoard(boardId: String): Future[Vector[DbPost]]

  def store(post: DbPost): Future[DbPost]

  def delete(id: UUID): Future[Boolean]
}

private[post] class PostRepositoryImpl extends PostRepository {

  private var kv = Map[UUID, DbPost]()

  override def findOne(id: UUID): Future[Option[DbPost]] = {
    Future.successful(kv.get(id))
  }

  override def findAllForBoard(boardId: String): Future[Vector[DbPost]] = {
    Future.successful(kv.filter { case (_, post) => post.boardId == boardId }.values.toVector)
  }

  override def store(post: DbPost): Future[DbPost] = {
    kv = kv.updated(post.id, post)
    Future.successful(post)
  }

  override def delete(id: UUID): Future[Boolean] = {
    kv = kv - id
    Future.successful(true)
  }
}
