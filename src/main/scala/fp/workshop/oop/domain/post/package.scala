package fp.workshop.oop.domain

package post {

  case class CreatePost(boardId: String, authorId: String, title: String, content: String)

  case class UpdatePost(id: String, content: String)

  case class Post(
    id: String,
    authorId: String,
    title: String,
    content: String
  )

  case class BoardPosts(posts: Vector[Post])
}
