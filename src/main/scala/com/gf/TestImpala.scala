package com.gf

import com.typesafe.config.ConfigFactory

import java.security.PrivilegedAction
import java.sql.{Connection, DriverManager, ResultSetMetaData, Statement}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.security.UserGroupInformation

import scala.collection.mutable.ListBuffer

object TestImpala {
  var con: Connection = null
  var st: Statement = null

  def main(args: Array[String]): Unit = {
    kerberosAuthenticationAndInitImpalaConnection()
    val querysql =
      s"""select 4 id,22 cnt,22 bbb
         |union all
         |select 6 id,8888 cnt,77777 bbb
         |""".stripMargin
    val result = st.executeQuery(querysql)

    val metaData: ResultSetMetaData = result.getMetaData(); //获取列集
    val columnCount = metaData.getColumnCount(); //获取列的数量
    val columnNames = ListBuffer[String]()
    for (i <- 1 to columnCount) {
      val columnName = metaData.getColumnName(i); //通过序号获取列名,起始值为1
      columnNames += columnName
    }

    //列长度 中间数据
    val colLenth2 = scala.collection.mutable.Map[String, ListBuffer[Long]]()

    val resToMap = scala.collection.mutable.Map[Int, scala.collection.mutable.Map[String, String]]()

    var i = 1
    while (result.next()) {
      val rowResMap = scala.collection.mutable.Map[String, String]()
      for (colname <- columnNames) {
        val xxx = if (colLenth2.contains(s"${colname}")) colLenth2(s"${colname}") else ListBuffer(0L)
        xxx += colname.length.toLong
        xxx += result.getString(s"$colname").length.toLong
        colLenth2 += (colname -> xxx)
        rowResMap += (colname -> result.getString(s"$colname"))
      }
      resToMap += (i -> rowResMap)
      i += 1
    }

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

    for (key <- colLenth.keys) {
      println(key + "=" + colLenth(key))
    }

    println("测试结果:")
    //打印字段名
    var j = 0;
    for (colname <- columnNames) {
      val v = colLenth(s"${colname}").toInt - colname.length
      print(" " * j + colname + " " * v)
      j = j + colLenth(s"${colname}").toInt
    }
    println()
    //打印结果
    for (key <- resToMap.keys) {
      var k = 0
      for (colname <- columnNames) {
        val value = resToMap(key)(colname)
        val v = colLenth(s"${colname}").toInt - value.length
        print(" " * k + value + " " * v)
        k = k + colLenth(s"${colname}").toInt
      }
      println()
    }


    st.close()
    con.close()
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
