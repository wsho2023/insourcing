package common.ocr;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.example.demo.SpringConfig;

public class OcrErrlDAO {
	String DB_URL;
	String DB_USER;
	String DB_PASS;
	String DB_DRIVER;
	
	public OcrErrlDAO(SpringConfig config) {
        //接続情報取得
		DB_URL = config.getDBUrl();
		DB_USER = config.getDBUsername();
		DB_PASS = config.getDBPassword();
		DB_DRIVER = config.getDBDriverClassName();
	}

	// インスタンスオブジェクトの生成->返却（コードの簡略化）
	public static OcrErrlDAO getInstance(SpringConfig config) {
		return new OcrErrlDAO(config);
	}
    
    public void insertData(OcrErrlBean errl) {
		String sql = "insert into OcrErrlTable values(?,?,?,?,?,?)";
    	
		//接続処理
		Connection conn = null;
		try {
			Class.forName(DB_DRIVER);
			conn = DriverManager.getConnection(this.DB_URL, this.DB_USER, this.DB_PASS);
			//System.out.println("  createdAt: " + createdAt + "  formName: " + formName + "  no: " + no);
			System.out.println("  sql: " + sql);
			conn.setAutoCommit(false);
	       
			PreparedStatement ps = conn.prepareStatement(sql);
			int i=1;
			//ps.setString(i++, createdAt);
			//ps.setString(i++, formName);
			//ps.setString(i++, no);
			//ps.setString(i++, no);
			//ps.setString(i++, no);
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
