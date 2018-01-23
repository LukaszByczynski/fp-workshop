package fp.workshop.ddd.infrastructure.domain.author

class AuthorClient {

  private val kv = Map(
    "1" -> Author("1", "Joe Test"),
    "2" -> Author("2", "Jimmy False"),
    "3" -> Author("3", "Bob Exception")
  )

  def findOne(id: String): Option[Author] = kv.get(id)

}

