package fp.workshop.oop.domain.post.repository

import java.util.UUID

private[post] case class DbPost(
  id: UUID,
  boardId: String,
  authorId: String,
  title: String,
  content: String
)
