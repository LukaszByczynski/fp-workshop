package fp.workshop.oop.domain.author

class AuthorService {

  private val kv = Map(
    "1" -> Author("1", "Joe Test"),
    "2" -> Author("1", "Jimmy False"),
    "3" -> Author("1", "Bob Exception")
  )

  def findOne(id: String): Option[Author] = kv.get(id)

}
