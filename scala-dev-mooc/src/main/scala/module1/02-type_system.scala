package module1

object type_system {

  /**
   * Scala type system
   *
   * 1. Наследование
   * 2. Дженерики
   * 3. Ограничения на уровне типов
   *
   */

   // Unit


   // Null


   // Nothing



  /**
   *
   * class
   *
   * 1. С помощью классов мы создаем новые типы
   * 2. Классы являются шаблонами для создания объектов
   * 3. Классы могут иметь параметры конструктора / поля / методы
   * 4. Классы могут наследовать другие классы
   * 5. Классы могут содержать дополнительные конструкторы
   *
   */

   class Foo(x: Int, y: Int){
     def a: Int = ???
     val b: Int = ???

     def this(x: Int) = {
       this(x, 0)
     }
  }




   val foo = new Foo(1)




  /**
   * Задание 1: Создать класс "Прямоугольник"(Rectangle), мы должны иметь возможность создавать прямоугольник с заданной
   * длиной(length) и шириной(width), а также вычислять его периметр и площадь
   */

  class Rectangle private (w: Int, l: Int) {
    def area:Int = w * l
    def perimeter: Int = 2 * (w + l)
  }

  object Rectangle{
    def apply(w: Int, l: Int): Rectangle = new Rectangle(w, l)
  }

  val r: Rectangle = Rectangle(1, 4)

  /**
   * object
   *
   * 1. Паттерн одиночка
   * 2. Линивая инициализация
   */

   object F{
    println("hello ")
  }


  /**
   * case class
   *
   * 1. Особый вид класса, экземпляр которого является имутабельным
   * 2. Сериализуемы
   * 3. Для них существует объект компаньон
   * 4. hashCode и equals
   * 5. copy
   * 6. toString
   *
   */

   case class CreditCard(number: String, cvc: Int)

   CreditCard("", 12).copy(number = "123")

  case object J
  /**
   * case object
   *
   * 1. Сериализуемы
   * 2. hashCode
   * 3. toString
   *
   * Используются для создания перечислений или же в качестве сообщений для Акторов
   */

   sealed trait Color
   case object Red extends Color
   case object Green extends Color
   case object Blue extends Color



  /**
   * trait
   *
   * 1. В общем случае ведут себя, как интерфейсы
   * 2. Могут содержать в том числе конкретные методы
   * 3. Не имеют параметров конструктора
   * 4. Нельзя создавать экземпляры, НО
   * 5. Есть возможность множественного подмешивания
   *
   */



  class A {
    def foo() = "A"
  }

  trait B extends A {
    override def foo() = "B" + super.foo()
  }

  trait C extends B {
    override def foo() = "C" + super.foo()
  }

  trait D extends A {
    override def foo() = "D" + super.foo()
  }

  trait E extends C {
    override def foo(): String = "E" + super.foo()
  }

  val v = new A with D with C with B

  // A -> D -> B -> C

  val v1 = new A with E with D with C with B

  // A -> B -> C -> E -> D



  /**
   * Value classes и Universal traits
   * Value classes - это механизм, который позволяет избегать аллокации дополнительных объектов во время исполнения
   * 1. Конструктор может содержать один val параметр
   * 2. Тело может содержать методы, но не val-s, var-s, class-es, object-s, trait-s
   * 3. Можно подмиксовать только universal trait
   *
   *
   * Universal trait - это trait который явно наследует Any, может иметь только def методы и не имеет инициализации
   */

   trait Printable extends Any{
    def print(): Unit = println(this)
  }
   class Id(val raw: String) extends AnyVal with Printable

   implicit class strExt(val str: String) extends AnyVal {
      def trimToOption: Option[String] = ???
   }

   "hello".trimToOption







}
