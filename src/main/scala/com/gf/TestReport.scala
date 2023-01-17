package com.gf

import com.typesafe.config.ConfigFactory

import java.security.PrivilegedAction
import java.sql.{Connection, DriverManager, ResultSet, ResultSetMetaData, Statement}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.security.UserGroupInformation
import org.apache.poi.xssf.usermodel.{XSSFCell, XSSFRow, XSSFSheet, XSSFWorkbook}

import java.io.{File, FileOutputStream}
import scala.collection.mutable.ListBuffer
import scala.io.Source
import utils.DateTimeUtil

/**
 * 测试报告辅助工具，将测试语句、执行结果写入excel
 * 1，用python（table_v2.py）生成测试脚本，检查修改后。确保sql无误
 * 2.程序读取上面的sql文件,将语句、执行结果写到excel（hello_test.xlsx）
 */

object TestReport {

  var con: Connection = null
  var st: Statement = null

  def main(args: Array[String]): Unit = {

    kerberosAuthenticationAndInitImpalaConnection()

    val file = new File("./hello_test.xlsx")
    if (file.exists) file.delete

    val fos = new FileOutputStream(file)

    val fileName = "E:\\工作-文件\\dms_auto_sql\\auto_sql\\test.sql"
    val testSql = Source.fromFile(fileName).mkString

    val workbook = new XSSFWorkbook
    val sheet = workbook.createSheet

    val sqlsplited = testSql.split("--XX============")

    var table_name: String = null
    var test_name: String = null
    var vSql: String = null

    var vrow: Int = 1;

    for (i <- 0 to sqlsplited.length - 1) {
      if (i != 0) {
        val vTable_splited = sqlsplited(i).split("-- TC-")
        for (j <- 0 to vTable_splited.length - 1) {
          if (j == 0) {
            table_name = vTable_splited(0).split("=")(0)
            println(s"-------表序号:${i}------表名:${table_name}------")
          }
          else {
            test_name = "TC-" + vTable_splited(j).split(" ")(0)
            //TC-02 TC-08没有sql
            if (test_name != "TC-02" && test_name != "TC-08") {
              println(s"-------表序号:${i}------表名:${table_name}------测试用例:${test_name}------")
              vSql = "-- TC-" + vTable_splited(j)
              println("测试语句:\n" + vSql)
              val rs = st.executeQuery(vSql)
              toExecl(rs, table_name, test_name, vrow, sheet, vSql)
            }
            else
              toExecl2(table_name, test_name, vrow, sheet, "无sql")
            vrow += 1
          }

        }
      }
    }

    workbook.write(fos)
    workbook.close()

    fos.close()

    st.close()
    con.close()

  }

