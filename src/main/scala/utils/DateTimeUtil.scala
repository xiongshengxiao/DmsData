package utils

import java.text.SimpleDateFormat
import java.util.{Calendar, Date, TimeZone}

object DateTimeUtil {

  def getNowTime(pattern: String): String = {
    val now: Date = new Date()
    val dateFormat: SimpleDateFormat = new SimpleDateFormat(pattern)
    dateFormat.format(now)
  }

  def main(args: Array[String]): Unit = {

    println(getNowTime("yyyy-MM-dd HH:mm:SS"))
  }
}
