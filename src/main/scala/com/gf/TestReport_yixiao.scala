package com.gf

import com.typesafe.config.ConfigFactory

import java.security.PrivilegedAction
import java.sql.{Connection, DriverManager, ResultSet, ResultSetMetaData, Statement}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.security.UserGroupInformation
import org.apache.poi.xssf.usermodel.{XSSFCell, XSSFRow, XSSFSheet, XSSFWorkbook}

import java.io.{File, FileOutputStream}
import scala.collection.mutable.ListBuffer
import scala.io.{Source, StdIn}
import utils.DateTimeUtil

/**
 * 测试报告辅助工具，将测试语句、执行结果写入excel
 * 1，用python（table_v2.py）生成测试脚本，检查修改后。确保sql无误
 * 2.程序读取上面的sql文件,将语句、执行结果写到excel（测试报告.xlsx）
 */

object TestReport_yixiao {

  var con: Connection = null
  var st: Statement = null

  print("请输入你要生成的测试报告是ODS/旧DL/DWD(1代表ODS & 2代表旧DL & 3代表DWD):")
  var test_report = StdIn.readLine()
  if (test_report != "1" & test_report != "2" & test_report != "3") {
    throw new Exception("输入有误！请重新输入(1&2&3)")
  }

  def main(args: Array[String]): Unit = {

    kerberosAuthenticationAndInitImpalaConnection()

    val file = new File("./测试报告.xlsx")
    if (file.exists) file.delete

    val fos = new FileOutputStream(file)

    val fileName = "E:\\GuangFeng\\dms_auto_sql\\auto_sql\\test.sql"
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
            //TC-02没有sql
            if (test_name != "TC-02") {
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

    // 创建标题
    nRow = sheet.createRow(0)
    nCell = nRow.createCell(0)
    nCell.setCellValue("序号NO.1")
    nCell = nRow.createCell(1)
    nCell.setCellValue("表名")
    nCell = nRow.createCell(2)
    nCell.setCellValue("数据范围")
    nCell = nRow.createCell(3)
    nCell.setCellValue("字段")
    nCell = nRow.createCell(4)
    nCell.setCellValue("结果记录数")
    nCell = nRow.createCell(5)
    nCell.setCellValue("参考数据")
    nCell = nRow.createCell(6)
    nCell.setCellValue("分类编号")
    nCell = nRow.createCell(7)
    nCell.setCellValue("测试用例描述")
    nCell = nRow.createCell(8)
    nCell.setCellValue("测试SQL")
    nCell = nRow.createCell(9)
    nCell.setCellValue("测试结果")
    nCell = nRow.createCell(10)
    nCell.setCellValue("结论")
    nCell = nRow.createCell(11)
    nCell.setCellValue("时间")
    nCell = nRow.createCell(12)
    nCell.setCellValue("执行人")

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
        cntL += rs.getLong(4)
        isPass = rs.getString(5)
        testR = s"异常条数:" + rs.getString(4)
        if (isPass == "通过") {
          nCell = nRow.createCell(4)
          nCell.setCellValue(cntL(0))
          nCell = nRow.createCell(5)
          nCell.setCellValue(cntL(0))
        } else {
          nCell = nRow.createCell(4)
          nCell.setCellValue(cntL(0))
          nCell = nRow.createCell(5)
          nCell.setCellValue(0)
        }
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
      nCell = nRow.createCell(4)
      nCell.setCellValue(cntL(0))
      nCell = nRow.createCell(5)
      nCell.setCellValue(cntL(1))
    }

    if (test_report == "1") {
      val test_case_description = Map[String, String](("TC-01", "对比ODS数据条数与DL数据条数是否一致"), ("TC-03", "判断ODS表时间类型字段值不符合时间格式的记录数是否为0"),
        ("TC-04", "判断ODS表数值类型字段值为NULL的记录数是否等于0"), ("TC-05", "判断ODS表字符串类型字段值为NULL的记录数是否等于0"), ("TC-06", "判断ODS表字符串类型字段值首尾有空格的记录数是否等于0"),
        ("TC-07", "判断ODS表总记录数据与DMS表主键去重记录数差异是否等于0"), ("TC-08", "判断ODS表insert_time与load_time字段值不符合时间格式的记录数是否等于0"))
      for ((k, v) <- test_case_description) {
        if (test_name == k) {
          nCell = nRow.createCell(7)
          nCell.setCellValue(test_case_description.get(k).get)
        }
      }

    } else if (test_report == "2") {
      val test_case_description = Map[String, String](("TC-01", "对比旧DL数据条数与DMS数据条数是否一致"), ("TC-03", "判断旧DL表时间类型字段值不符合时间格式的记录数是否为0"),
        ("TC-04", "判断旧DL表数值类型字段值为NULL的记录数是否等于0"), ("TC-05", "判断旧DL表字符串类型字段值为NULL的记录数是否等于0"),
        ("TC-06", "判断旧DL表字符串类型字段值首尾有空格的记录数是否等于0"), ("TC-07", "判断旧DL表总记录数据与DMS表主键去重记录数差异是否等于0"),
        ("TC-08", "判断DMS关联表数据不在主表条数是否等于0"), ("TC-09", "判断旧DL表load_time字段值不符合时间格式的记录数是否等于0"))
      for ((k, v) <- test_case_description) {
        if (test_name == k) {
          nCell = nRow.createCell(7)
          nCell.setCellValue(test_case_description.get(k).get)
        }
      }

    } else if (test_report == "3") {
      val test_case_description = Map[String, String](("TC-01", "对比DMS、旧DL数据条数之和与DWD数据条数是否一致"), ("TC-02", "对比DWD字段【】枚举值为''的数据条数与ODS字段【】枚举值为''的数据条数是否一致"),
        ("TC-03", "判断DWD表时间类型字段值不符合时间格式的记录数是否为0"), ("TC-04", "判断DWD表数值类型字段值为NULL的记录数是否等于0"), ("TC-05", "判断DWD表字符串类型字段值为NULL的记录数是否等于0"),
        ("TC-06", "判断DWD表字符串类型字段值首尾有空格的记录数是否等于0"), ("TC-07", "判断DWD表总记录数据与DWD表主键去重记录数差异是否等于0"),
        ("TC-08", "判断DMS/旧表关联表数据不在主表条数是否等于0"), ("TC-09", "判断DWD表insert_time字段值不符合时间格式的记录数是否等于0"))
      for ((k, v) <- test_case_description) {
        if (test_name == k) {
          nCell = nRow.createCell(7)
          nCell.setCellValue(test_case_description.get(k).get)
        }
      }

    }

    if (test_report == "1") {
      val field_date = Map[String, String](("TC-01", "count(1)"), ("TC-03", "所有日期/时间格式字段"), ("TC-04", "所有数值类型字段"),
        ("TC-05", "所有字段串类型字段"), ("TC-06", "所有字符串类型字段"), ("TC-07", "业务主键字段"), ("TC-08", "load_time,insert_time"))
      for ((k, v) <- field_date) {
        if (test_name == k) {
          nCell = nRow.createCell(3)
          nCell.setCellValue(field_date.get(k).get)
        }
      }

    } else if (test_report == "2") {
      val field_date = Map[String, String](("TC-01", "count(1)"), ("TC-03", "所有日期/时间格式字段"), ("TC-04", "所有数值类型字段"), ("TC-05", "所有字段串类型字段"),
        ("TC-06", "所有字段"), ("TC-07", "主键字段"), ("TC-08", "外键字段"), ("TC-09", "load_time"))
      for ((k, v) <- field_date) {
        if (test_name == k) {
          nCell = nRow.createCell(3)
          nCell.setCellValue(field_date.get(k).get)
        }
      }

    } else if (test_report == "3") {
      val field_date = Map[String, String](("TC-01", "count(1)"), ("TC-03", "所有日期/时间格式字段"), ("TC-04", "所有数值类型字段"), ("TC-05", "所有字段串类型字段"),
        ("TC-06", "所有字段"), ("TC-07", "主键字段"), ("TC-08", "外键字段"), ("TC-09", "load_time"))
      for ((k, v) <- field_date) {
        if (test_name == k) {
          nCell = nRow.createCell(3)
          nCell.setCellValue(field_date.get(k).get)
        }
      }
    }

    nCell = nRow.createCell(0) //对应的位置是序列号
    nCell.setCellValue(vRow)

    nCell = nRow.createCell(1) //对应的位置是表名
    nCell.setCellValue(table_name)

    nCell = nRow.createCell(2) //对应的位置是表名
    nCell.setCellValue("此处需手动填写最早时间" + "-" + DateTimeUtil.getNowTime("yyyy/MM/dd") + "(截至时间)")

    nCell = nRow.createCell(6) //对应的位置是分类编号
    nCell.setCellValue(test_name)

    nCell = nRow.createCell(8) //对应的位置是测试用例描述
    nCell.setCellValue(vSql)

    nCell = nRow.createCell(9) //对应的位置是测试结果
    nCell.setCellValue(testR)

    nCell = nRow.createCell(10) //对应的位置是结论
    nCell.setCellValue(isPass)

    nCell = nRow.createCell(11) //对应的位置是时间
    nCell.setCellValue(DateTimeUtil.getNowTime("yyyy/MM/dd"))

    nCell = nRow.createCell(12) //对应的位置是执行人
    nCell.setCellValue("肖雄升")


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

    if (test_report == "2") {
      nCell = nRow.createCell(7)
      nCell.setCellValue("对比旧DL字段【】枚举值为''的数据条数与ODS字段【】枚举值为''的数据条数是否一致")
    } else if (test_report == "3") {
      nCell = nRow.createCell(7)
      nCell.setCellValue("对比DWD字段【】枚举值为''的数据条数与ODS字段【】枚举值为''的数据条数是否一致")
    }

    nCell = nRow.createCell(0) //对应的位置是序列号
    nCell.setCellValue(vRow)

    nCell = nRow.createCell(1) //对应的位置是表名
    nCell.setCellValue(table_name)

    nCell = nRow.createCell(6) //对应的位置是分类编号
    nCell.setCellValue(test_name)

    nCell = nRow.createCell(8) //对应的位置是测试用例描述
    nCell.setCellValue(vSql)

    nCell = nRow.createCell(11) //对应的位置是时间
    nCell.setCellValue(DateTimeUtil.getNowTime(""))

    nCell = nRow.createCell(12) //对应的位置是执行人
    nCell.setCellValue("")

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