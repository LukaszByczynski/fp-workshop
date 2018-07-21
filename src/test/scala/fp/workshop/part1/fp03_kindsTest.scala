package fp.workshop.part1

import org.scalatest.FunSuite

class fp03_kindsTest extends FunSuite {
  import fp03_kinds._

  test("list size") {
    assert(SizedList.size(1 :: 2 :: 3 :: Nil) == 3)
  }

  test("map size") {
//    assert(SizedMap.size(Map(1 -> 2, 2 -> 3)) == 2)
  }

  test("tuple size") {
//    assert(SizedTuple2.size(("ss", "aaa")) == 1)
  }
}
