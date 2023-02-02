package utils

object tablename {

  //  DWD销售线索
  val dwdlist_sd: List[String] = List("dwd.dwd_sd_clue_customer", "dwd.dwd_sd_clue_dtl", "dwd.dwd_sd_clue_intn_new_vehicle", "dwd.dwd_sd_customer",
    "dwd.dwd_sd_customer_concern", "dwd.dwd_sd_customer_defeat_reason", "dwd.dwd_sd_customer_follow_call", "dwd.dwd_sd_customer_hobby",
    "dwd.dwd_sd_customer_intn_vehicle_history", "dwd.dwd_sd_customer_level_source_dtl", "dwd.dwd_sd_customer_paper_sheet", "dwd.dwd_sd_customer_pending",
    "dwd.dwd_sd_customer_pending_follow", "dwd.dwd_sd_intn_vhcl_dtl")
  //  DWD二手车
  val dwdlist_uc: List[String] = List("")

  //  旧DL销售线索
  val odl_list_sd: List[String] = List("dl.tg_gms_tb_m_bodycolor_prefer", "dl.tg_gms_tb_m_grade_prefer", "dl.tg_gms_tb_m_model_prefer",
    "dl.tg_gms_tb_m_staff", "dl.tg_gms_tbl_activity_info", "dl.tg_gms_tbl_cst_concerns", "dl.tg_gms_tbl_cstcalloutlist", "dl.tg_gms_tbl_customer_exp",
    "dl.tg_gms_tbl_paper", "dl.tg_gms_tbl_snap_customer", "dl.tg_gms_tbl_snap_followbox", "dl.tg_gms_tbl_users", "dl.tg_icrop_cst_hobby",
    "dl.tg_icrop_cstcontactweekofday", "dl.tg_icrop_h_activity", "dl.tg_icrop_h_activity_memo", "dl.tg_icrop_h_prefer_vcl", "dl.tg_icrop_h_request",
    "dl.tg_icrop_h_sales", "dl.tg_icrop_m_activity_result", "dl.tg_icrop_m_customer_dlr", "dl.tg_icrop_m_customer_exp", "dl.tg_icrop_m_customer",
    "dl.tg_icrop_m_customer_vcl", "dl.tg_icrop_m_staff", "dl.tg_icrop_pro_fllwupbox_his", "dl.tg_icrop_pro_nc_exp", "dl.tg_icrop_pro_nc_newvcl_prefer_his",
    "dl.tg_icrop_pro_nc_newvcl_prefer", "dl.tg_icrop_pro_newcustomer_his", "dl.tg_icrop_pro_newcustomer", "dl.tg_icrop_pro_walkinperson",
    "dl.tg_icrop_t_activity", "dl.tg_icrop_t_activity_memo", "dl.tg_icrop_t_activity_new", "dl.tg_icrop_t_prefer_vcl", "dl.tg_icrop_t_request",
    "dl.tg_icrop_t_request_new", "dl.tg_icrop_t_sales", "dl.tg_icrop_t_salesbooking", "dl.tg_icrop_tb_m_source_0", "dl.tg_icrop_tb_m_source_1")
  //  旧DL二手车
  val odl_list_uc: List[String] = List("dl.tg_b2b_relevance", "dl.tg_ubp_tbl_b2b_newcarinfo", "dl.tg_ums_activeinfo", "dl.tg_ums_author_apply_info",
    "dl.tg_ums_customerinfo", "dl.tg_ums_exacthis", "dl.tg_ums_inspectioninfo", "dl.tg_ums_mst_area2", "dl.tg_ums_mst_city", "dl.tg_ums_mst_dealer",
    "dl.tg_ums_mst_memoparts", "dl.tg_ums_mst_region", "dl.tg_ums_mst_repairinfo", "dl.tg_ums_mst_vehicle", "dl.tg_ums_order_sellsinfo", "dl.tg_ums_orderinfo",
    "dl.tg_ums_rating_frame_parts", "dl.tg_ums_rating_organization_parts", "dl.tg_ums_repair_costinfo", "dl.tg_ums_repairhis", "dl.tg_ums_salesinfo",
    "dl.tg_ums_source_1", "dl.tg_ums_source_2", "dl.tg_ums_ucar_stockinginfo", "dl.tg_ums_ucarinfo", "dl.tg_ums_ucarmanage", "dl.tg_ums_vehiclememo")

