package fp.workshop.ddd.infrastructure.domain.author

case class Author(
  id: String,
  name: String,
  description: Option[String] = None
) {

  require(id.nonEmpty, "id must be set")
  require(name.nonEmpty, "name must be set")
}
