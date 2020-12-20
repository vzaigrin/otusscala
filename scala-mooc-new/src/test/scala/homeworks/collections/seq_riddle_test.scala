package homeworks.collections

import homeworks.collections.task_seq_riddle.nextLine
import org.scalatest.flatspec.AnyFlatSpec

class seq_riddle_test extends AnyFlatSpec {

  "nextLine" should "produce next line" in {
    assert(nextLine(List(1, 2, 1, 1)) === List(1, 1, 1, 2, 2, 1))
    assert(nextLine(List(1, 1, 1, 2, 2, 1)) === List(3, 1, 2, 2, 1, 1))
    assert(nextLine(List(3, 1, 2, 2, 1, 1)) === List(1, 3, 1, 1, 2, 2, 2, 1))
  }

  "funSeq" should "produce next line" in {
    assert(task_seq_riddle.funSeq(5) === List(3, 1, 2, 2, 1, 1))
  }

}