  //  ODS销售线索
  val odslist_sd: List[String] = List("ods.ods_dms_cs_intn_vhcl_dtl", "ods.ods_dms_sal_vhs_clue", "ods.ods_dms_sal_vhs_clue_avg_dist",
    "ods.ods_dms_sal_vhs_clue_customer", "ods.ods_dms_sal_vhs_clue_intn_new_vehicle", "ods.ods_dms_sal_vhs_customer", "ods.ods_dms_sal_vhs_customer_concern",
    "ods.ods_dms_sal_vhs_customer_actual_arrival", "ods.ods_dms_sal_vhs_customer_defeat_reason", "ods.ods_dms_sal_vhs_customer_first_level_source",
    "ods.ods_dms_sal_vhs_customer_follow_call", "ods.ods_dms_sal_vhs_customer_hobby", "ods.ods_dms_sal_vhs_customer_intn_vehicle",
    "ods.ods_dms_sal_vhs_customer_intn_vehicle_history", "ods.ods_dms_sal_vhs_customer_paper_sheet", "ods.ods_dms_sal_vhs_customer_pending",
    "ods.ods_dms_sal_vhs_customer_pending_follow", "ods.ods_dms_sal_vhs_customer_prefer_contact_time", "ods.ods_dms_sal_vhs_customer_second_level_source")
  //  ODS销售线索每小时
  val odslist_sd_60: List[String] = List("ods.ods_dms_sal_vhs_clue_60", "ods.ods_dms_sal_vhs_clue_customer_60",
    "ods.ods_dms_sal_vhs_clue_intn_new_vehicle_60", "ods.ods_dms_sal_vhs_customer_60", "ods.ods_dms_sal_vhs_customer_intn_vehicle_60",
    "ods.ods_dms_sal_vhs_customer_intn_vehicle_history_60", "ods.ods_dms_sal_vhs_customer_pending_60", "ods.ods_dms_sal_vhs_customer_pending_follow_60")
  //  ODS二手车
  val odslist_uc: List[String] = List("ods.ods_dms_uc_uc_assessment_info", "ods.ods_dms_uc_uc_assessment_ledger", "ods.ods_dms_uc_uc_assessment_vehicle_info",
    "ods.ods_dms_uc_uc_assessment_vehicle_info_2", "ods.ods_dms_uc_uc_assessment_vehicle_info_3", "ods.ods_dms_uc_uc_authen_apply",
    "ods.ods_dms_uc_uc_cust_car_buy_follow", "ods.ods_dms_uc_uc_cust_car_sale_follow", "ods.ods_dms_uc_uc_cust_sales_vio_settl",
    "ods.ods_dms_uc_uc_customer_info", "ods.ods_dms_uc_uc_demand_info", "ods.ods_dms_uc_uc_dlr_sales_vio_settl", "ods.ods_dms_uc_uc_exchange_vehicle_relation",
    "ods.ods_dms_uc_uc_inventory_ledger", "ods.ods_dms_uc_uc_inventory_ledger_transfer", "ods.ods_dms_uc_uc_mst_dealer",
    "ods.ods_dms_uc_uc_mst_elect_defect", "ods.ods_dms_uc_uc_negotiation", "ods.ods_dms_uc_uc_new_vhcl_exchange_achv",
    "ods.ods_dms_uc_uc_purchase_contract_ledger", "ods.ods_dms_uc_uc_purchase_contract_other_cost", "ods.ods_dms_uc_uc_repair_authen_ledger",
    "ods.ods_dms_uc_uc_repair_cost", "ods.ods_dms_uc_uc_repair_item", "ods.ods_dms_uc_uc_sales_contract", "ods.ods_dms_uc_uc_sales_contract_cost",
    "ods.ods_dms_uc_uc_sales_ledger", "ods.ods_dms_uc_uc_authen_cost", "ods.ods_dms_uc_uc_authen_front_check", "ods.ods_dms_uc_uc_authen_front_check_project",
    "ods.ods_dms_uc_uc_car_trader_inquiry_info", "ods.ods_dms_uc_uc_d2b_auction_info", "ods.ods_dms_uc_uc_d2b_auction_offline", "ods.ods_dms_uc_uc_d2b_auction_online",
    "ods.ods_dms_uc_uc_exhibit_material", "ods.ods_dms_uc_uc_exhibit_vehicle_ledger", "ods.ods_dms_uc_uc_inquiry_info", "ods.ods_dms_uc_uc_inventory_ledger_storage",
    "ods.ods_dms_uc_uc_mgt_target_setting", "ods.ods_dms_uc_uc_mst_area_type", "ods.ods_dms_uc_uc_mst_indtor_type", "ods.ods_dms_uc_uc_mst_period_type",
    "ods.ods_dms_uc_uc_retail_achievement", "ods.ods_dms_uc_uc_sales_settlement", "ods.ods_dms_uc_uc_seek_purchase_info", "ods.ods_dms_uc_uc_staff_enter",
    "ods.ods_dms_uc_uc_supply_demand_rela_info", "ods.ods_dms_cs_vhcl_common_model", "ods.ods_dms_cs_vhcl_common_model_franchise", "ods.ods_dms_cs_vhcl_common_model_maker",
    "ods.ods_dms_cs_vhcl_common_model_grade_config", "ods.ods_dms_cs_salesed_vehicle_ledger", "ods.ods_dms_cs_maintain_uc_repair_entrust",
    "ods.ods_dms_cs_meta_second_level_source", "ods.ods_dms_cs_meta_first_level_source", "ods.ods_dms_cs_base_region_new", "ods.ods_dms_cs_base_dealer_basic_info")

  val odslist_uc_ceshi : List[String] = List("ods.ods_dms_uc_uc_cust_car_sale_follow","ods.ods_dms_uc_uc_cust_car_buy_follow")
  val odl_list_partition: List[String] = List()

  val odl_list_no_bitchid_updatetime: List[String] = List("dl.tg_gms_vhc_order_tb_exp", "dl.tg_icrop_tb_m_grade")

  val odl_list_for_updatetime: List[String] = List("dl.tg_icrop_tb_t_chip_his_new")

}

object primary_key{
  val odslist_uc_key = Map("ods.ods_dms_uc_uc_cust_car_sale_follow"-> "negotiation_id","ods.ods_dms_uc_uc_cust_car_buy_follow"-> "negotiation_id")
}