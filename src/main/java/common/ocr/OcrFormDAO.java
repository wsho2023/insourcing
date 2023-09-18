package common.ocr;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.demo.SpringConfig;

public class OcrFormDAO {
	String DB_URL;
	String DB_USER;
	String DB_PASS;
	String DB_DRIVER;
	
	public OcrFormDAO(SpringConfig config) {
        //接続情報取得
		DB_URL = config.getDBUrl();
		DB_USER = config.getDBUsername();
		DB_PASS = config.getDBPassword();
		DB_DRIVER = config.getDBDriverClassName();
	}

	// インスタンスオブジェクトの生成->返却（コードの簡略化）
	public static OcrFormDAO getInstance(SpringConfig config) {
		return new OcrFormDAO(config);
	}

	public OcrFormBean queryOcrFormInfoDocumentId(String documentId) {
		
		if (documentId.equals("") || documentId == null) {
			return null;
		}
		String sql = "select NAME,DOCUMENT_ID,DOCUMENT_NAME,DOCSET_NAME from OCRFORMTABLE where DOCUMENT_ID=?";
		
		//接続処理
		Connection conn = null;
        OcrFormBean form = null;
		try {
			Class.forName(DB_DRIVER);
			conn = DriverManager.getConnection(DB_URL,DB_USER,DB_PASS);
			System.out.println(sql);

			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, documentId);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
            	form = new OcrFormBean();
				form.setFormName(rs.getString(1));		//"NAME"
				form.setDocumentId(rs.getString(2));	//"DOCUMENT_ID"
				form.setDocumentName(rs.getString(3));	//"DOCUMENT_NAME"
				form.setDocsetName(rs.getString(4));	//"DOCSET_NAME"
			}			
		} catch(SQLException sql_e) {
			// エラーハンドリング
			System.out.println("sql実行失敗");
			sql_e.printStackTrace();
			
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
		return form;
	}
	
	public OcrFormBean queryOcrFormImportPath(int sortFlag, String importPath, String docsetName) {
		String sql = "select NAME,DOCUMENT_ID,DOCUMENT_NAME,DOCSET_NAME from ocrFormTable where IMPORT_PATH=? and DOCSET_NAME=? ";
		if (sortFlag != 0) {
			sql = sql + " and DOCUMENT_NAME IS NULL";
		}

		//接続処理
		Connection conn = null;
        OcrFormBean form = new OcrFormBean();
		try {
			Class.forName(DB_DRIVER);
			conn = DriverManager.getConnection(this.DB_URL, this.DB_USER, this.DB_PASS);
			System.out.println("  sortFlag: " + sortFlag + "  importPath: " + importPath + "  docsetName: " + docsetName);
			System.out.println("  sql: " + sql);

			PreparedStatement ps = conn.prepareStatement(sql);
			int i=1;
			ps.setString(i++, importPath);
			ps.setString(i++, docsetName);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
				form.setFormName(rs.getString(1));		//"NAME"
				form.setDocumentId(rs.getString(2));	//"DOCUMENT_ID"
				form.setDocumentName(rs.getString(3));	//"DOCUMENT_NAME"
				form.setDocsetName(rs.getString(4));	//"DOCSET_NAME"
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
		
		return form;
	}
}
