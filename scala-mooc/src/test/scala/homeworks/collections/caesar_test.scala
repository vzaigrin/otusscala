package homeworks.collections

import homeworks.collections.task_caesar.{decrypt, encrypt}
import org.scalatest.flatspec.AnyFlatSpec

class caesar_test extends AnyFlatSpec {

  "caesar encryptor" should "encrypt and decrypt simple words" in {
    assert(encrypt("SCALA", 2) === "UECNC")
    assert(decrypt("UECNC", 2) === "SCALA")
  }

  it should "encrypt and decrypt with little offset" in {
    assert(decrypt(encrypt("SCALA", 2), 2) === "SCALA")
  }

  it should "encrypt and decrypt with big offset" in {
    assert(decrypt(encrypt("SCALAPENIO", 242), 242) === "SCALAPENIO")
  }

  it should "encrypt and decrypt empty string" in {
    assert(decrypt(encrypt("", 1), 1) === "")
  }

  it should "compose encryptions" in {
    assert(encrypt(encrypt("ILOVESCALA", 8), 20) === encrypt("ILOVESCALA", 28))
  }

  it should "consider cycles" in {
    assert(encrypt("XYZ", 3) === "ABC")
  }
}
