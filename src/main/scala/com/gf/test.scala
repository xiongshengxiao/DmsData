package com.gf

import scala.::
import scala.collection.mutable.ListBuffer

object test {

  def main(args: Array[String]): Unit = {

    val colLenth = scala.collection.mutable.Map[String, ListBuffer[Long]]()

    colLenth += ("a" -> ListBuffer(1L, 3L))

    println("a 键存在，对应的值为 :" + colLenth("a"))

    val xx = colLenth("a")

    xx += 22333L

    colLenth += ("a" -> xx)

    println("a 键存在，对应的值为 :" + colLenth("a"))

    val colLenth2 = scala.collection.mutable.Map[String, Long]()

    for (key <- colLenth.keys) {
      println(key)
      var vlength = 0L
      val ss = colLenth(s"$key")
      for (a <- ss) {
        if (vlength < a.toString.length.toLong)
          vlength = a.toString.length.toLong
      }
      colLenth2 += (key -> vlength)
    }

    println(colLenth2("a"))

    println("数据条数".length)

  }
}
