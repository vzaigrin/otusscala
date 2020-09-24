import java.util.UUID

import ru.otus.sc.App
import ru.otus.sc.user.model._
import ru.otus.sc.author.model._
import ru.otus.sc.book.model._

// Демо-программа
object Main {
  def main(args: Array[String]): Unit = {
    val app: App = App()

    println("The Library: Demo program")

    // Создадим пользователей и выведем их список
    println()
    println("Add users")
    app.createUser(
      CreateUserRequest(User(None, "Name0", "LastName0", 50, Set(Role.Admin, Role.Reader)))
    )
    app.createUser(CreateUserRequest(User(None, "Name1", "LastName1", 10, Set(Role.Reader))))
    app.createUser(CreateUserRequest(User(None, "Name2", "LastName2", 15, Set(Role.Reader))))
    app.createUser(CreateUserRequest(User(None, "Name3", "LastName3", 20, Set(Role.Reader))))
    app.createUser(CreateUserRequest(User(None, "Name4", "LastName4", 25, Set(Role.Reader))))
    app.createUser(CreateUserRequest(User(None, "Name5", "LastName5", 30, Set(Role.Reader))))
    app.createUser(
      CreateUserRequest(User(None, "Name9", "LastName9", 35, Set(Role.Reader, Role.Manager)))
    )
    app.createUser(CreateUserRequest(User(None, "Name9", "LastName9", 40, Set(Role.Manager))))

    getAllUsers(app).foreach(println)

    // Удалим пользователей с ролью Manager и выведем список оставшихся
    println()
    println("Delete users with Manager role")
    (app.findUsers(FindUsersRequest.ByRole(Role.Manager)) match {
      case FindUsersResponse.Result(users) => users
      case _                               => Seq.empty
    }).foreach { user => app.deleteUser(DeleteUserRequest(user.id.get)) }

    getAllUsers(app).foreach(println)

    // Добавим авторов и выведем их список
    println()
    println("Add authors")

    val tolstoy: UUID =
      createAuthor(app, CreateAuthorRequest(Author(None, "Leo", "Tolstoy"))).id.get
    val dostoevsky: UUID =
      createAuthor(app, CreateAuthorRequest(Author(None, "Fyodor", "Dostoevsky"))).id.get
    val gogol: UUID =
      createAuthor(app, CreateAuthorRequest(Author(None, "Nikolai", "Gogol"))).id.get
    val pushkin: UUID =
      createAuthor(app, CreateAuthorRequest(Author(None, "Alexander", "Pushkin"))).id.get
    val lermontov: UUID =
      createAuthor(app, CreateAuthorRequest(Author(None, "Mikhail", "Lermontov"))).id.get

    getAllAuthors(app).foreach(println)

    // Добавим книги и выведем их список
    println()
    println("Add books")

    app.createBook(CreateBookRequest(Book(None, "War and Peace", Set(tolstoy), 1869, 1274)))
    app.createBook(CreateBookRequest(Book(None, "Anna Karenina", Set(tolstoy), 1877, 864)))
    app.createBook(CreateBookRequest(Book(None, "Resurrection", Set(tolstoy), 1899, 640)))
    app.createBook(CreateBookRequest(Book(None, "The Raid", Set(tolstoy), 1853, 196)))
    app.createBook(CreateBookRequest(Book(None, "After the Ball", Set(tolstoy), 1911, 9)))

    app.createBook(
      CreateBookRequest(Book(None, "Humiliated and Insulted", Set(dostoevsky), 1861, 512))
    )
    app.createBook(
      CreateBookRequest(Book(None, "Crime and Punishment", Set(dostoevsky), 1866, 672))
    )
    app.createBook(CreateBookRequest(Book(None, "The Idiot", Set(dostoevsky), 1869, 640)))
    app.createBook(CreateBookRequest(Book(None, "Demons", Set(dostoevsky), 1872, 768)))
    app.createBook(
      CreateBookRequest(Book(None, "The Brothers Karamazov", Set(dostoevsky), 1880, 992))
    )

    app.createBook(
      CreateBookRequest(Book(None, "Evenings on a Farm Near Dikanka", Set(gogol), 1832, 320))
    )
    app.createBook(CreateBookRequest(Book(None, "Viy", Set(gogol), 1835, 48)))
    app.createBook(CreateBookRequest(Book(None, "Dead Souls", Set(gogol), 1842, 352)))
    app.createBook(CreateBookRequest(Book(None, "The Nose", Set(gogol), 1836, 26)))
    app.createBook(CreateBookRequest(Book(None, "The Overcoat", Set(gogol), 1843, 36)))

    app.createBook(CreateBookRequest(Book(None, "Ruslan and Ludmila", Set(pushkin), 1820, 146)))
    app.createBook(CreateBookRequest(Book(None, "Poltava", Set(pushkin), 1829, 88)))
    app.createBook(CreateBookRequest(Book(None, "The Bronze Horseman", Set(pushkin), 1837, 55)))
    app.createBook(CreateBookRequest(Book(None, "Eugene Onegin", Set(pushkin), 1832, 448)))
    app.createBook(
      CreateBookRequest(Book(None, "The Tale of the Fisherman and the Fish", Set(pushkin), 1835, 3))
    )

    app.createBook(CreateBookRequest(Book(None, "Borodino", Set(lermontov), 1837, 64)))
    app.createBook(CreateBookRequest(Book(None, "A Hero of Our Time", Set(lermontov), 1840, 224)))
    app.createBook(CreateBookRequest(Book(None, "Demon", Set(lermontov), 1842, 75)))
    app.createBook(CreateBookRequest(Book(None, "The Sail", Set(lermontov), 1841, 1)))
    app.createBook(CreateBookRequest(Book(None, "The Fugitive", Set(lermontov), 1846, 18)))

    app.createBook(
      CreateBookRequest(Book(None, "The Great Novels", Set(tolstoy, dostoevsky), 1914, 1300))
    )
    app.createBook(
      CreateBookRequest(Book(None, "The Great Poems", Set(pushkin, lermontov), 1850, 600))
    )

    getAllBooks(app).foreach(println)

    // Найдём все книги по фамилии автора
    println()
    println("Find all books by Fyodor Dostoevsky")

    app.findBooks(FindBooksRequest.ByAuthor(dostoevsky)) match {
      case FindBooksResponse.Result(books) => books.sorted.foreach(println)
      case _                               => println("Not found")
    }

    // Найдём всех авторов по году книги
    println()
    println("Find all Authors by the year of the book \"The Idiot\"")

    findAuthorsByBookYear(app, "The Idiot")
      .map(a => (a.firstName, a.lastName))
      .sorted
      .foreach(a => println(s"${a._1} ${a._2}"))

    // Найдём все книги с количеством страниц более 1000, авторы которых также издавали что-либо с менее, чем 10 страницами
    println()
    println(
      "We will find all books with more than 1000 pages, the authors of which also published something with less than 10 pages"
    )

    findBooksMoreLess(app, 1000, 10).sorted.foreach(println)

  }

