package com.gf

import com.typesafe.config.ConfigFactory
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.security.UserGroupInformation
import org.apache.poi.xssf.usermodel.{XSSFCell, XSSFRow, XSSFSheet, XSSFWorkbook}
import utils.tablename.{odslist_sd, odslist_sd_60, odslist_uc,odslist_uc_ceshi}
import utils.primary_key.odslist_uc_key

import java.io.{File, FileOutputStream}
import java.security.PrivilegedAction
import java.sql.{Connection, DriverManager, ResultSet, Statement}

object TestImpalaODS {
  var con: Connection = null
  var st: Statement = null

  def main(args: Array[String]): Unit = {
    kerberosAuthenticationAndInitImpalaConnection()

    val file = new File("./ods.xlsx")
    if (file.exists) file.delete
    val fos = new FileOutputStream(file)
    val workbook = new XSSFWorkbook
    val sheet = workbook.createSheet

    var nCell: XSSFCell = null
    var nRow: XSSFRow = null
    nRow = sheet.createRow(0)

    val update_time = "2023-01-16 00:00:00"

    nCell = nRow.createCell(0)
    nCell.setCellValue("序号")
    nCell = nRow.createCell(1)
    nCell.setCellValue("ods表名")
    nCell = nRow.createCell(2)
    nCell.setCellValue("dl表名")
    nCell = nRow.createCell(3)
    nCell.setCellValue(s"ods总条数")
    nCell = nRow.createCell(4)
    nCell.setCellValue("dl总条数")
    nCell = nRow.createCell(5)
    nCell.setCellValue(s"ods新增条数update_time>=${update_time}")
    nCell = nRow.createCell(6)
    nCell.setCellValue(s"dl新增条数update_time>=${update_time}")

    var vrow: Int = 1;

    for (table_name <- odslist_uc_ceshi) {
//            println(table_name)
      val ld_tn = table_name.replace("ods.ods_", "dl.tg_")
        .replace("dms_cs_", "dms_cs_t_")
        .replace("_dms_apad_", "_dms_apad_t_")
        .replace("_dms_crm_", "_dms_crm_t_")
        .replace("_dms_parts_", "_dms_parts_t_")
        .replace("_dms_sal_", "_dms_sal_t_")
        .replace("_dms_srv_", "_dms_srv_t_")
        .replace("_dms_ts_", "_dms_ts_t_")
        .replace("_dms_uc_", "_dms_uc_t_")
        .replace("_dms_wty_", "_dms_wty_t_")
        .replaceAll("_new$","")

      val odslist_uc_key = Map("ods.ods_dms_uc_uc_cust_car_sale_follow"-> "negotiation_id","ods.ods_dms_uc_uc_cust_car_buy_follow"-> "negotiation_id")
      val RDDdata = odslist_uc_key.get(table_name).get
      println(RDDdata)

      val vsql0 =
        s"""with
           |t1 as (select '${table_name}' as table_name,count(1) ods_cnt,count(if(update_time>='${update_time}',1,null)) ods_cnt_update from ${table_name}),
           |t2 as (select '${ld_tn}' as ld_tn,count(DISTINCT ${RDDdata}) dl_cnt,count(if(update_time>='${update_time}',1,null)) dl_cnt_update from ${ld_tn})
           |select t1.table_name,t2.ld_tn,t1.ods_cnt,t2.dl_cnt,t1.ods_cnt_update,t2.dl_cnt_update from t1 left join t2 on 1=1""".stripMargin
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

      val ods_tn = result.getString(1)
      val ld_tn = result.getString(2)
      val ods_cnt = result.getString(3)
      val dl_cnt = result.getString(4)
      val ods_cnt_update = result.getString(5)
      val dl_cnt_update = result.getString(6)

      nCell = nRow.createCell(0)
      nCell.setCellValue(vRow)
      nCell = nRow.createCell(1)
      nCell.setCellValue(ods_tn)
      nCell = nRow.createCell(2)
      nCell.setCellValue(ld_tn)
      nCell = nRow.createCell(3)
      nCell.setCellValue(ods_cnt)
      nCell = nRow.createCell(4)
      nCell.setCellValue(dl_cnt)
      nCell = nRow.createCell(5)
      nCell.setCellValue(ods_cnt_update)
      nCell = nRow.createCell(6)
      nCell.setCellValue(dl_cnt_update)

      println(s"${ods_tn} ${ld_tn} ${ods_cnt} ${dl_cnt} ${ods_cnt_update} ${dl_cnt_update}")

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
