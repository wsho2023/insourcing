package common.fax;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.example.demo.SpringConfig;

public class FaxDataDAO {
	String DB_URL;
	String DB_USER;
	String DB_PASS;
	String DB_DRIVER;
	
	public FaxDataDAO(SpringConfig config) {
        //接続情報取得
		DB_URL = config.getDBUrl();
		DB_USER = config.getDBUsername();
		DB_PASS = config.getDBPassword();
		DB_DRIVER = config.getDBDriverClassName();
	}

	// インスタンスオブジェクトの生成->返却（コードの簡略化）
	public static FaxDataDAO getInstance(SpringConfig config) {
		return new FaxDataDAO(config);
	}
	
	// 検索処理
	// 戻り値		：ArrayList<Beanクラス>
	public ArrayList<FaxDataBean> read(String formName, String date_fr, String date_to, String soushinMotoNashi, String kyoten) {
        String sql = "select NO,FAX_NO,CDATE,CTIME,JOB_NO,FILE_NAME,SOUSHN_MOTO,FULLPATH,UNIT_ID from "
     			   + "(select f.NO,f.FAX_NO,f.CDATE,f.CTIME,f.JOB_NO,f.FILE_NAME,f.SOUSHN_MOTO,f.FULLPATH,o.UNIT_ID from faxDataTable f left outer join ocrDataTable o on f.FULLPATH=o.UPLOAD_PATH where f.KYOTEN='" + kyoten + "'";
        if (date_fr != null && date_fr.equals("") != true) {
        	date_fr = date_fr.replace("-", "/");	//(yyyy-MM-dd) → (yyyy/MM/dd)
        	sql = sql + " and f.CDATE >= '" + date_fr + "'";
        }
        if (soushinMotoNashi == null) soushinMotoNashi = "false"; 
        if (formName != null && formName.equals("") != true && soushinMotoNashi.equals("true") == false) {
        	sql = sql + " and f.SOUSHN_MOTO like '%" + formName + "%'";
        } else if (soushinMotoNashi != null && soushinMotoNashi.equals("true") == true) {
			sql = sql + " and f.FULLPATH like '%送信元なし%'";
		}
        sql = sql + " order by NO DESC) where rownum <= 30";
        
		//接続処理
		Connection conn = null;
		ArrayList<FaxDataBean> fax_dao = new ArrayList<FaxDataBean>();
		try {
			Class.forName(this.DB_DRIVER);
			conn = DriverManager.getConnection(DB_URL,DB_USER,DB_PASS);
			System.out.println(sql);

			PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            FaxDataBean fax = null;
			while(rs.next()) {
				//Beanクラスを初期化
				fax = new FaxDataBean();
				// ユーザIDと名前をBeanクラスへセット
				fax.setNo(rs.getInt(1));	//"NO"	
				fax.setFaxNo(rs.getString(2));	//"FAX_NO"
				fax.setCDate(rs.getString(3));	//"CDATE"
				fax.setCTime(rs.getString(4));	//"CTIME"
				fax.setJobNo(rs.getString(5));	//"JOB_NO"
				fax.setFileName(rs.getString(6));	//"FILE_NAME"
				fax.setSoshinMoto(rs.getString(7));	//"SOUSHN_MOTO"
				fax.setFullPath(rs.getString(8));	//"FULLPATH"
				fax.setUnitId(rs.getString(9));		//"UNIT_ID"
            	// リストにBeanクラスごと格納
				fax_dao.add(fax);
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
		return fax_dao;
	}
	
	public void insertDB(FaxDataBean fax) {
		String sql = "INSERT INTO FAXDATATABLE VALUES(SEQ_FAX_NO.nextval,?,?,?,?,?,?,?,?)";
		
		//接続処理
		Connection conn = null;
        try {
			Class.forName(this.DB_DRIVER);
			conn = DriverManager.getConnection(this.DB_URL, this.DB_USER, this.DB_PASS);
			System.out.println("  faxNo: " + fax.faxNo + "  cDate: " + fax.cDate + "  cTime: " + fax.cTime + "  jobNo: " + fax.jobNo + "  fileName: " + fax.fileName
							 + "  soshinMoto: " + fax.soshinMoto + "  fullPath: " + fax.fullPath + "  soshinMoto: " + fax.soshinMoto + "  kyoten: " + fax.kyoten);
			System.out.println("  sql: " + sql);
            conn.setAutoCommit(false);
            
            PreparedStatement ps = conn.prepareStatement(sql);
            int i=1;
            //ps.setString(i++, this.no);
            ps.setString(i++, fax.faxNo);
            ps.setString(i++, fax.cDate);
            ps.setString(i++, fax.cTime);
            ps.setString(i++, fax.jobNo);
            ps.setString(i++, fax.fileName);
            ps.setString(i++, fax.soshinMoto);
            ps.setString(i++, fax.fullPath);
            ps.setString(i++, fax.kyoten);
            
            ps.executeUpdate();
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
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
	
	public void updateSoshinmotoWithUploadFile(String soshinMoto, String uploadFilePath) {
		String sql = "UPDATE FAXDATATABLE SET SOUSHN_MOTO=? WHERE FULLPATH=?";
	
		//接続処理
		Connection conn = null;
		try {
			Class.forName(this.DB_DRIVER);
			conn = DriverManager.getConnection(this.DB_URL, this.DB_USER, this.DB_PASS);
			System.out.println("  soshinMoto: " + soshinMoto + "  uploadFilePath: " + uploadFilePath);
			System.out.println("  sql: " + sql);
			conn.setAutoCommit(false);
	       
			PreparedStatement ps = conn.prepareStatement(sql);
			int i=1;
			ps.setString(i++, soshinMoto);
			ps.setString(i++, uploadFilePath);
			
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

	public void updateUploadFilePath(String before, String after, String updateFilePath) {
		String sql = "UPDATE FAXDATATABLE SET FULLPATH=REPLACE(FULLPATH, ?, ?) "
				   + "WHERE FULLPATH=?";
		
		//接続処理
		Connection conn = null;
		try {
			Class.forName(this.DB_DRIVER);
			conn = DriverManager.getConnection(this.DB_URL, this.DB_USER, this.DB_PASS);
			System.out.println("  uploadFile: " + updateFilePath);
			System.out.println("  before: " + before + "--> after: " + after);
			System.out.println("  sql: " + sql);
			conn.setAutoCommit(false);
	       
			PreparedStatement ps = conn.prepareStatement(sql);
			int i=1;
			ps.setString(i++, before);
			ps.setString(i++, after);
			ps.setString(i++, updateFilePath);
			
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