  // Функция возвращает список всех пользователей
  def getAllUsers(app: App): Seq[User] =
    (app.findUsers(FindUsersRequest.All()) match {
      case FindUsersResponse.Result(users) => users
      case _                               => Seq.empty
    }).sorted

  // Функция создаёт автора по параметрам и возвращает его
  def createAuthor(app: App, request: CreateAuthorRequest): Author =
    app.createAuthor(request) match {
      case CreateAuthorResponse(author) => author
      case _                            => Author()
    }

  // Функция возвращает список всех авторов
  def getAllAuthors(app: App): Seq[Author] = {
    (app.findAuthors(FindAuthorsRequest.All()) match {
      case FindAuthorsResponse.Result(authors) => authors
      case _                                   => Seq.empty
    }).sorted
  }

  // Функция возвращает список всех книг
  def getAllBooks(app: App): Seq[Book] =
    (app.findBooks(FindBooksRequest.All()) match {
      case FindBooksResponse.Result(books) => books
      case _                               => Seq.empty
    }).sorted

  // Функция возвращает список всех авторов по году книги
  def findAuthorsByBookYear(app: App, title: String): Seq[Author] = {
    // Шаг 1. Находим год издания книги
    app.findBooks(FindBooksRequest.ByTitle(title)) match {
      case FindBooksResponse.Result(book) =>
        // Шаг 2. Находим все книги по году, полученному на Шаге 1.
        app.findBooks(FindBooksRequest.ByYear(book.head.published)) match {
          case FindBooksResponse.Result(books) =>
            // Шаг 3. Получаем список id авторов книг, найденных на Шаге 2.
            val authors: Seq[UUID] = books.flatMap(_.authors).distinct
            // Шаг 4. Возвращаем список авторов по списку id, найденных на шаге 3.
            getAllAuthors(app).filter(a => authors.contains(a.id.get))
          case _ => Seq()
        }
      case _ => Seq()
    }
  }

  // Функция возвращает список книг с количеством страниц более More, авторы которых также издавали что-либо с менее, чем Less страницами
  def findBooksMoreLess(app: App, more: Int, less: Int): Seq[Book] = {
    // Шаг 1. Находим книги, в которых больше More страниц и их авторов
    // Список пар (автор, книга) книг, в которых больше More страниц
    val ab1000: Seq[(UUID, Book)] = getAllBooks(app)
      .filter(b => b.pages > more)
      .flatMap(b => b.authors.map(a => (a, b)))

    // Список авторов книг, в которых больше More страниц
    val a1000: Seq[UUID] = ab1000.map(_._1).distinct

    // Шаг 2. Получаем список книг авторов, найденных на Шаге 1.
    val ab: Seq[(UUID, Seq[Book])] = a1000.map { a =>
      app.findBooks(FindBooksRequest.ByAuthor(a)) match {
        case FindBooksResponse.Result(books) => (a, books)
        case _                               => (a, Seq())
      }
    }

    // Шаг 3. Оставляем авторов, у которых есть книги меньше Less страниц
    val a10: Seq[UUID] = ab
      .map(v => (v._1, v._2.filter(b => b.pages < less)))
      .filter(_._2.nonEmpty)
      .map(_._1)

    // Шаг 4. Оставляем книги авторов, найденных на Шаге 3.
    ab1000.filter(v => a10.contains(v._1)).map(_._2)
  }

}
