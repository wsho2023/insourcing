package common.ocr;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.example.demo.SpringConfig;

public class OcrRirekiDAO {
	String DB_URL;
	String DB_USER;
	String DB_PASS;
	String DB_DRIVER;
	
	public OcrRirekiDAO(SpringConfig config) {
        //接続情報取得
		DB_URL = config.getDBUrl();
		DB_USER = config.getDBUsername();
		DB_PASS = config.getDBPassword();
		DB_DRIVER = config.getDBDriverClassName();
	}

	// インスタンスオブジェクトの生成->返却（コードの簡略化）
	public static OcrRirekiDAO getInstance(SpringConfig config) {
		return new OcrRirekiDAO(config);
	}

	public ArrayList<OcrRirekiBean> readDataUnit(String fields, String unitIdName, int type) {
		String sql;
		if (type == 0) {
			sql = "select " + fields + " from ocrRirekiTable r, ocrDataTable o where r.COL0=o.createdAt and r.COL1=o.UNIT_NAME and o.UNIT_ID=? order by COL2";
		} else{
			sql = "select " + fields + " from ocrRirekiTable r where r.COL1=? and r.COL2<>'0' order by COL0 Desc, COL2";
		}
		String[] cols = fields.split(",");
		System.out.println("  colLen: " + cols.length);
		
		//接続処理
		Connection conn = null;
		ArrayList<OcrRirekiBean> list = new ArrayList<OcrRirekiBean>();
		try {
			Class.forName(DB_DRIVER);
			conn = DriverManager.getConnection(DB_URL,DB_USER,DB_PASS);
			System.out.println("  unitId/Name: " + unitIdName);
			System.out.println("  sql: " + sql);

			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, unitIdName);
            ResultSet rs = ps.executeQuery();
            
            OcrRirekiBean rireki = null;
			String col = "";
			while (rs.next()) {
				//Beanクラスを初期化
				rireki = new OcrRirekiBean();
				for (int i=0; i<cols.length; i++) {
					col = rs.getString(cols[i]);
	            	rireki.setCOL(i, col);
				}
            	// リストにBeanクラスごと格納
				list.add(rireki);
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
	
	public OcrRirekiBean readHeader(String fields, String documentId) {
		String sql = "select " + fields + " from ocrRirekiTable where COL0 = ? and COL2 = '0' order by COL2";
		String[] cols = fields.split(",");
		System.out.println("  colLen: " + cols.length);
		
		//接続処理
		Connection conn = null;
		//ArrayList<OcrRirekiBean> list = new ArrayList<OcrRirekiBean>();
        OcrRirekiBean rireki = new OcrRirekiBean();
		try {
			Class.forName(DB_DRIVER);
			conn = DriverManager.getConnection(DB_URL,DB_USER,DB_PASS);
			System.out.println("  documentId: " + documentId);
			System.out.println("  sql: " + sql);

			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, documentId);
            ResultSet rs = ps.executeQuery();

			String col = "";
			boolean headerFlag = false;
			while (rs.next()) {
				headerFlag = true;
				for (int i=0; i<cols.length; i++) {
					col = rs.getString(cols[i]);
	            	rireki.setCOL(i, col);
				}
			}
			//ヘッダなし ⇒ cols[i] を入れる。
			if (headerFlag == false) {
				for(int i=0; i<cols.length; i++) {
	            	rireki.setCOL(i, cols[i]);
				}
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
		return rireki;
	}

	public void deleteRirekiData(String createdAt, String formName) {
        String sql = "delete from ocrRirekiTable where COL0=? and COL1=?";
	
		//接続処理
		Connection conn = null;
		try {
			Class.forName(DB_DRIVER);
			conn = DriverManager.getConnection(this.DB_URL, this.DB_USER, this.DB_PASS);
			System.out.println("  createdAt: " + createdAt + "  formName: " + formName);
			System.out.println("  sql: " + sql);
			conn.setAutoCommit(false);
	       
	       PreparedStatement ps = conn.prepareStatement(sql);
	       int i=1;
	       ps.setString(i++, createdAt);
	       ps.setString(i++, formName);
	       
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
	
	public void insertRirekiData(String fields, String values) {
        String sql = "insert into ocrRirekiTable(" + fields + ") values(" + values + ")";
    	
		//接続処理
		Connection conn = null;
		try {
			Class.forName(DB_DRIVER);
			conn = DriverManager.getConnection(this.DB_URL, this.DB_USER, this.DB_PASS);
			System.out.println("  sql: " + sql);
			conn.setAutoCommit(false);
	       
			PreparedStatement ps = conn.prepareStatement(sql);
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

	public int queryCountRirekiHinmei(String formName, int colHinmei, String hinmei) {
		int count = -1;
		String sql = "select count(*) from ocrRirekiTable where COL1=? and COL" + (colHinmei+3) + "=?";	//COLの時は、+3する。
		
		//接続処理
		Connection conn = null;
		try {
			Class.forName(DB_DRIVER);
			conn = DriverManager.getConnection(this.DB_URL, this.DB_USER, this.DB_PASS);
			System.out.println("  formName: " + formName + "  hinmei: " + hinmei);
			System.out.println("  sql: " + sql);

			PreparedStatement ps = conn.prepareStatement(sql);
			int i=1;
			ps.setString(i++, formName);
			ps.setString(i++, hinmei);

            ResultSet rs = ps.executeQuery();
            
            //1レコードなので、listなし
            rs.next();
            count = rs.getInt("COUNT(*)");
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
		return count;
	}

	public int queryCountRirekiChuban(String createdAt, String formName, String colChuban) {
		int count = -1;
		String sql = "select count(distinct COL0) from ocrRirekiTable where COL1='" + formName + "' and "
				   + colChuban + " IN (select " + colChuban + " from ocrRirekiTable where COL1='" + formName + "' and COL0='" + createdAt + "')";
		
		//接続処理
		Connection conn = null;
		try {
			Class.forName(DB_DRIVER);
			conn = DriverManager.getConnection(this.DB_URL, this.DB_USER, this.DB_PASS);
			System.out.println("  createdAt: " + createdAt + "  formName: " + formName);
			System.out.println("  sql: " + sql);

			PreparedStatement ps = conn.prepareStatement(sql);
			//int i=1;
			//ps.setString(i++, createdAt);
			//ps.setString(i++, formName);
			//ps.setString(i++, formName);
			//ps.setString(i++, chuban);
            
			ResultSet rs = ps.executeQuery();
            
            //1レコードなので、listなし
            rs.next();
            count = rs.getInt("COUNT(DISTINCTCOL0)");
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
		return count;
	}
	
    public void insertDaichofromRirekiData(String colOutput, String createdAt, String formName, String no) {
		String sql = "insert into DAICHO_TABLE(OBJECT_ID,CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE,TORI_MEI,CHUMON_BI,SOFU_MEI,CHUMON_BANGO,HINMEI,SURYO,SEIZO_TANKA,KINGAKU,YOUKYU_NOUKI,BIKO,OCR_ID,MEISAI_NO) "
				   + " select SEQ_DAICHO.nextval,'AI-OCR',TO_DATE(COL0, 'YYYY/MM/DD HH24:MI:SS'),'AI-OCR',TO_DATE(COL0, 'YYYY/MM/DD HH24:MI:SS')," + colOutput + ",d.UNIT_ID,COL2 "
				   + " from ocrRirekiTable r, ocrDataTable d where COL0=? and COL1=? and COL2=? and COL0=d.CREATEDAT and COL1=d.UNIT_NAME";
    	
		//接続処理
		Connection conn = null;
		try {
			Class.forName(DB_DRIVER);
			conn = DriverManager.getConnection(this.DB_URL, this.DB_USER, this.DB_PASS);
			System.out.println("  createdAt: " + createdAt + "  formName: " + formName + "  no: " + no);
			System.out.println("  sql: " + sql);
			conn.setAutoCommit(false);
	       
			PreparedStatement ps = conn.prepareStatement(sql);
			int i=1;
			ps.setString(i++, createdAt);
			ps.setString(i++, formName);
			ps.setString(i++, no);
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
