package common.po;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.example.demo.InsourcingConfig;

public class PoUploadDAO {
	//DB接続情報
	String DB_URL;
	String DB_USER;
	String DB_PASS;
	String DB_DRIVER;

	public PoUploadDAO(InsourcingConfig config) {
        //接続情報取得
		DB_URL = config.getDBUrl();
		DB_USER = config.getDBUsername();
		DB_PASS = config.getDBPassword();
		DB_DRIVER = config.getDBDriverClassName();
	}

	// インスタンスオブジェクトの生成->返却（コードの簡略化）
	public static PoUploadDAO getInstance(InsourcingConfig config) {
		return new PoUploadDAO(config);
	}
	
	public ArrayList<PoUploadBean> read(String userId) {
		String sql = "select * from POUPLOADTABLE where LOGIN_ID = ?";
		
		//接続処理
		Connection conn = null;
		ArrayList<PoUploadBean> list = new ArrayList<PoUploadBean>();
		try {
			Class.forName(DB_DRIVER);
			conn = DriverManager.getConnection(DB_URL,DB_USER,DB_PASS);
			System.out.println(sql);

			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            
            PoUploadBean upload = null;
			while(rs.next()) {
	            upload = new PoUploadBean();
				upload.setUserId(rs.getString(1));		//"LOGIN_ID"
				upload.setUserName(rs.getString(2));	//"USER_NAME"
				upload.setDatetime(rs.getString(3));	//"CREATED_DATE"
				upload.setToriCd(rs.getString(4));		//"TORIHIKISAKI_CD"
				upload.setUploadPath(rs.getString(5));	//"UPLOAD_PATH"
				upload.setCode(rs.getString(6));		//"CODE"
				upload.setInputPath(rs.getString(7));	//"INPUT_PATH"
            	// リストにBeanクラスごと格納
				list.add(upload);
			}
			return list;
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
		
		return null;
	}

	public PoUploadBean quertyWithUploadPath(String uploadPath) {
		String sql = "select * from POUPLOADTABLE where UPLOAD_PATH = ?";
		
		//接続処理
		Connection conn = null;
		//ArrayList<PoUploadBean> list = new ArrayList<PoUploadBean>();
		try {
			Class.forName(DB_DRIVER);
			conn = DriverManager.getConnection(DB_URL,DB_USER,DB_PASS);
			System.out.println("  uploadPath: " + uploadPath);
			System.out.println("  sql: " + sql);

			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, uploadPath);
            ResultSet rs = ps.executeQuery();
            
            PoUploadBean upload = new PoUploadBean();
			while (rs.next()) {
    			//Beanクラスへセット(getString:1始まり)
				upload.setUserId(rs.getString(1));		//LOGIN_ID
				upload.setUserName(rs.getString(2));	//USER_NAME
				upload.setDatetime(rs.getString(3));	//CREATED_DATE
				upload.setToriCd(rs.getString(4));		//TORIHIKISAKI_CD
				upload.setUploadPath(rs.getString(5));	//UPLOAD_PATH
				upload.setCode(rs.getString(6));		//CODE
				upload.setInputPath(rs.getString(7));	//INPUT_PATH
            	// リストにBeanクラスごと格納
				//list.add(upload);
				//Beanクラスを初期化
				//upload = new PoUploadBean();
			}
			return upload;
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
		
		return null;
	}

	public void registData_new(PoUploadBean poUpload) {
		
		int count = query_count(poUpload.getUploadPath());
		if (count == 0) {
			insert(poUpload);
		} else {
			update(poUpload);
		}
	}

	public void registData(PoUploadBean poUpload) {
		
		int count = query_count(poUpload.getUploadPath());
		if (count == 0) {
			insert(poUpload);
		} else {
			update(poUpload);
		}
	}

	private int query_count(String uploadPath) {
		int count = -1;
		String sql = "select count(*) from POUPLOADTABLE where UPLOAD_PATH=?";
		
		//接続処理
		Connection conn = null;
		try {
			Class.forName(DB_DRIVER);
			conn = DriverManager.getConnection(DB_URL,DB_USER,DB_PASS);
			System.out.println("  UploadPath: " + uploadPath);
			System.out.println("  sql: " + sql);

			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, uploadPath);
            ResultSet rs = ps.executeQuery();
            
            //1レコードなので、listなし
            rs.next();
            count = rs.getInt("COUNT(*)");
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
		
		return count;
	}

	private void insert(PoUploadBean poUpload) {
		String sql = "INSERT INTO POUPLOADTABLE(LOGIN_ID,USER_NAME,CREATED_DATE,TORIHIKISAKI_CD,UPLOAD_PATH,CODE,INPUT_PATH) " + 
					 "VALUES(?,?,TO_DATE(?,'YYYY/MM/DD HH24:MI:SS'),?,?,?,?)";
		
		//接続処理
		Connection conn = null;
        try {
			Class.forName(DB_DRIVER);
			conn = DriverManager.getConnection(DB_URL,DB_USER,DB_PASS);
			System.out.println(sql);
            conn.setAutoCommit(false);
            
            PreparedStatement ps = conn.prepareStatement(sql);
            int i=1;
            ps.setString(i++, poUpload.getUserId());
            ps.setString(i++, poUpload.getUserName());
            ps.setString(i++, poUpload.getDatetime());
            ps.setString(i++, poUpload.getToricd());
            ps.setString(i++, poUpload.getUploadPath());
            ps.setString(i++, poUpload.getCode());
            ps.setString(i++, poUpload.getInputPath());
            
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

	private void update(PoUploadBean poUpload) {
		String sql = "UPDATE POUPLOADTABLE SET LOGIN_ID=?,USER_NAME=?,CREATED_DATE=TO_DATE(?,'YYYY/MM/DD HH:MM:SS'),TORIHIKISAKI_CD=?,CODE=?,INPUT_PATH=? " + 
				 	 "WHERE UPLOAD_PATH=?";
		
		//接続処理
		Connection conn = null;
		try {
			Class.forName(DB_DRIVER);
			conn = DriverManager.getConnection(DB_URL,DB_USER,DB_PASS);
			System.out.println(sql);
			conn.setAutoCommit(false);
			
			PreparedStatement ps = conn.prepareStatement(sql);
			int i=1;
            ps.setString(i++, poUpload.getUserId());
            ps.setString(i++, poUpload.getUserName());
            ps.setString(i++, poUpload.getDatetime());
            ps.setString(i++, poUpload.getToricd());
            ps.setString(i++, poUpload.getCode());
            ps.setString(i++, poUpload.getInputPath());
            ps.setString(i++, poUpload.getUploadPath());
			
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
