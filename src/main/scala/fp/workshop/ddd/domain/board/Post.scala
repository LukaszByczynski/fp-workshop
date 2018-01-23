package fp.workshop.ddd.domain.board

import fp.workshop.ddd.infrastructure.domain.author.Author

final case class Post private[board](
  id: String,
  title: String,
  content: String,
  author: Author
)