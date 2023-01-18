package com.gf

import scala.io.StdIn


object ceshi {

  print("请输入你要生成的测试报告是ODS/旧DL/DWD(1代表ODS & 2代表旧DL & 3代表DWD):")
  var test_report = StdIn.readLine()
  if (test_report != "1" & test_report != "2" & test_report != "3") {
    throw new Exception("输入有误！请重新输入(1&2&3)")
  }

  def main(args: Array[String]): Unit = {
    val test_name = "TC-01"
    if (test_report == "1") {
      val description_date = Map[String, String](("TC-01", "对比ODS数据条数与DL数据条数是否一致"), ("TC-03", "判断ODS表时间类型字段值不符合时间格式的记录数是否为0"),
        ("TC-04", "判断ODS表数值类型字段值为NULL的记录数是否等于0"), ("TC-05", "判断ODS表字符串类型字段值为NULL的记录数是否等于0"), ("TC-06", "判断ODS表字符串类型字段值首尾有空格的记录数是否等于0"),
        ("TC-07", "判断ODS表总记录数据与DMS表主键去重记录数差异是否等于0"), ("TC-08", "判断ODS表insert_time与load_time字段值不符合时间格式的记录数是否等于0"))
      for((k,v)<- description_date){
        if (test_name == k){
          println(description_date.get(k))
        }
      }

    } else if (test_report == "2") {
      val description_date = Map[String, String](("TC-01", "对比旧DL数据条数与DMS数据条数是否一致"), ("TC-03", "判断旧DL表时间类型字段值不符合时间格式的记录数是否为0"),
        ("TC-04", "判断旧DL表数值类型字段值为NULL的记录数是否等于0"), ("TC-05", "判断旧DL表字符串类型字段值为NULL的记录数是否等于0"),
        ("TC-06", "判断旧DL表字符串类型字段值首尾有空格的记录数是否等于0"), ("TC-07", "判断旧DL表总记录数据与DMS表主键去重记录数差异是否等于0"),
        ("TC-08", "判断DMS关联表数据不在主表条数是否等于0"), ("TC-09", "判断旧DL表load_time字段值不符合时间格式的记录数是否等于0"))
      //遍历 k，根据 k值获取 value值
//      for(k <- description_date.keySet){
//        println(k +"所对应的 value:"+student.get(k))
//      }
      for((k,v)<- description_date){
        println(k+":"+v)
      }
    } else if (test_report == "3") {
      val description_date = Map[String, String](("TC-01", "对比DMS、旧DL数据条数之和与DWD数据条数是否一致"), ("TC-02", "对比DWD字段【】枚举值为''的数据条数与ODS字段【】枚举值为''的数据条数是否一致"),
        ("TC-03", "判断DWD表时间类型字段值不符合时间格式的记录数是否为0"), ("TC-04", "判断DWD表数值类型字段值为NULL的记录数是否等于0"), ("TC-05", "判断DWD表字符串类型字段值为NULL的记录数是否等于0"),
        ("TC-06", "判断DWD表字符串类型字段值首尾有空格的记录数是否等于0"), ("TC-07", "判断DWD表总记录数据与DWD表主键去重记录数差异是否等于0"),
        ("TC-08", "判断DMS/旧表关联表数据不在主表条数是否等于0"), ("TC-09", "判断DWD表insert_time字段值不符合时间格式的记录数是否等于0"))
      for((k,v)<- description_date){
        println(k+":"+v)
      }
    }

  }
}
