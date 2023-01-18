package com.gf

import com.typesafe.config.ConfigFactory
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.security.UserGroupInformation
import org.apache.poi.xssf.usermodel.{XSSFCell, XSSFRow, XSSFSheet, XSSFWorkbook}
import utils.DateTimeUtil

import java.security.PrivilegedAction
import java.sql.{Connection, DriverManager, ResultSet, ResultSetMetaData, Statement}
import scala.collection.mutable.ListBuffer
import utils.tablename.{dwdlist_sd,dwdlist_uc}

import java.io.{File, FileOutputStream}

object TestImpalaDWD {
  var con: Connection = null
  var st: Statement = null

  def main(args: Array[String]): Unit = {
    kerberosAuthenticationAndInitImpalaConnection()

    val file = new File("./dwd.xlsx")
    if (file.exists) file.delete
    val fos = new FileOutputStream(file)
    val workbook = new XSSFWorkbook
    val sheet = workbook.createSheet

    val update_time = "2023-01-14 00:00:00"

    var nCell: XSSFCell = null
    var nRow: XSSFRow = null
    nRow = sheet.createRow(0)

    nCell = nRow.createCell(0)
    nCell.setCellValue("序号")
    nCell = nRow.createCell(1)
    nCell.setCellValue("dwd表名")
    nCell = nRow.createCell(2)
    nCell.setCellValue("max_insert_time")
    nCell = nRow.createCell(3)
    nCell.setCellValue("总条数")
    nCell = nRow.createCell(4)
    nCell.setCellValue("来源dms")
    nCell = nRow.createCell(5)
    nCell.setCellValue("来源旧dl")
    nCell = nRow.createCell(6)
    nCell.setCellValue("总条数-非删除")
    nCell = nRow.createCell(7)
    nCell.setCellValue("来源dms-非删除")
    nCell = nRow.createCell(8)
    nCell.setCellValue("来源旧dl-非删除")
    nCell = nRow.createCell(9)
    nCell.setCellValue(s"总条数新增")
    nCell = nRow.createCell(10)
    nCell.setCellValue(s"来源dms新增")
    nCell = nRow.createCell(11)
    nCell.setCellValue(s"来源旧dl新增")
    nCell = nRow.createCell(12)
    nCell.setCellValue(s"总条数新增-非删除")
    nCell = nRow.createCell(13)
    nCell.setCellValue(s"来源dms新增-非删除")
    nCell = nRow.createCell(14)
    nCell.setCellValue(s"来源旧dl新增-非删除(update_time>=${update_time})")

    var vrow: Int = 1;

    for (table_name <- dwdlist_sd) {
      //      println(table_name)

      val vsql0 =
        s"""select '${table_name}' as table_name,max(insert_time) max_insert_time
           |,count(1) total_cnt
           |,count(if(source_system='DMS',1,null)) dms_cnt
           |,count(if(source_system='OLD_DMS',1,null)) old_cnt
           |,count(if(dwd_del_flag=0,1,null)) total_cnt_0
           |,count(if(source_system='DMS' and dwd_del_flag=0,1,null)) dms_cnt_0
           |,count(if(source_system='OLD_DMS' and dwd_del_flag=0,1,null)) old_cnt_0
           |,count(if(update_time>='${update_time}',1,null)) total_cnt_update
           |,count(if(source_system='DMS' and update_time>='${update_time}',1,null)) dms_cnt_update
           |,count(if(source_system='OLD_DMS' and update_time>='${update_time}',1,null)) old_cnt_update
           |,count(if(dwd_del_flag=0 and update_time>='${update_time}',1,null)) total_cnt_0_update
           |,count(if(source_system='DMS' and dwd_del_flag=0 and update_time>='${update_time}',1,null)) dms_cnt_0_update
           |,count(if(source_system='OLD_DMS' and dwd_del_flag=0 and update_time>='${update_time}',1,null)) old_cnt_0_update
           |from ${table_name}
           |""".stripMargin
      toExecl2(st.executeQuery(vsql0), vrow, sheet)
      vrow += 1

    }


    workbook.write(fos)
    workbook.close()

    fos.close()

    st.close()
    con.close()
  }


  //写execl
  def toExecl2(result: ResultSet, vRow: Int, sheet: XSSFSheet): Unit = {

    var nCell: XSSFCell = null
    var nRow: XSSFRow = null

    nRow = sheet.createRow(vRow)

    while (result.next()) {

      val dwd_tn = result.getString(1)
      val max_insert_time = result.getString(2)
      val total_cnt = result.getString(3)
      val dms_cnt = result.getString(4)
      val old_cnt = result.getString(5)
      val total_cnt_0 = result.getString(6)
      val dms_cnt_0 = result.getString(7)
      val old_cnt_0 = result.getString(8)
      val total_cnt_update = result.getString(9)
      val dms_cnt_update = result.getString(10)
      val old_cnt_update = result.getString(11)
      val total_cnt_0_update = result.getString(12)
      val dms_cnt_0_update = result.getString(13)
      val old_cnt_0_update = result.getString(14)

      nCell = nRow.createCell(0)
      nCell.setCellValue(vRow)
      nCell = nRow.createCell(1)
      nCell.setCellValue(dwd_tn)
      nCell = nRow.createCell(2)
      nCell.setCellValue(max_insert_time)
      nCell = nRow.createCell(3)
      nCell.setCellValue(total_cnt)
      nCell = nRow.createCell(4)
      nCell.setCellValue(dms_cnt)
      nCell = nRow.createCell(5)
      nCell.setCellValue(old_cnt)
      nCell = nRow.createCell(6)
      nCell.setCellValue(total_cnt_0)
      nCell = nRow.createCell(7)
      nCell.setCellValue(dms_cnt_0)
      nCell = nRow.createCell(8)
      nCell.setCellValue(old_cnt_0)
      nCell = nRow.createCell(9)
      nCell.setCellValue(total_cnt_update)
      nCell = nRow.createCell(10)
      nCell.setCellValue(dms_cnt_update)
      nCell = nRow.createCell(11)
      nCell.setCellValue(old_cnt_update)
      nCell = nRow.createCell(12)
      nCell.setCellValue(total_cnt_0_update)
      nCell = nRow.createCell(13)
      nCell.setCellValue(dms_cnt_0_update)
      nCell = nRow.createCell(14)
      nCell.setCellValue(old_cnt_0_update)

      println(s"${dwd_tn} ${total_cnt_0} ${dms_cnt_0} ${old_cnt_0} ${total_cnt_update} ${dms_cnt_0_update} ${old_cnt_0_update}")

    }

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
