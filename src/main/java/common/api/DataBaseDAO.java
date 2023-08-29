package common.api;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import com.example.demo.ApiConfig;

public class DataBaseDAO {
	String DB_URL;
	String DB_USER;
	String DB_PASS;
	String DB_DRIVER;
	
	ArrayList<String> header;
	
	public DataBaseDAO(ApiConfig config) {
        //接続情報取得
		DB_URL = config.getDBUrl();
		DB_USER = config.getDBUsername();
		DB_PASS = config.getDBPassword();
		DB_DRIVER = config.getDBDriverClassName();
	}

	// インスタンスオブジェクトの生成->返却（コードの簡略化）
	public static DataBaseDAO getInstance(ApiConfig config) {
		return new DataBaseDAO(config);
	}
	
	public String getHeaderTsv() {
		String retStr = "";

		for (int c=0; c<header.size()-1; c++) {
			retStr = retStr + header.get(c) + "\t";
		}
		retStr = retStr + header.get(header.size()-1) + "\r\n";

		return retStr;
	}
	
	public ArrayList<ArrayList<String>> getData(String sql) throws SQLException {
		//接続処理
		Connection conn = null;
		ArrayList<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
		try {
			Class.forName(DB_DRIVER);
			conn = DriverManager.getConnection(DB_URL,DB_USER,DB_PASS);
			System.out.println(sql);	//for debug

			PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            //ヘッダ情報取得
    		ResultSetMetaData meta = rs.getMetaData();
    		int colNum = meta.getColumnCount();
    		System.out.println("カラム数：" + colNum);
    		
    		//ヘッダ格納
    		header = new ArrayList<String>();
    		System.out.print("ヘッダ: ");
    		for (int h=0; h<colNum; h++) {
    			header.add(meta.getColumnName(h+1));//1始まり
        		System.out.print(header.get(h) + " ");
    		}
    		System.out.println();		//改行
    		list.add(header);
            
    		//データ格納
			ArrayList<String> data = null;
			String tmp;
    		while (rs.next()) {
    			data = new ArrayList<String>();
				for (int d=0; d<colNum; d++) {
					tmp = rs.getString(d+1);	//1始まり
					if (tmp == null)			//nullを表示しない
						data.add("");
					else
						data.add(tmp);
				}
    			list.add(data);
    		}
		//} catch(SQLException e) {
		//	// エラーハンドリング
		//	System.out.println("sql実行失敗");
		//	e.printStackTrace();
		//	
		} catch(ClassNotFoundException e) {
			// エラーハンドリング
			System.out.println("JDBCドライバ関連エラー");
			e.printStackTrace();
		} finally {
			// DB接続を解除
			if (conn != null) {
				conn.close();
			}
		}
		
		// リストを返す
		return list;
	}
	
	//https://confrage.jp/javaからプロシージャを呼び出す方法/
	public void update() throws SQLException {
        String sql = "CALL package.update";
    	
		//接続処理
		Connection conn = null;
		try {
			Class.forName(DB_DRIVER);
			conn = DriverManager.getConnection(this.DB_URL, this.DB_USER, this.DB_PASS);
			System.out.println("  sql: " + sql);
			conn.setAutoCommit(false);
			
			CallableStatement cs = conn.prepareCall(sql);
			// OUTパラメータ
			//cs.registerOutParameter(4, Types.INTEGER);  
			//cs.registerOutParameter(5, Types.VARCHAR);
			//cs.registerOutParameter(6, Types.ARRAY, "INFOARRAY");
			// INパラメータ
			//cs.setInt(1, 100);
			//cs.setString(2, "2");
			// プロシージャを実行する	
			cs.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
	   	} finally {
			// DB接続を解除
			if (conn != null) {
				conn.close();
			}
	   	}
	}
}
