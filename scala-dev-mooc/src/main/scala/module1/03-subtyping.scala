package module1

import module1.subtyping.adt.case_classes.Person
import module1.subtyping.adt.sealed_traits.Card.Spades
import module1.subtyping.type_classes.JsValue.{JsNull, JsNumber, JsString}


object subtyping {


  /**
   * Наследование - это отношение межу типами.
   * Отношение вида подтип / супертип
   * Компилятор способен отслеживать это отношение и следить за его соблюдением
   * В Scala мы имеем возможность указать компилятору на наличие отношения между Generic параметрами
   *
   * Для этого используется комбинация специальных символов(type operator)
   *  <: -   для отношения `подтип`
   *  >: -   для отношения `супертип`
   */

  trait Vehicle
  trait Car        extends Vehicle
  trait Moto       extends Vehicle
  object Harley    extends Moto
  object Mustang   extends Car

  type IsSubtypeOf[A, B >: A]
  type IsSupertypeOf[A, B <: A]


  /**
   *
   * С помощью типа IsSubtypeOf выразить отношение Car и Vehicle
   *
   */

   val t1: IsSubtypeOf[Car, Vehicle] = ???


  /**
   *
   * С помощью типа IsSubtypeOf выразить отношение Car и Mustang
   *
   */

  val t2: IsSupertypeOf[Car, Mustang.type] = ???


  /**
   *
   * С помощью типа выразить отношение Vehicle и Harley, причем чтобы они шли в этом порядке
   *
   */

  val t3: IsSupertypeOf[Vehicle, Harley.type] = ???


  /**
   * В этом примере вам нужно правильно выбрать оператор отношения,
   * чтобы среди идущих ниже выражений, те которые корректны по смыслу компилировались, а остальные нет
   */

  def isInstanceOf[A, B >: A](a: A): Unit = ???




  lazy val mustCompile1    = isInstanceOf[Mustang.type, Car](Mustang)
  lazy val mustCompile2    = isInstanceOf[Harley.type, Moto](Harley)
  // lazy val mustNotCompile1 = isInstanceOf[Mustang.type, Moto](Mustang)
  // lazy val mustNotCompile2 = isInstanceOf[Harley.type, Car](Harley)



  trait Box[+T] {
    def get: T
    def put[TT >: T](v: TT): Unit
  }

  val a : IsSubtypeOf[Box[Car], Box[Vehicle]] = ???


  trait Consumer[-T] {
    def consume(v: T): Unit
    def produce[TT <: T](): TT
  }

  val b : IsSupertypeOf[Consumer[Car], Consumer[Vehicle]] = ???






  object adt{


    object tuples{
      /**
       * Products
       * Произведение типов A * B - это такой тип,
       * который позволит закодировать все возможные комбинации значений типов А и В
       */


      // Int * Boolean ()  true false 2^32 * 2

      // () true
      // () false


      /**
       * Tuples
       * Наиболее общий способ хранить 2 и более кусочка информации в одно время. По русски - кортеж.
       * Вместе с кортежем мы получаем из коробки конструктор / деконсруктор, сравнение, hashCode, copy,
       * красивое строковое представление
       *
       */
       type ProductUnitBoolean = (Unit, Boolean)

       val v1 : ProductUnitBoolean = ((), true)
       val v2 : ProductUnitBoolean = ((), false)


      /**
       * Реализовать тип Person который будет содержать имя и возраст
       */

      type Person = (String, Int)


      /**
       *
       *  Реализовать тип `CreditCard` который может содержать номер (String),
       *  дату окончания (java.time.YearMonth), имя (String), код безопастности (Short)
       */
      type CreditCard = (String, java.time.YearMonth, String, Short)

      val person: Person = ("Alex", 36)
      person._1 // name
      person._2 // age
    }

    object case_classes {
      /**
       * Case classes
       */

      final case class Person(name: String, age: Int)

      val tonyStark: Person = Person("Tony Stark", 42)


      case class CreditCard(number: String, expireDate: java.time.YearMonth, name:String, cvc: Short)

      /**
       * используя паттерн матчинг напечатать имя и возраст
       */

       def printNameAndAge: Unit = tonyStark match {
         case Person(n, _) => println(s"$n, ")
       }


      final case class Employee(name: String, address: Address)
      final case class Address(street: String, number: Int)

      val alex = Employee("Alex", Address("XXX", 221))

      /**
       * воспользовавшись паттерн матчингом напечатать номер из поля адрес
       */

       alex match {
         case Employee(_, Address(_, number)) => println(s"$number")
       }


      /**
       * Паттерн матчинг может содержать литералы.
       * Реализовать паттерн матчинг на alex с двумя кейсами.
       * 1. Имя должно соотвествовать Alex
       * 2. Все остальные
       */

       alex match {
         case Employee("Alex",  _) =>
         case _ =>
       }


      /**
       * Паттерны могут содержать условия. В этом случае case сработает,
       * если и паттерн совпал и условие true.
       * Условия в паттерн матчинге называются гардами.
       */


