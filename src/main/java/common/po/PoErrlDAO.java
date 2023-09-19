package common.po;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.example.demo.SpringConfig;

public class PoErrlDAO {
	String DB_URL;
	String DB_USER;
	String DB_PASS;
	String DB_DRIVER;
	
	public PoErrlDAO(SpringConfig config) {
        //接続情報取得
		DB_URL = config.getDBUrl();
		DB_USER = config.getDBUsername();
		DB_PASS = config.getDBPassword();
		DB_DRIVER = config.getDBDriverClassName();
	}

	// インスタンスオブジェクトの生成->返却（コードの簡略化）
	public static PoErrlDAO getInstance(SpringConfig config) {
		return new PoErrlDAO(config);
	}
	
	public ArrayList<PoErrlBean> read(String toriMei, String date_fr, String date_to) {
		String sql = "select ERRL_ID, CREATED_DATE, CREATED_BY, TORIHIKISAKI_MEI, PO_LIST from " +
				 "(select ERRL_ID, CREATED_DATE, CREATED_BY, TORIHIKISAKI_MEI, PO_LIST from ERRLTABLE";
        if (date_fr != null && date_fr.equals("") != true) {
        	date_fr = date_fr.replace("-", "/");	//(yyyy-MM-dd) → (yyyy/MM/dd)
        	sql = sql + " and CREATED_DATE >= '" + date_fr + "'";
        }
        if (toriMei != null && toriMei.equals("") != true) {
        	sql = sql + " and TORI_MEI like '%" + toriMei + "%'";
		}
        sql = sql + " order by CREATED_DATE desc) where rownum <= 50";
        
		//接続処理
		Connection conn = null;
		ArrayList<PoErrlBean> list = new ArrayList<PoErrlBean>();
		try {
			Class.forName(DB_DRIVER);
			conn = DriverManager.getConnection(DB_URL,DB_USER,DB_PASS);
			System.out.println(sql);

			PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            PoErrlBean errl = new PoErrlBean();
    		while(rs.next()) {
    			// ユーザIDと名前をBeanクラスへセット
    			errl.errlId = rs.getString(1);
    			errl.createdDate = rs.getString(2);
    			errl.createdBy = rs.getString(3);
    			errl.toriMei = rs.getString(4);
    			errl.poList = rs.getString(5);
            	// リストにBeanクラスごと格納
    			list.add(errl);
    			//Beanクラスを初期化
    			errl = new PoErrlBean();
    		}
		} catch(SQLException e) {
			// エラーハンドリング
			System.out.println("sql実行失敗");
			e.printStackTrace();
		} catch(ClassNotFoundException e) {
			// エラーハンドリング
			System.out.println("JDBCドライバ関連エラー");
			e.printStackTrace();
		} finally {
			// DB接続を解除
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		// リストを返す
		return list;
	}
	
    public void insertDB(PoErrlBean errl) {
		String sql = "insert into OcrErrlTable(ERRL_ID,CREATED_DATE,CREATED_BY,TORIHIKISAKI_MEI,PO_LIST) " 
				   + "values(?,sysdate,?,?,?)";
    	
		//接続処理
		Connection conn = null;
		try {
			Class.forName(DB_DRIVER);
			conn = DriverManager.getConnection(this.DB_URL, this.DB_USER, this.DB_PASS);
			System.out.println("  errlId: " + errl.errlId + "  createdBy: " + errl.createdBy
					         + "  toriCode: " + errl.toriMei + "  poList: " + errl.poList);
			System.out.println("  sql: " + sql);
			conn.setAutoCommit(false);
	       
			PreparedStatement ps = conn.prepareStatement(sql);
			int i=1;
			ps.setString(i++, errl.errlId);
			ps.setString(i++, errl.createdBy);
			ps.setString(i++, errl.toriMei);
			ps.setString(i++, errl.poList);
			ps.executeUpdate();
			conn.commit();
			
		} catch (Exception e) {
	       e.printStackTrace();
	       
	   	} finally {
			// DB接続を解除
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
	   	}
    }
}
