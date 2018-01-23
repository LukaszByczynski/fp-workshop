package fp.workshop.ddd.infrastructure.domain.advert

import scala.concurrent.Future

class AdvertClient {

  private val kv = Map[String, Vector[Advert]](
    "OOP Programming" -> Vector(Advert("1", "C++ Book"), Advert("2", "Java Book")),
    "FP Programming" -> Vector(Advert("3", "Haskell Book"))
  )

  def findAdvertsForPhrase(phrase: String): Future[Vector[Advert]] = {
    Future.successful {
      kv.getOrElse(phrase, Vector())
    }
  }
}
