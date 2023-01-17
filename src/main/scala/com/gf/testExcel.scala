package com.gf

import org.apache.poi.xssf.usermodel.{XSSFCell, XSSFRow, XSSFSheet, XSSFWorkbook}
import utils.DateTimeUtil

import java.io.{File, FileOutputStream}
import java.sql.ResultSet
import scala.io.Source

object testExcel {

  def main(args: Array[String]): Unit = {


    val fileName = "D:\\myway\\DMS\\05开发脚本\\2022年中\\DWD\\供需\\供需dwd-测试脚本.sql"
    val testSql = Source.fromFile(fileName).mkString

    val file = new File("./hello_test2.xlsx")

    val fos = new FileOutputStream(file)

    val workbook = new XSSFWorkbook
    val sheet = workbook.createSheet

    val sqlsplited = testSql.split("--XX============")

    var table_name: String = null
    var test_name: String = null
    var vSql: String = null

    for (i <- 0 to sqlsplited.length - 3) {

      println(i)

      printRes(rs = null, "xx", "ct", i, sheet, "sdfas")

    }

    workbook.write(fos)
    workbook.close()

    fos.close()

  }


  def printRes(rs: ResultSet, table_name: String, test_name: String, vRow: Int, sheet: XSSFSheet, vSql: String): Unit = {

    var nCell: XSSFCell = null
    var nRow:XSSFRow = null

    nRow = sheet.createRow(vRow)

    nCell = nRow.createCell(1)
    nCell.setCellValue(DateTimeUtil.getNowTime("yyyy-MM-dd HH:mm:ss"))

    nCell = nRow.createCell(2)
    nCell.setCellValue(vRow)

    nCell = nRow.createCell(5)
    nCell.setCellValue(table_name)

    nCell = nRow.createCell(19)
    nCell.setCellValue(test_name)

    nCell = nRow.createCell(21)
    nCell.setCellValue(vSql)

  }

}
