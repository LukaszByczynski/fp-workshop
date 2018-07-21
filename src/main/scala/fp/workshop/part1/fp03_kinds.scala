package fp.workshop.part1

object fp03_kinds {

  // Type    Kind
  // Int     *
  // Option  * => *
  // List    [*] => *
  // Either  [*, *] => *
  trait Sized[F[_]] {
    def size[A](fa: F[A]): Int
  }

  val SizedList: Sized[List] = new Sized[List] {
    override def size[A](fa: List[A]): Int = ???
  }

  // Partial application of the Map type, so that its kind turns from [*, *] => * into * => *
//  def SizedMap[K]: Sized[Map[K, _]] = ???

//  def SizedTuple2[A]: Sized[(A, _)] = ???
}