  //写execl
  def toExecl(rs: ResultSet, table_name: String, test_name: String, vRow: Int, sheet: XSSFSheet, vSql: String): Unit = {

    val metaData: ResultSetMetaData = rs.getMetaData(); //获取列集
    val columnCount = metaData.getColumnCount(); //获取列的数量
    val columnNames = ListBuffer[String]()
    for (i <- 1 to columnCount) {
      val columnName = metaData.getColumnName(i); //通过序号获取列名,起始值为1
      columnNames += columnName
    }

    var nCell: XSSFCell = null
    var nRow: XSSFRow = null

    nRow = sheet.createRow(vRow)

    var isPass: String = null
    var testR: String = null

    val cntL = ListBuffer[Long]()

    //列长度 中间数据
    val colLenth2 = scala.collection.mutable.Map[String, ListBuffer[Long]]()

    val resToMap = scala.collection.mutable.Map[Int, scala.collection.mutable.Map[String, String]]()

    var i = 1

    while (rs.next()) {
      if (test_name == "TC-01") {
        cntL += rs.getLong(4)
      } else {
        isPass = rs.getString(5)
        testR = s"异常条数:" + rs.getString(4)
      }

      val rowResMap = scala.collection.mutable.Map[String, String]()
      for (colname <- columnNames) {
        val xxx = if (colLenth2.contains(s"${colname}")) colLenth2(s"${colname}") else ListBuffer(0L)
        xxx += colname.length.toLong
        xxx += rs.getString(s"$colname").length.toLong
        colLenth2 += (colname -> xxx)
        rowResMap += (colname -> rs.getString(s"$colname"))
      }
      resToMap += (i -> rowResMap)
      i += 1

    }

    if (test_name == "TC-01") {
      if (cntL(0) == cntL(1)) {

        isPass = "通过"
        testR = "数据条数相同"
      }
      else {
        isPass = "未通过"
        testR = "数据条数不相同\n" + cntL(0) + "\n" + cntL(1)
      }

      nCell = nRow.createCell(12)
      nCell.setCellValue(cntL(0))

    }


    nCell = nRow.createCell(1)
    nCell.setCellValue(vRow)

    nCell = nRow.createCell(4)
    nCell.setCellValue(table_name)

    nCell = nRow.createCell(18)
    nCell.setCellValue(test_name)

    nCell = nRow.createCell(21)
    nCell.setCellValue(vSql)

    nCell = nRow.createCell(31)
    nCell.setCellValue(testR)

    nCell = nRow.createCell(35)
    nCell.setCellValue(isPass)

    nCell = nRow.createCell(37)
    nCell.setCellValue(DateTimeUtil.getNowTime("yyyy/MM/dd"))

    nCell = nRow.createCell(39)
    nCell.setCellValue("你的大名")


    //打印结果

    //列长度
    val colLenth = scala.collection.mutable.Map[String, Long]()

    for (key <- colLenth2.keys) {
      var vlength = 0L
      val ss = colLenth2(s"$key")
      for (a <- ss) {
        if (vlength < a)
          vlength = a
      }
      colLenth += (key -> vlength)
    }

    println("测试结果:")
    //打印字段名
    var j = 1
    for (colname <- columnNames) {
      if (j == 1) print(colname) else print("|" + colname)
      j += 1
    }
    println()
    //打印结果
    for (key <- resToMap.keys) {
      var k = 1
      for (colname <- columnNames) {
        val value = resToMap(key)(colname)
        if (k == 1) print(value) else print("|" + value)
        k += 1
      }
      println()
    }


    //    //打印字段名
    //    var j = 0;
    //    for (colname <- columnNames) {
    //      val v = colLenth(s"${colname}").toInt - colname.length
    //      print(" " + colname + " " * v)
    //      j = j + colLenth(s"${colname}").toInt
    //    }
    //    println()
    //    //打印结果
    //    for (key <- resToMap.keys) {
    //      var k = 0
    //      for (colname <- columnNames) {
    //        val value = resToMap(key)(colname)
    //        val v = colLenth(s"${colname}").toInt - value.length
    //        print(" " + value + " " * v)
    //        k = k + colLenth(s"${colname}").toInt
    //      }
    //      println()
    //    }


  }

  //写execl
  def toExecl2(table_name: String, test_name: String, vRow: Int, sheet: XSSFSheet, vSql: String): Unit = {

    var nCell: XSSFCell = null
    var nRow: XSSFRow = null

    nRow = sheet.createRow(vRow)

    nCell = nRow.createCell(1)
    nCell.setCellValue(vRow)

    nCell = nRow.createCell(4)
    nCell.setCellValue(table_name)

    nCell = nRow.createCell(18)
    nCell.setCellValue(test_name)

    nCell = nRow.createCell(21)
    nCell.setCellValue(vSql)

    nCell = nRow.createCell(37)
    nCell.setCellValue(DateTimeUtil.getNowTime("yyyy-MM-dd"))

    nCell = nRow.createCell(39)
    nCell.setCellValue("你的大名")

  }

  /**
   * 获得impala JDBC连接，这边由于集成了Kerberos所以需要，在UserGroupInformation 信息里面进行连接初始化才能够验证权限通过
   * 不然会有该错出现： Unable to obtain Principal Name for authentication
   */
  def getConnect(): Unit = {

    val jdbc = ConfigFactory.load("common.properties")
    val impala_driver: String = jdbc.getString("impala.driver")
    val kerberos_impala_url: String = jdbc.getString("kerberos.impala.url")

    Class.forName(impala_driver)
    con = DriverManager.getConnection(kerberos_impala_url)
    st = con.createStatement
  }

  /**
   * 进行Kerberos认证，并且进行Impala JDBC连接权限初始化
   */
  def kerberosAuthenticationAndInitImpalaConnection(): Unit = {
    val jdbc = ConfigFactory.load("common.properties")
    val kerberos_conf: String = jdbc.getString("kerberos.conf")
    val kerberos_impala_user21: String = jdbc.getString("kerberos.impala.user21")
    val kerberos_impala_keytab21: String = jdbc.getString("kerberos.impala.keytab21")

    val conf = new Configuration
    conf.set("hadoop.security.authentication", "Kerberos")
    //    加载kerberos配置信息，这边也可以不加载，因为会默认在C:\Windows文件夹里面寻找 krb5.ini 配置文件
    System.setProperty("java.security.krb5.conf", kerberos_conf)
    UserGroupInformation.setConfiguration(conf)
    UserGroupInformation.loginUserFromKeytab(kerberos_impala_user21, kerberos_impala_keytab21)
    val loginUser: UserGroupInformation = UserGroupInformation.getLoginUser
    loginUser.doAs(new PrivilegedAction[Void] {
      override def run(): Void = {
        //        初始化impala 连接信息
        getConnect()
        null
      }
    })
  }
}