package common.po;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.example.demo.InsourcingConfig;

public class PoFormDAO {
	//DB接続情報
	String DB_URL;
	String DB_USER;
	String DB_PASS;
	String DB_DRIVER;
		
	public PoFormDAO(InsourcingConfig config) {
        //接続情報取得
		DB_URL = config.getDBUrl();
		DB_USER = config.getDBUsername();
		DB_PASS = config.getDBPassword();
		DB_DRIVER = config.getDBDriverClassName();
	}

	// インスタンスオブジェクトの生成->返却（コードの簡略化）
	public static PoFormDAO getInstance(InsourcingConfig config) {
		return new PoFormDAO(config);
	}
	
	// 検索処理
	// 戻り値		：ArrayList<Beanクラス>
	public ArrayList<PoFormBean> read(String userId) {
        String sql = "select * from POFORMTABLE where MEMBER like '%" + userId + "%' order by CODE";
		
		//接続処理
		Connection conn = null;
		ArrayList<PoFormBean> list = new ArrayList<PoFormBean>();
		try {
			Class.forName(DB_DRIVER);
			conn = DriverManager.getConnection(DB_URL,DB_USER,DB_PASS);
			System.out.println(sql);

			PreparedStatement ps = conn.prepareStatement(sql);
			//ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();

            PoFormBean poForm = null;
			while(rs.next()) {
				//Beanクラスを初期化
				poForm = new PoFormBean();
				// ユーザIDと名前をBeanクラスへセット
				poForm.setNo(rs.getInt(1));			//"NO"
				poForm.setCode(rs.getString(2));	//"CODE"
				poForm.setFormName(rs.getString(3));//"FORM_NAME"
				poForm.setFormId(rs.getString(4));	//"FORM_ID"
				poForm.setMember(rs.getString(5));	//"MEMBER"
				list.add(poForm);
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
