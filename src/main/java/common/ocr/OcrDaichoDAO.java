package ocr;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.example.demo.SpringConfig;

public class OcrDaichoDAO {
	String DB_URL;
	String DB_USER;
	String DB_PASS;
	String DB_DRIVER;
	
	public OcrDaichoDAO(SpringConfig config) {
        //接続情報取得
		DB_URL = config.getDBUrl();
		DB_USER = config.getDBUsername();
		DB_PASS = config.getDBPassword();
		DB_DRIVER = config.getDBDriverClassName();
	}

	// インスタンスオブジェクトの生成->返却（コードの簡略化）
	public static OcrDaichoDAO getInstance(SpringConfig config) {
		return new OcrDaichoDAO(config);
	}
    
	public ArrayList<OcrDaichoBean> getDaicho() {
		String sql = "select OBJECT_ID, CREATED_DATE, OCR_ID, MEISAI_NO, TORI_MEI, CHUMON_BI, SOFU_MEI, CHUMON_BANGO, HINMEI, SURYO, SEIZO_TANKA, KINGAKU, YOUKYU_NOUKI, BIKO from " +
					 "(select OBJECT_ID, CREATED_DATE, OCR_ID, MEISAI_NO, TORI_MEI, CHUMON_BI, SOFU_MEI, CHUMON_BANGO, HINMEI, SURYO, SEIZO_TANKA, KINGAKU, YOUKYU_NOUKI, BIKO " + 
					 "from DAICHO_TABLE order by CREATED_DATE desc, MEISAI_NO) where rownum <= 50";
		
		//接続処理
		Connection conn = null;
		ArrayList<OcrDaichoBean> list = new ArrayList<OcrDaichoBean>();
		try {
			Class.forName(DB_DRIVER);
			conn = DriverManager.getConnection(DB_URL,DB_USER,DB_PASS);
			System.out.println(sql);	//for debug

			PreparedStatement ps = conn.prepareStatement(sql);
			//int i=1;
			//ps.setString(i++, type);
            ResultSet rs = ps.executeQuery();
            
            OcrDaichoBean daicho = null;
    		while(rs.next()) {
    			//Beanクラスを初期化
    			daicho = new OcrDaichoBean();
    			// ユーザIDと名前をBeanクラスへセット
    			daicho.objectId = rs.getString(1);		//"OBJECT_ID"
    			daicho.createdDate = rs.getString(2);	//"CREATED_DATE"
    			daicho.ocrId = rs.getString(3);			//"OCR_ID"
    			daicho.meisaiNo = rs.getString(4);		//"MEISAI_NO"
    			daicho.toriMei = rs.getString(5);		//"TORI_MEI"
    			daicho.chumonBi = rs.getString(6);		//"CHUMON_BI"
    			daicho.sofuMei = rs.getString(7);		//"SOFU_MEI"
    			daicho.chumonBango = rs.getString(8);	//"CHUMON_BANGO"
    			daicho.hinmei = rs.getString(9);		//"HINMEI"
    			daicho.suryo = rs.getString(10);		//"SURYO"
    			daicho.seizoTanka = rs.getString(11);	//"SEIZO_TANKA"
    			daicho.kingaku = rs.getString(12);		//"KINGAKU"
    			daicho.youkyuNouki = rs.getString(13);	//"YOUKYU_NOUKI"
    			daicho.biko = rs.getString(14);			//"BIKO"
            	// リストにBeanクラスごと格納
    			list.add(daicho);
    		}			
		} catch (SQLException e) {
			// エラーハンドリング
			System.out.println("sql実行失敗");
			e.printStackTrace();
			
		} catch (ClassNotFoundException e) {
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
	
	// 検索処理
	// 戻り値		：ArrayList<Beanクラス>
	public ArrayList<OcrDaichoBean> read(String toriMei, String date_fr, String date_to) {
		String sql = "select OBJECT_ID, CREATED_DATE, OCR_ID, MEISAI_NO, TORI_MEI, CHUMON_BI, SOFU_MEI, CHUMON_BANGO, HINMEI, SURYO, SEIZO_TANKA, KINGAKU, YOUKYU_NOUKI, BIKO from " +
				 "(select OBJECT_ID, CREATED_DATE, OCR_ID, MEISAI_NO, TORI_MEI, CHUMON_BI, SOFU_MEI, CHUMON_BANGO, HINMEI, SURYO, SEIZO_TANKA, KINGAKU, YOUKYU_NOUKI, BIKO " +
				 "from DAICHO_TABLE ";
				 //"from DAICHO_TABLE order by CREATED_DATE desc, MEISAI_NO) where rownum <= 50";
        if (date_fr != null && date_fr.equals("") != true) {
        	date_fr = date_fr.replace("-", "/");	//(yyyy-MM-dd) → (yyyy/MM/dd)
        	sql = sql + " and CREATED_DATE >= '" + date_fr + "'";
        }
        if (toriMei != null && toriMei.equals("") != true) {
        	sql = sql + " and TORI_MEI like '%" + toriMei + "%'";
		}
        sql = sql + " order by CREATED_DATE desc, MEISAI_NO) where rownum <= 50";
        
		//接続処理
		Connection conn = null;
		ArrayList<OcrDaichoBean> list = new ArrayList<OcrDaichoBean>();
		try {
			Class.forName(DB_DRIVER);
			conn = DriverManager.getConnection(DB_URL,DB_USER,DB_PASS);
			System.out.println(sql);

			PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            OcrDaichoBean daicho = new OcrDaichoBean();
    		while(rs.next()) {
    			// ユーザIDと名前をBeanクラスへセット
    			daicho.objectId = rs.getString(1);		//"OBJECT_ID"
    			daicho.createdDate = rs.getString(2);	//"CREATED_DATE"
    			daicho.ocrId = rs.getString(3);			//"OCR_ID"
    			daicho.meisaiNo = rs.getString(4);		//"MEISAI_NO"
    			daicho.toriMei = rs.getString(5);		//"TORI_MEI"
    			daicho.chumonBi = rs.getString(6);		//"CHUMON_BI"
    			daicho.sofuMei = rs.getString(7);		//"SOFU_MEI"
    			daicho.chumonBango = rs.getString(8);	//"CHUMON_BANGO"
    			daicho.hinmei = rs.getString(9);		//"HINMEI"
    			daicho.suryo = rs.getString(10);		//"SURYO"
    			daicho.seizoTanka = rs.getString(11);	//"SEIZO_TANKA"
    			daicho.kingaku = rs.getString(12);		//"KINGAKU"
    			daicho.youkyuNouki = rs.getString(13);	//"YOUKYU_NOUKI"
    			daicho.biko = rs.getString(14);			//"BIKO"
            	// リストにBeanクラスごと格納
    			list.add(daicho);
    			//Beanクラスを初期化
    			daicho = new OcrDaichoBean();
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
}
