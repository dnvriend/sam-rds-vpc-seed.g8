package $package$

import scala.util.Random

object Data {
  def randomInteger: Int = Random.nextInt(10000)
  def randomString: String = Random.alphanumeric.take(12).mkString
}
