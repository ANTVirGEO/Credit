package sample;

import java.sql.*;

public class Load implements Runnable {

    @Override
    public void run()	//Этот метод будет выполнен в побочном потоке
    {
        main.label0.setText("Оплаты:");
        main.label1.setText("ДМС:");
        main.label2.setText("Долги:");
        main.label3.setText("Оплаты+ДМС:");
        main.label4.setText("Оплаты+ДМС+Долги:");
        String connectionUrl = "jdbc:sqlserver://APP104;databaseName=medialog710;user=Java;password=VahVah123";
        try {
            if (main.startdate.getText().equals("")) {
                main.label0.setText("Первое поле не может быть пустым! Необходимо вводить даты по типу 2015-02-26");
            }else{
                if(main.enddate.getText().equals("")){
                    main.label0.setText("Второе поле не может быть пустым! Необходимо вводить даты по типу 2015-02-26");
                }
                else{
                    int summa = 0;
                    int itog = 0;
                    String summadolga;
                    int itogdolga = 0;
                    int summadolg = 0;
                    int summastrah = 0;
                    String summastraha;
                    int itogstraha = 0;
                    String textenddate = main.enddate.getText();
                    String textstardate = main.startdate.getText();
                    Connection con = DriverManager.getConnection(connectionUrl);
                    //Подсчет Оплат
                    Statement st = con.createStatement();
                    String SQL = "SELECT\n" +
                            " FM_ACCOUNT_TRAN.FM_ACCOUNT_TRAN_ID,FM_ACCOUNT_TRAN.TRAN_DATE,(cast((PATIENTS.NOM + ' ' + PATIENTS.PRENOM + ' ' + PATIENTS.PATRONYME) AS VARCHAR(100))) FIO_pat,(CONVERT(varchar(8), FM_ACCOUNT_TRAN.TRAN_DATE , 108)) time_trans,FM_ACCOUNT_TRAN.TRAN_SUM,\n" +
                            " FM_SERV.CODE,FM_SERV.LABEL,(case when (FM_ACCOUNT_TRAN.TRAN_TYPE ='A' or FM_ACCOUNT_TRAN.TRAN_TYPE='B') then FM_ACCOUNT_TRAN.TRAN_SUM  else null end) vnes_spisanie,(case when (FM_ACCOUNT_TRAN.TRAN_TYPE ='P' ) then FM_PAYMENTS.TRAN_AMOUNT  else null  end) oplata_usl,FM_BILL.BILL_DATE,\n" +
                            " (case when (FM_ACCOUNT_TRAN.TRAN_TYPE ='P' ) then (CONVERT(varchar(10), FM_ACCOUNT_TRAN_1.TRAN_DATE , 120)+'  /  '+ cast(FM_ACCOUNT_TRAN_1.TRAN_SUM  as  varchar)+'руб.')  else null  end) Oplata_is\n" +
                            " ,FM_PAYMODE.LABEL FM_PAYMODE_LABEL,FM_ORG.CODE FM_ORG_CODE,FM_ORG.LABEL FM_ORG_LABEL,FM_ACCOUNT_TRAN.CASH,FM_ACCOUNT_TRAN.ECR_NUM,\n" +
                            " FM_ACCOUNT_TRAN_1.DEVERS_ID,FM_ACCOUNT_TRAN_1.ECR_NUM FM_ACCOUNT_TRAN_ECR_NUM,FM_ACCOUNT_TRAN_1.CASH FM_ACCOUNT_TRAN_CASH,FM_ACCOUNT_TRAN_1.FM_DEP_ID,FM_ACCOUNT_TRAN_1.mss_Place\n" +
                            "FROM\n" +
                            " FM_ACCOUNT_TRAN FM_ACCOUNT_TRAN LEFT OUTER JOIN FM_ACCOUNT FM_ACCOUNT ON FM_ACCOUNT.FM_ACCOUNT_ID = FM_ACCOUNT_TRAN.FM_ACCOUNT_ID \n" +
                            " LEFT OUTER JOIN PATIENTS PATIENTS ON PATIENTS.PATIENTS_ID = FM_ACCOUNT.PATIENTS_ID \n" +
                            " LEFT OUTER JOIN MEDECINS MEDECINS ON MEDECINS.MEDECINS_ID = FM_ACCOUNT_TRAN.MEDECINS_ID \n" +
                            " LEFT OUTER JOIN FM_PAYMENTS FM_PAYMENTS ON FM_ACCOUNT_TRAN.FM_ACCOUNT_TRAN_ID = FM_PAYMENTS.FM_ACCOUNT_TRAN_ID \n" +
                            " LEFT OUTER JOIN FM_BILLDET_PAY FM_BILLDET_PAY ON FM_BILLDET_PAY.FM_BILLDET_PAY_ID = FM_PAYMENTS.FM_BILLDET_PAY_ID \n" +
                            " LEFT OUTER JOIN FM_BILLDET FM_BILLDET ON FM_BILLDET.FM_BILLDET_ID = FM_BILLDET_PAY.FM_BILLDET_ID \n" +
                            " LEFT OUTER JOIN FM_SERV FM_SERV ON FM_SERV.FM_SERV_ID = FM_BILLDET.FM_SERV_ID \n" +
                            " LEFT OUTER JOIN FM_BILL FM_BILL ON FM_BILL.FM_BILL_ID = FM_BILLDET.FM_BILL_ID \n" +
                            " LEFT OUTER JOIN FM_ACCOUNT_TRAN FM_ACCOUNT_TRAN_1 ON FM_ACCOUNT_TRAN_1.FM_ACCOUNT_TRAN_ID = FM_ACCOUNT_TRAN.FM_MAIN_TRAN_ID \n" +
                            " LEFT OUTER JOIN FM_PAYMODE FM_PAYMODE ON FM_PAYMODE.FM_PAYMODE_ID = FM_ACCOUNT_TRAN.FM_PAYMODE_ID \n" +
                            " LEFT OUTER JOIN MSS_FILIAL_COMP MSS_FILIAL_COMP ON (FM_ACCOUNT_TRAN.mss_Place= MSS_FILIAL_COMP.CompName)\n" +
                            " LEFT OUTER JOIN FM_ORG FM_ORG ON FM_ORG.FM_ORG_ID = MSS_FILIAL_COMP.FM_ORG_ID \n" +
                            "WHERE\n" +
                            " ((PATIENTS.NOM not like 'ТЕСТ--%') AND (FM_ACCOUNT_TRAN.TRAN_DATE > '" + textstardate + "')" +
                            " AND (FM_ACCOUNT_TRAN.TRAN_DATE < '" + textenddate + "') )";
                    System.out.println(SQL);
                    ResultSet rs = st.executeQuery(SQL);
                    while (rs.next()) {
                        summa = rs.getInt("vnes_spisanie");
//                        if (summa>0){
                            itog = itog + summa;
//                        }
                    }
                    main.label0.setText("Оплаты: " + String.valueOf(itog)+" р.");
                    rs.close();
                    st.close();
                    //Подсчет страховых
                    Statement stStrah = con.createStatement();
                    String SQLStrah ="USE Medialog710 \n" +
                            "SELECT DISTINCT \n" +
                            " FM_BILL.BILL_DATE,FM_BILL.FM_BILL_ID,MOTCONSU.DATE_CONSULTATION,FM_SERV.CODE,FM_SERV.LABEL, \n" +
                            " PATIENTS.NOM,PATIENTS.PRENOM,PATIENTS.PATRONYME,FM_BILLDET.PRICE PRICE_1,FM_BILLDET.CNT, \n" +
                            " FM_BILLDET.TOTAL_PRICE,FM_BILLDET.DISCOUNT,FM_BILLDET.PRICE_TO_PAY, \n" +
                            " ( (SELECT   (sum(FM_PAYMENTS_1.TRAN_AMOUNT)) opl FROM  FM_BILLDET_PAY FM_BILLDET_PAY_1 LEFT OUTER JOIN FM_PAYMENTS FM_PAYMENTS_1 ON FM_BILLDET_PAY_1.FM_BILLDET_PAY_ID = FM_PAYMENTS_1.FM_BILLDET_PAY_ID  WHERE  ((FM_BILLDET_PAY_1.PATIENTS_ID is not null) )  AND (FM_BILLDET.FM_BILLDET_ID = FM_BILLDET_PAY_1.FM_BILLDET_ID)) ) OPLATAfact \n" +
                            " ,(coalesce(FM_BILLDET.PRICE_TO_PAY  ,0)  - coalesce( (SELECT   (sum(FM_PAYMENTS_1.TRAN_AMOUNT)) ttt FROM  FM_BILLDET_PAY FM_BILLDET_PAY_1 LEFT OUTER JOIN FM_PAYMENTS FM_PAYMENTS_1 ON FM_BILLDET_PAY_1.FM_BILLDET_PAY_ID = FM_PAYMENTS_1.FM_BILLDET_PAY_ID   JOIN FM_ACCOUNT_TRAN FM_ACCOUNT_TRAN ON FM_ACCOUNT_TRAN.FM_ACCOUNT_TRAN_ID = FM_PAYMENTS_1.FM_ACCOUNT_TRAN_ID  WHERE  ((FM_BILLDET_PAY_1.PATIENTS_ID is not null) and (FM_ACCOUNT_TRAN.FM_PAYMODE_ID=1) or (FM_ACCOUNT_TRAN.FM_PAYMODE_ID=2))  AND (FM_BILLDET.FM_BILLDET_ID = FM_BILLDET_PAY_1.FM_BILLDET_ID)) ,0)) ost \n" +
                            " ,( (SELECT   (min(FM_PAYMODE.CODE)) vid_min FROM  FM_BILLDET_PAY FM_BILLDET_PAY LEFT OUTER JOIN FM_PAYMENTS FM_PAYMENTS ON FM_BILLDET_PAY.FM_BILLDET_PAY_ID = FM_PAYMENTS.FM_BILLDET_PAY_ID   LEFT OUTER JOIN FM_ACCOUNT_TRAN FM_ACCOUNT_TRAN ON FM_ACCOUNT_TRAN.FM_ACCOUNT_TRAN_ID = FM_PAYMENTS.FM_ACCOUNT_TRAN_ID   LEFT OUTER JOIN FM_PAYMODE FM_PAYMODE ON FM_PAYMODE.FM_PAYMODE_ID = FM_ACCOUNT_TRAN.FM_PAYMODE_ID  WHERE  (FM_BILLDET_PAY.PATIENTS_ID is not null)  AND (FM_BILLDET.FM_BILLDET_ID =FM_BILLDET_PAY.FM_BILLDET_ID))+  case when (SELECT   (count(FM_PAYMENTS.FM_PAYMENTS_ID )) klv FROM  FM_BILLDET_PAY FM_BILLDET_PAY_2 LEFT OUTER JOIN FM_PAYMENTS FM_PAYMENTS ON FM_BILLDET_PAY_2.FM_BILLDET_PAY_ID = FM_PAYMENTS.FM_BILLDET_PAY_ID  WHERE  (FM_BILLDET_PAY_2.PATIENTS_ID is not null)  AND (FM_BILLDET.FM_BILLDET_ID =FM_BILLDET_PAY_2.FM_BILLDET_ID)) >1 then ', '+ (SELECT   (max(FM_PAYMODE.CODE)) vid_max FROM  FM_BILLDET_PAY FM_BILLDET_PAY LEFT OUTER JOIN FM_PAYMENTS FM_PAYMENTS ON FM_BILLDET_PAY.FM_BILLDET_PAY_ID = FM_PAYMENTS.FM_BILLDET_PAY_ID   LEFT OUTER JOIN FM_ACCOUNT_TRAN FM_ACCOUNT_TRAN ON FM_ACCOUNT_TRAN.FM_ACCOUNT_TRAN_ID = FM_PAYMENTS.FM_ACCOUNT_TRAN_ID   LEFT OUTER JOIN FM_PAYMODE FM_PAYMODE ON FM_PAYMODE.FM_PAYMODE_ID = FM_ACCOUNT_TRAN.FM_PAYMODE_ID  WHERE  (FM_BILLDET_PAY.PATIENTS_ID is not null)  AND (FM_BILLDET.FM_BILLDET_ID =FM_BILLDET_PAY.FM_BILLDET_ID)) +' | кол-во='+ (SELECT   (cast( count(FM_PAYMENTS.FM_PAYMENTS_ID )  as varchar(2))) ks FROM  FM_BILLDET_PAY FM_BILLDET_PAY LEFT OUTER JOIN FM_PAYMENTS FM_PAYMENTS ON FM_BILLDET_PAY.FM_BILLDET_PAY_ID = FM_PAYMENTS.FM_BILLDET_PAY_ID  WHERE  (FM_BILLDET_PAY.PATIENTS_ID is not null)  AND (FM_BILLDET.FM_BILLDET_ID =FM_BILLDET_PAY.FM_BILLDET_ID))  else '' end) CODE_2 \n" +
                            " ,( (SELECT   (sum(FM_BILLDET_PAY_smo.PRICE )) ssss FROM  FM_BILLDET_PAY FM_BILLDET_PAY_smo WHERE  (FM_BILLDET_PAY_smo.FM_ORG_ID is not null)  AND (FM_BILLDET.FM_BILLDET_ID =FM_BILLDET_PAY_smo.FM_BILLDET_ID)) ) SMOplat \n" +
                            " ,MEDECINS.NOM NOM_2,MEDECINS.PRENOM PRENOM_2,FM_CLINK.CODE CODE_1,FM_ORG.LABEL LABEL_2,FM_BILL.KABINET, \n" +
                            " FM_SERV.CODE_AN,MEDECINS.MEDECINS_ID,(FM_BILL.KRN_CREATE_DATE ) TimeTalon,(coalesce(  FM_CLINK_PATIENTS.DATE_FROM , FM_CONTR.DATE_FROM , null )) data_prog_begin,FM_SERVGRP.CODE FM_SERVGRP_CODE, \n" +
                            " FM_SERVGRP_PATR.CODE CODE_3,MEDECINS.VID_OPLATY,FM_SERV.SERV_TYPE,FM_SERV.SERV_EXT_TYPE,FM_ORG_1.CODE FM_ORG_CODE, \n" +
                            " FM_ORG_1.LABEL FM_ORG_LABEL,MEDECINS_1.NOM MEDECINS_NOM,FM_BILLDET.FM_PRICETYPE_ID,MEDECINS_2.NOM NOM_1,PATIENTS.GOD_ROGDENIQ,\n" +
                            " PATIENTS.KRN_CREATE_DATE\n" +
                            "FROM\n" +
                            " FM_BILLDET FM_BILLDET LEFT OUTER JOIN FM_BILL FM_BILL ON FM_BILL.FM_BILL_ID = FM_BILLDET.FM_BILL_ID \n" +
                            " LEFT OUTER JOIN MEDECINS MEDECINS ON MEDECINS.MEDECINS_ID = FM_BILL.MEDECINS1_ID \n" +
                            " LEFT OUTER JOIN MOTCONSU MOTCONSU ON MOTCONSU.MOTCONSU_ID = FM_BILL.MOTCONSU_ID \n" +
                            " LEFT OUTER JOIN PATIENTS PATIENTS ON PATIENTS.PATIENTS_ID = FM_BILL.PATIENTS_ID \n" +
                            " LEFT OUTER JOIN FM_ORG FM_ORG_1 ON FM_ORG_1.FM_ORG_ID = PATIENTS.KOMPANIYA_INICIATOR \n" +
                            " LEFT OUTER JOIN MOTCONSU MOTCONSU_1 ON MOTCONSU_1.MOTCONSU_ID = FM_BILL.MOTCONSU_MAIN_ID \n" +
                            " LEFT OUTER JOIN MEDECINS MEDECINS_1 ON MEDECINS_1.MEDECINS_ID = FM_BILL.MEDECINS_CREATE_ID \n" +
                            " LEFT OUTER JOIN MEDECINS MEDECINS_2 ON MEDECINS_2.MEDECINS_ID = FM_BILL.MEDECINS2_ID \n" +
                            " LEFT OUTER JOIN FM_SERV FM_SERV ON FM_SERV.FM_SERV_ID = FM_BILLDET.FM_SERV_ID \n" +
                            " LEFT OUTER JOIN FM_SERVGRP FM_SERVGRP ON FM_SERVGRP.FM_SERVGRP_ID = FM_SERV.FM_SERVGRP_ID \n" +
                            " LEFT OUTER JOIN FM_SERVGRP FM_SERVGRP_PATR ON FM_SERVGRP_PATR.FM_SERVGRP_ID = FM_SERVGRP.PARENT \n" +
                            " LEFT OUTER JOIN FM_CLINK FM_CLINK ON FM_CLINK.FM_CLINK_ID = FM_BILLDET.FM_CLINK_ID \n" +
                            " LEFT OUTER JOIN FM_CONTR FM_CONTR ON FM_CONTR.FM_CONTR_ID = FM_CLINK.FM_CONTR_ID \n" +
                            " LEFT OUTER JOIN FM_ORG FM_ORG ON FM_ORG.FM_ORG_ID = FM_CONTR.FM_ORG1_ID \n" +
                            " LEFT OUTER JOIN FM_CLINK_PATIENTS FM_CLINK_PATIENTS ON FM_CLINK_PATIENTS.FM_CLINK_PATIENTS_ID = FM_BILLDET.FM_CLINK_PATIENTS_ID \n" +
                            "WHERE \n" +
                            " ((PATIENTS.NOM not like 'ТЕСТ--%')) \n" +
                            " AND ((FM_BILL.BILL_DATE>='" + textstardate + "')) AND ((FM_BILL.BILL_DATE<'" + textenddate + "')) \n" +
                            " AND ((FM_ORG.LABEL NOT LIKE '%ЛДЦ%'))";
                    System.out.println(SQLStrah);
                    ResultSet rsStrah = stStrah.executeQuery(SQLStrah);
                    while (rsStrah.next()) {
                        summastraha = String.valueOf(rsStrah.getBigDecimal("PRICE_TO_PAY"));
                        summastrah = Integer.valueOf((summastraha).substring(0,summastraha.lastIndexOf(".")));
                        itogstraha = itogstraha + summastrah;
                    }
                    main.label1.setText("ДМС: " + String.valueOf(itogstraha)+" р.");
                    rsStrah.close();
                    stStrah.close();
                    //Подсчет долгов
                    Statement stDolg = con.createStatement();
                    String SQLDolg ="USE Medialog710 \n" +
                            "SELECT \n" +
                            " FM_BILL.FM_BILL_ID,FM_BILL.BILL_DATE,PATIENTS.N_OMON,(PATIENTS.NOM +' '+PATIENTS.PRENOM + ' '+PATIENTS.PATRONYME ) FIO_pat,(MEDECINS.NOM +' '+MEDECINS.PRENOM ) FIO_sozdav,\n" +
                            " FM_BILL.KRN_CREATE_DATE,FM_SERV.CODE,FM_SERV.LABEL,\n" +
                            " (case when FM_PAYMENTS.FM_PAYMENTS_ID =SG_FIRST_TRAN_ZA_USL.FM_PAYMENTS_ID or FM_PAYMENTS.FM_PAYMENTS_ID is null  then FM_BILLDET.PRICE else null end) PRICE \n" +
                            " ,(case when FM_PAYMENTS.FM_PAYMENTS_ID =SG_FIRST_TRAN_ZA_USL.FM_PAYMENTS_ID or FM_PAYMENTS.FM_PAYMENTS_ID is null  then FM_BILLDET.CNT else null end ) CNT \n" +
                            " ,(case when FM_PAYMENTS.FM_PAYMENTS_ID =SG_FIRST_TRAN_ZA_USL.FM_PAYMENTS_ID or FM_PAYMENTS.FM_PAYMENTS_ID is null  then FM_BILLDET.TOTAL_PRICE else null end) TOTAL_PRICE \n" +
                            " ,(case when FM_PAYMENTS.FM_PAYMENTS_ID =SG_FIRST_TRAN_ZA_USL.FM_PAYMENTS_ID or FM_PAYMENTS.FM_PAYMENTS_ID is null  then FM_BILLDET.DISCOUNT  else null end) DISCOUNT \n" +
                            " ,(case when FM_PAYMENTS.FM_PAYMENTS_ID =SG_FIRST_TRAN_ZA_USL.FM_PAYMENTS_ID or FM_PAYMENTS.FM_PAYMENTS_ID is null  then FM_BILLDET_PAY.PRICE   else null end) PRICE_TO_PAY \n" +
                            " ,FM_ACCOUNT_TRAN.TRAN_DATE,\n" +
                            " (FM_BILLDET_PAY.PRICE  - coalesce (  (SELECT   (sum(FM_PAYMENTS_ost.TRAN_AMOUNT )) ostopl FROM  FM_PAYMENTS FM_PAYMENTS_ost WHERE  (FM_BILLDET_PAY.FM_BILLDET_PAY_ID =FM_PAYMENTS_ost.FM_BILLDET_PAY_ID and FM_PAYMENTS_ost.FM_PAYMENTS_ID <=FM_PAYMENTS.FM_PAYMENTS_ID))  ,0)) Ostalos_oplatit \n" +
                            " ,FM_PAYMENTS.TRAN_AMOUNT,FM_PAYMODE.CODE FM_PAYMODE_CODE,(MEDECINS_kassir.NOM +' '+MEDECINS_kassir.PRENOM ) kassir,FM_BILLDET.FM_BILLDET_ID, \n" +
                            " (case when FM_PAYMENTS.FM_PAYMENTS_ID is null or  FM_PAYMENTS.FM_PAYMENTS_ID  = (SELECT   (max(FM_PAYMENTS_max.FM_PAYMENTS_ID )) maxpp FROM  FM_PAYMENTS FM_PAYMENTS_max WHERE  (FM_BILLDET_PAY.FM_BILLDET_PAY_ID =FM_PAYMENTS_max.FM_BILLDET_PAY_ID))     then ( FM_BILLDET_PAY.PRICE  - coalesce (  (SELECT   (sum(FM_PAYMENTS_dolg.TRAN_AMOUNT )) maxd FROM  FM_PAYMENTS FM_PAYMENTS_dolg WHERE  (FM_BILLDET_PAY.FM_BILLDET_PAY_ID =FM_PAYMENTS_dolg.FM_BILLDET_PAY_ID))  ,0)) else null end) summa_dolga_usl \n" +
                            " ,(case when (   FM_BILLDET_PAY.FM_BILLDET_PAY_ID  =  (SELECT   (max( FM_BILLDET_PAY_zzz.FM_BILLDET_PAY_ID  )) mb FROM  FM_BILLDET FM_BILLDET_zzz LEFT OUTER JOIN FM_BILLDET_PAY FM_BILLDET_PAY_zzz ON FM_BILLDET_zzz.FM_BILLDET_ID = FM_BILLDET_PAY_zzz.FM_BILLDET_ID   LEFT OUTER JOIN FM_PAYMENTS FM_PAYMENTS ON FM_BILLDET_PAY_zzz.FM_BILLDET_PAY_ID = FM_PAYMENTS.FM_BILLDET_PAY_ID  WHERE  (FM_BILLDET_PAY_zzz.PATIENTS_ID  is not null and (FM_BILLDET_PAY_zzz.PRICE <>FM_PAYMENTS.TRAN_AMOUNT or FM_PAYMENTS.FM_PAYMENTS_ID is null  ))  AND (FM_BILLDET_zzz.FM_BILL_ID = FM_BILL.FM_BILL_ID))  ) and (FM_ACCOUNT_TRAN.FM_ACCOUNT_TRAN_ID  is null or FM_ACCOUNT_TRAN.FM_ACCOUNT_TRAN_ID  =   (SELECT   (max( FM_ACCOUNT_TRAN.FM_ACCOUNT_TRAN_ID  )) mmax FROM  FM_PAYMENTS FM_PAYMENTS_maxid LEFT OUTER JOIN FM_ACCOUNT_TRAN FM_ACCOUNT_TRAN ON FM_ACCOUNT_TRAN.FM_ACCOUNT_TRAN_ID = FM_PAYMENTS_maxid.FM_ACCOUNT_TRAN_ID   LEFT OUTER JOIN FM_BILLDET_PAY FM_BILLDET_PAY_maxid ON FM_BILLDET_PAY_maxid.FM_BILLDET_PAY_ID = FM_PAYMENTS_maxid.FM_BILLDET_PAY_ID   LEFT OUTER JOIN FM_BILLDET FM_BILLDET_maxid ON FM_BILLDET_maxid.FM_BILLDET_ID = FM_BILLDET_PAY_maxid.FM_BILLDET_ID  WHERE  (FM_BILLDET_PAY_maxid.PATIENTS_ID  is not null and FM_PAYMENTS_maxid.TRAN_AMOUNT <>FM_BILLDET_maxid.PRICE_TO_PAY )  AND (FM_BILLDET_PAY.FM_BILLDET_PAY_ID =FM_PAYMENTS_maxid.FM_BILLDET_PAY_ID))    )  then   ( (SELECT   (Sum( FM_BILLDET_PAY_kopl.PRICE  )) suma FROM  FM_BILLDET FM_BILLDET_kopl LEFT OUTER JOIN FM_BILLDET_PAY FM_BILLDET_PAY_kopl ON FM_BILLDET_kopl.FM_BILLDET_ID = FM_BILLDET_PAY_kopl.FM_BILLDET_ID  WHERE  (FM_BILLDET_PAY_kopl.PATIENTS_ID is not null)  AND (FM_BILL.FM_BILL_ID =FM_BILLDET_kopl.FM_BILL_ID))  - coalesce (  (SELECT   (sum(FM_PAYMENTS.TRAN_AMOUNT )) oplachSum FROM  FM_BILLDET FM_BILLDET_pod LEFT OUTER JOIN FM_BILLDET_PAY FM_BILLDET_PAY ON FM_BILLDET_pod.FM_BILLDET_ID = FM_BILLDET_PAY.FM_BILLDET_ID   LEFT OUTER JOIN FM_PAYMENTS FM_PAYMENTS ON FM_BILLDET_PAY.FM_BILLDET_PAY_ID = FM_PAYMENTS.FM_BILLDET_PAY_ID  WHERE  (FM_BILLDET_PAY.PATIENTS_ID is not null)  AND (FM_BILL.FM_BILL_ID =FM_BILLDET_pod.FM_BILL_ID))  ,0))  else null end) summa_dolga_talon \n" +
                            " ,FM_ACCOUNT_TRAN.FM_ACCOUNT_TRAN_ID,FM_INVOICE.INVOICE_STATE,FM_BILLDET_PAY.FM_CLINK_ID \n" +
                            "FROM \n" +
                            " FM_BILL FM_BILL LEFT OUTER JOIN PATIENTS PATIENTS ON PATIENTS.PATIENTS_ID = FM_BILL.PATIENTS_ID \n" +
                            " LEFT OUTER JOIN MEDECINS MEDECINS ON MEDECINS.MEDECINS_ID = FM_BILL.MEDECINS_CREATE_ID \n" +
                            " LEFT OUTER JOIN FM_BILLDET FM_BILLDET ON FM_BILL.FM_BILL_ID = FM_BILLDET.FM_BILL_ID \n" +
                            " LEFT OUTER JOIN FM_SERV FM_SERV ON FM_SERV.FM_SERV_ID = FM_BILLDET.FM_SERV_ID \n" +
                            " LEFT OUTER JOIN FM_BILLDET_PAY FM_BILLDET_PAY ON FM_BILLDET.FM_BILLDET_ID = FM_BILLDET_PAY.FM_BILLDET_ID \n" +
                            " LEFT OUTER JOIN FM_PAYMENTS FM_PAYMENTS ON FM_BILLDET_PAY.FM_BILLDET_PAY_ID = FM_PAYMENTS.FM_BILLDET_PAY_ID \n" +
                            " LEFT OUTER JOIN FM_ACCOUNT_TRAN FM_ACCOUNT_TRAN ON FM_ACCOUNT_TRAN.FM_ACCOUNT_TRAN_ID = FM_PAYMENTS.FM_ACCOUNT_TRAN_ID \n" +
                            " LEFT OUTER JOIN FM_PAYMODE FM_PAYMODE ON FM_PAYMODE.FM_PAYMODE_ID = FM_ACCOUNT_TRAN.FM_PAYMODE_ID \n" +
                            " LEFT OUTER JOIN MEDECINS MEDECINS_kassir ON MEDECINS_kassir.MEDECINS_ID = FM_ACCOUNT_TRAN.MEDECINS_ID \n" +
                            " LEFT OUTER JOIN sg_first_tran_za_usl SG_FIRST_TRAN_ZA_USL ON FM_BILLDET_PAY.FM_BILLDET_PAY_ID = SG_FIRST_TRAN_ZA_USL.FM_BILLDET_PAY_ID \n" +
                            " LEFT OUTER JOIN FM_INVOICE FM_INVOICE ON FM_INVOICE.FM_INVOICE_ID = FM_BILLDET_PAY.FM_INVOICE_ID \n" +
                            "WHERE \n" +
                            " ( (SELECT  (min( FM_BILLDET_PAY_1.PRICE  )) FROM FM_BILLDET_PAY  FM_BILLDET_PAY_1  WHERE  ( FM_BILLDET_PAY_1.PATIENTS_ID is not null ) AND ( FM_BILLDET_PAY.FM_BILLDET_PAY_ID = FM_BILLDET_PAY_1.FM_BILLDET_PAY_ID ))  -   (SELECT (sum(FM_PAYMENTS.TRAN_AMOUNT)) oplachSum   FROM FM_BILLDET_PAY FM_BILLDET_PAY_2 JOIN FM_PAYMENTS FM_PAYMENTS ON FM_BILLDET_PAY_2.FM_BILLDET_PAY_ID = FM_PAYMENTS.FM_BILLDET_PAY_ID    WHERE (FM_BILLDET_PAY_2.PATIENTS_ID is not null )  AND (  FM_BILLDET_PAY.FM_BILLDET_PAY_ID = FM_BILLDET_PAY_2.FM_BILLDET_PAY_ID  )) >0 or FM_PAYMENTS.FM_PAYMENTS_ID is null and FM_BILLDET_PAY.PATIENTS_ID is not null) \n" +
                            " AND ((PATIENTS.NOM not like 'ТЕСТ--%')) \n" +
                            " AND ((DISCOUNT <> '-100')) \n" +
                            " AND ((INVOICE_STATE<>'D' or INVOICE_STATE is null)) \n" +
                            " AND ((FM_BILL.BILL_DATE>='" + textstardate + "')) AND ((FM_BILL.BILL_DATE<'" + textenddate + "'))";
                    System.out.println(SQLDolg);
                    ResultSet rsDolg = stDolg.executeQuery(SQLDolg);
                    while (rsDolg.next()) {
                        if (rsDolg.getInt("summa_dolga_usl")!=0){
                            summadolga = String.valueOf(rsDolg.getBigDecimal("summa_dolga_usl"));
                            summadolg = Integer.valueOf((summadolga).substring(0,summadolga.lastIndexOf(".")));
                            itogdolga = itogdolga + summadolg;
                        }

                    }
                    main.label2.setText("Долги: " + String.valueOf(itogdolga)+" р.");
                    rsDolg.close();
                    stDolg.close();
                    con.close();
                    //вывод совокупности Выручки и ДМС
                    main.label3.setText("Оплаты+ДМС: " + String.valueOf(itogstraha+itog) + " р.");
                    main.label4.setText("Оплаты+ДМС+Долги: " + String.valueOf((itogstraha+itog)+itogdolga) + " р.");
                    main.label5.setVisible(false);
                    main.button.setEnabled(true);
                    main.enddate.setEnabled(true);
                    main.startdate.setEnabled(true);
                }
            }
        }catch (SQLException ignored) {
        }
//        System.gc();
    }
}