      /**
       * Реализовать паттерн матчинг на alex с двумя кейсами.
       * 1. Имя должно начинаться с A
       * 2. Все остальные
       */

      alex match {
        case Employee(name,  _) if name.startsWith("A") =>  ???
        case _ =>
      }

      /**
       *
       * Мы можем поместить кусок паттерна в переменную использую `as` паттерн,
       * x @ ..., где x это любая переменная. Это переменная может использоваться, как в условии,
       * так и внутри кейса
       */

      alex match {
        case e @ Employee(_,  _) if e.name.startsWith("A") =>  ???
        case _ =>
      }

      /**
       * Мы можем использовать вертикальную черту `|` для матчинга на альтернативы
       */

       val x: Int = ???
       x match {
         case 1 | 2 | 3 => ???
         case _ => ???
       }


    }


    object either{


      /**
       * Sum
       * Сумма типов A и B - это такой тип,
       * который позволит закодировать все значения типа A и все значения типа B
       */

      // Unit + Boolean  () | true | false


      /**
       * Either - это наиболее общий способ хранить один из двух или более кусочков информации в одно время.
       * Также как и кортежи обладает целым рядом полезных методов
       * Иммутабелен
       */

      type IntOrString = Either[Int, String]

      /**
       * Реализовать экземпляр типа IntOrString с помощью конструктора Right(_)
       */
      val intOrString: IntOrString = Right("")


      /**\
       * Реализовать тип PaymentMethod который может быть представлен одной из альтернатив
       */
      type PaymentMethod = Either[CreditCard, Either[WireTransfer, Cash]]

      final case class CreditCard()
      final case class WireTransfer()
      final case class Cash()

    }

    object sealed_traits{
      /**
       * Также Sum type можно представить в виде sealed trait с набором альтернатив
       */


      sealed trait Card
      object Card {
        final case class Clubs(points: Int)    extends Card // крести
        final case class Diamonds(points: Int) extends Card // бубны
        final case class Spades(points: Int)   extends Card // пики
        final case class Hearts(points: Int)   extends Card // червы
      }

      lazy val card: Card = Card.Spades(10)


      /**
       * Написать паттерн матчинг на 10 пику, и на все остальное
       */

      val Person(a, b) = Person("", 1)

      val M = "Hello"

      val str: String = ???
      str match {
        case M =>
      }
      card match {
        case Spades(10) => println()
        case _ => println()
      }

      /**
       * Написать паттерн матчинг который матчит карты номиналом >= 10
       */

    }





  }

  object type_classes {

    /**
     * Type class - это паттерн родом из Haskel
     * Он позволяет расширять существующие типы новым функционалом,
     * без необходимости менять их исходники или использовать наследование
     *
     * Компоненты паттерна:
     * 1. Сам type class
     * 2. Его экземпляры
     * 3. Методы которые его используют
     *
     *
     * Необходимые Scala конструкции
     *  trait
     *  implicit values
     *  implicit parameters
     *  implicit class
     */

    sealed trait JsValue
    object JsValue{
      final case class JsObject(get: Map[String, JsValue]) extends JsValue
      final case class JsString(get: String) extends JsValue
      final case class JsNumber(get: Double) extends JsValue
      final case object JsNull extends JsValue
    }


    trait JsonWriter[T]{
      def write(v: T): JsValue
    }

    object JsonInstances {
      implicit val strJson = new JsonWriter[String] {
        override def write(v: String): JsValue = JsString(v)
      }

      implicit val intJson = new JsonWriter[Int] {
        override def write(v: Int): JsValue = JsNumber(v)
      }

      implicit def optInstance[A](implicit jsonWriter: JsonWriter[A]) = new JsonWriter[Option[A]] {
        override def write(v: Option[A]): JsValue = v match {
          case Some(v) => jsonWriter.write(v)
          case None => JsNull
        }
      }

    }

    object JsonSyntax {
      implicit class jsonOps[A](v: A){
        def toJson(implicit jsonWriter: JsonWriter[A]) = jsonWriter.write(v)
      }
    }


    object Json{
      def toJson[T](v: T)(implicit jsonWriter: JsonWriter[T]): JsValue = jsonWriter.write(v)
    }

    import JsonInstances._
    import JsonSyntax._

    val jsString: JsValue = Json.toJson("Hello")
    val jsNumber: JsValue = Json.toJson(22)

    val jsString2: JsValue = "Hello".toJson
    val jsN: JsValue = 22.toJson

    Option(22).toJson







    /**
     * в Scala есть специальный метод позволяющий получить инстанс type класса из контекста
     */

     // implicitly


    /**
     * Упаковка имплиситов
     * Имплисты могут располагаться либо внутри объектов / классов / трэйтов
     *
     * Имплиситы помещенные в объект компаньон для типа,
     * автоматически попадают в скоуп, где мы используем данный тип
     */


    /**
     * Поиск имплиситов
     *
     *  - локальные либо наследованные
     *  - импортированные
     *  - объект компаньон
     */



  }



}