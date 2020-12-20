package homeworks.collections

object task_caesar {

  // Вспомогательная функция
  // Сначала проверяем к какому диапазону относится символ
  // Потом смещаем символ в этом диапазоне
  // Если символ не пренадлежит ни одну из даипазонов, не меняем его
  def rotate(word: String, offset: Int): String = {
    val ranges = List('A' to 'Z', 'a' to 'z', 'А' to 'Я', 'а' to 'я')

    word.map(c => (c, ranges.filter(_.contains(c)))).map { cl =>
      cl._2.headOption match {
        case Some(r) => r((cl._1 - r.head + (offset % r.size) + r.size) % r.size)
        case None => cl._1
      }
    }.mkString
  }

  /**
   * В данном задании Вам предлагается реализовать функции,
   * реализующие кодирование/декодирование строки шифром Цезаря.
   * https://ru.wikipedia.org/wiki/Шифр_Цезаря
   * Алфавит - прописные латинские буквы от A до Z.
   * Сдвиг   - неотрицательное целое число.
   * Пример: при сдвиге 2 слово "SCALA" шифруется как "UECNC".
   */
  /**
   * @param word   входное слово, которое необходимо зашифровать
   * @param offset сдвиг вперёд по алфавиту
   * @return зашифрованное слово
   */
  def encrypt(word: String, offset: Int): String = rotate(word, offset)

  /**
   * @param cipher шифр, который необходимо расшифровать
   * @param offset сдвиг вперёд по алфавиту
   * @return расшифрованное слово
   */
  def decrypt(cipher: String, offset: Int): String = rotate(cipher, -offset)

}
