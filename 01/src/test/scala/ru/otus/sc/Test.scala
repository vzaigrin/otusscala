package ru.otus.sc

import org.scalatest.funsuite.AnyFunSuite
import ru.otus.sc.count.dao.CountDao
import ru.otus.sc.count.dao.impl.{CountDaoImpl, CountDaoImplS}
import ru.otus.sc.count.model.{CountRequest, CountResponse}
import ru.otus.sc.count.service.CountService
import ru.otus.sc.count.service.impl.CountServiceImpl
import ru.otus.sc.echo.dao.EchoDao
import ru.otus.sc.echo.dao.impl.EchoDaoImpl
import ru.otus.sc.echo.model.{EchoRequest, EchoResponse}
import ru.otus.sc.echo.service.EchoService
import ru.otus.sc.echo.service.impl.EchoServiceImpl
import ru.otus.sc.fib.dao.FibDao
import ru.otus.sc.fib.dao.impl.FibDaoImpl
import ru.otus.sc.fib.model.{FibRequest, FibResponse}
import ru.otus.sc.fib.service.FibService
import ru.otus.sc.fib.service.impl.FibServiceImpl
import ru.otus.sc.greet.dao.GreetingDao
import ru.otus.sc.greet.dao.impl.GreetingDaoImpl
import ru.otus.sc.greet.model.{GreetRequest, GreetResponse}
import ru.otus.sc.greet.service.GreetingService
import ru.otus.sc.greet.service.impl.GreetingServiceImpl
import ru.otus.sc.key.dao.KeyDao
import ru.otus.sc.key.dao.impl.KeyDaoImpl
import ru.otus.sc.key.model.{KeyRequest, KeyResponse}
import ru.otus.sc.key.service.KeyService
import ru.otus.sc.key.service.impl.KeyServiceImpl
import ru.otus.sc.reverse.dao.ReverseDao
import ru.otus.sc.reverse.dao.impl.ReverseDaoImpl
import ru.otus.sc.reverse.model.{ReverseRequest, ReverseResponse}
import ru.otus.sc.reverse.service.ReverseService
import ru.otus.sc.reverse.service.impl.ReverseServiceImpl

class Test extends AnyFunSuite {
  test("Greting Dao") {
    val greetingDao: GreetingDao = new GreetingDaoImpl
    val result: String           = greetingDao.greetingPrefix + "Test" + greetingDao.greetingPostfix

    assert(result === "Hi,Test!")
  }

  test("Greeting Service") {
    val greetingDao: GreetingDao         = new GreetingDaoImpl
    val greetingService: GreetingService = new GreetingServiceImpl(greetingDao)

    val result: GreetResponse = greetingService.greet(GreetRequest("Dog", isHuman = false))

    assert(result === GreetResponse("AAAAAAAAAA!!!!!!"))
  }

  test("Count Dao") {
    val countDao: CountDao = CountDaoImpl
    countDao.count
    countDao.count
    countDao.count

    val result: Int = countDao.count

    assert(result === 4)
  }

  test("Count Dao Singleton") {
    val countDaoS: CountDaoImplS = new CountDaoImplS()
    countDaoS.getInstance.count
    countDaoS.getInstance.count
    countDaoS.getInstance.count

    val result: Int = countDaoS.getInstance.count

    assert(result === 4)
  }

  test("Count Service") {
    val countDao: CountDao         = CountDaoImpl
    val countService: CountService = new CountServiceImpl(countDao)

    (1 to 5) foreach { _ => countService.count(CountRequest("!")) }
    val result: CountResponse = countService.count(CountRequest("!"))

    assert(result === CountResponse(10))
  }

  test("Eho Dao") {
    val echoDao: EchoDao = new EchoDaoImpl
    val result: String   = echoDao.echoPrefix + "Test" + echoDao.echoPostfix

    assert(result === "Test")
  }

  test("Echo Service") {
    val echoDao: EchoDao         = new EchoDaoImpl
    val echoService: EchoService = new EchoServiceImpl(echoDao)

    val result: EchoResponse = echoService.echo(EchoRequest("Test"))

    assert(result === EchoResponse("Test"))
  }

  test("Key Dao") {
    val keyDao: KeyDao = KeyDaoImpl
    val result: String = keyDao.key(1)

    assert(result === "one")
  }

  test("Key Service") {
    val keyDao: KeyDao         = KeyDaoImpl
    val keyService: KeyService = new KeyServiceImpl(keyDao)

    val result = keyService.key(KeyRequest(10))

    assert(result === KeyResponse("Illegal key"))
  }

  test("Fib Dao") {
    val fibDao: FibDao = new FibDaoImpl()
    val result: Long   = fibDao.fib(10)

    assert(result === 55)
  }

  test("Fib Service") {
    val fibDao: FibDao         = new FibDaoImpl()
    val fibService: FibService = new FibServiceImpl(fibDao)

    val result: FibResponse = fibService.fib(FibRequest(5))

    assert(result === FibResponse(5))
  }

  test("Reverse Dao") {
    val reverseDao: ReverseDao = new ReverseDaoImpl
    val result: String         = reverseDao.reversePrefix + "Test" + reverseDao.reversePostfix

    assert(result === "Test")
  }

  test("Reverse Service") {
    val reverseDao: ReverseDao         = new ReverseDaoImpl
    val reverseService: ReverseService = new ReverseServiceImpl(reverseDao)

    val result: ReverseResponse = reverseService.reverse(ReverseRequest("test"))

    assert(result === ReverseResponse("tset"))
  }

}
