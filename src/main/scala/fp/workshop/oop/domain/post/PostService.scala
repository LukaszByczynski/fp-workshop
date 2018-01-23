package fp.workshop.oop.domain.post

import java.util.UUID

import fp.workshop.oop.domain.post.repository.{DbPost, PostRepositoryImpl}
import fp.workshop.oop.infrastructure.hermes.HermesClient

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}

class PostService {

  private val repository = new PostRepositoryImpl

  def createPost(newPost: CreatePost)(implicit ec: ExecutionContext): Future[Boolean] = {
    val uuid = UUID.randomUUID()
    repository
      .store(DbPost(uuid, newPost.boardId, newPost.authorId, newPost.title, newPost.content))
      .map { post =>
        HermesClient.publishEvent("post.created", s"${newPost.boardId}:${toPost(post)}")
        true
      }
  }

  def updatePost(updatePost: UpdatePost)(implicit ec: ExecutionContext): Future[Unit] = {
    Try(UUID.fromString(updatePost.id)) match {
      case Success(uuid) =>
        repository.findOne(uuid).flatMap {
          case Some(dbPost) =>
            repository
              .store(dbPost.copy(content = updatePost.content))
              .map(updateDbPost => HermesClient.publishEvent("post.updated", s"${dbPost.boardId}:${toPost(updateDbPost)}"))
          case _ => Future.failed(new NoSuchElementException)
        }

      case _ => throw new IllegalArgumentException("Unknown id type")
    }
  }

  def findAllForBoard(boardId: String)(implicit ec: ExecutionContext): Future[Vector[Post]] = {
    repository.findAllForBoard(boardId).map(_.map(toPost))
  }

  private def toPost(dbPost: DbPost): Post = {
    Post(dbPost.id.toString, dbPost.authorId, dbPost.title, dbPost.content)
  }

}