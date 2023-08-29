package common.api;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;

import com.example.demo.ApiConfig;

import common.utils.MyExcel;
import common.utils.MyFiles;
import common.utils.MyUtils;
import jakarta.servlet.http.HttpServletResponse;

public class ShukeiObjInfo {
	ApiConfig config;
	String sys;
	String sysName;
	String obj;
	String objName;
	String[][] colFormat;
	ArrayList<ArrayList<String>> list = null;
	String templePath;
	String outputPath;
    String saveXlsPath;
    SendMail sendMail;
	ArrayList<String> objList;
	ArrayList<ArrayList<ArrayList<String>>> listList;	//ArrayList<ArrayList<String>> x n個
	ArrayList<String[][]> colFmtList;
	
	public ShukeiObjInfo(ApiConfig argConfig, String argSys, String argObj) {
		config = argConfig;
        sys = argSys;
        sysName = null;
		obj = argObj;
		objName = null;
		colFormat = null;
		System.out.println("/" + sys + " obj: " + obj);
		sendMail = new SendMail(config);
	}
	
	public String makeObject() {
        sysName = "システム";
        if (obj.equals("jisseki") == true) {
			//curl -X POST "http://localhost:8080/shukei?obj=jisseki"
			objName = obj;
			objList = new ArrayList<String>();
			objList.add("jisseki1");
			objList.add("jisseki2");
	        String[][] format1 = {
        		{"1", "DATETIME"},
        		{"5", "DATE"},
        		{"9", "SURYO"},
        		{"10", "TANKA"},
        		{"11", "KINGAKU"},
        		{"12", "DATE"},
	        };
	        String[][] format2 = {
        		{"1", "DATETIME"},
        		{"5", "DATE"},
        		{"9", "SURYO"},
        		{"10", "TANKA"},
        		{"11", "KINGAKU"},
        		{"12", "DATE"},
	        };
			//colFormat = new String[format.length][];
	        //colFormat = format;
			colFmtList = new ArrayList<String[][]>();
			colFmtList.add(format1);
			colFmtList.add(format2);
        } else if (obj.equals("meisai") == true) {
			//curl -X POST "http://localhost:8080/shukei?obj=meisai"
			objName = obj;
			objList = new ArrayList<String>();
			objList.add(obj);
	        String[][] format = {
        		{"1", "DATETIME"},
        		{"5", "DATE"},
        		{"9", "SURYO"},
        		{"10", "TANKA"},
        		{"11", "KINGAKU"},
        		{"12", "DATE"},
	        };
			//colFormat = new String[format.length][];
	        //colFormat = format;
			colFmtList = new ArrayList<String[][]>();
			colFmtList.add(format);
        } else if (obj.equals("tonyu") == true) {
			//curl -X POST "http://localhost:8080/shukei?obj=tonyu"
			objName = "tonyu";
			objList = new ArrayList<String>();
			objList.add("shinki");
			objList.add("nouki1");
			objList.add("henkou");
			objList.add("nouki2");
	        String[][] format = {
        		{"1", "DATETIME"},
        		{"5", "DATE"},
        		{"9", "SURYO"},
        		{"10", "TANKA"},
        		{"11", "KINGAKU"},
        		{"12", "DATE"},
	        };
			//colFormat = new String[format.length][];
			//colFormat = format;
			colFmtList = new ArrayList<String[][]>();
			colFmtList.add(format);
			colFmtList.add(format);
			colFmtList.add(format);
			colFmtList.add(format);
        } 
		if (objName == null)
			return "対象Object処理なし";
		
		return null;
	}
	
	public String execute() {
		String msg;
    	listList = new ArrayList<ArrayList<ArrayList<String>>>();
		for (String obj: objList) {
			msg = getData(obj);	//データ取得
			if (msg != null) return msg;
			listList.add(list);
			list = null;
		}
		
		msg = makeExcel();			//Excelに書き出し
		if (msg != null) return msg;
		
		msg = sendMail();			//メール添付送信
		if (msg != null) return msg;
		
		return null;
	}
	
	public String download(HttpServletResponse response) {
		String msg;
    	listList = new ArrayList<ArrayList<ArrayList<String>>>();
		for (String obj: objList) {
			msg = getData(obj);	//データ取得
			if (msg != null) return msg;
			listList.add(list);
			list = null;
		}
		
		msg = makeExcel();			//Excelに書き出し
		if (msg != null) return msg;
		
        try (OutputStream os = response.getOutputStream();) {
        	String fileName = MyFiles.getFileName(saveXlsPath);
            byte[] fb = MyFiles.readAllBytes(saveXlsPath);
            
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            response.setContentLength(fb.length);
            os.write(fb);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		return null;
	}
	
	//---------------------------------------
	//データ取得
	//---------------------------------------
	public String getData(String obj) {
		//---------------------------------------
		//SQL取得
		//---------------------------------------
		String sqlPath =  config.getPathTempletePath() + obj + ".sql";
		String sql = "";
		try {
			sql = MyFiles.readAllText(sqlPath);
		} catch (IOException e) {
			e.printStackTrace();
        	return e.toString();
		}
		
		//---------------------------------------
		//データ取得
		//---------------------------------------
		DataBaseDAO dao = new DataBaseDAO(config);
		try {
			list = dao.getData(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			return e.toString();
		}
		if (list.size() < 2) {
			MyUtils.SystemErrPrint("抽出データなし");
		}
		
		//---------------------------------------
		//TSVファイル読み込み
		//---------------------------------------
		/*outputPath = config.getPathOutputPath();
		String saveTxtPath = outputPath + obj + ".tsv";
        list = null;
		try {
			list = MyFiles.parseTSV(saveTxtPath, "UTF-8");	//or "SJIS"
		} catch (IOException e) {
			e.printStackTrace();
        	return e.toString();
		}
		if (list == null) {
			MyUtils.SystemErrPrint("抽出データなし");
		}*/
		
		return null;
	}
	
	//---------------------------------------
	//Excelに書き出し
	//---------------------------------------
	public String makeExcel() {
		templePath = config.getPathTempletePath();
		outputPath = config.getPathOutputPath();
        String defXlsPath = templePath + objName + "_org.xlsx";
        String tmpXlsPath = null;
		if (MyFiles.exists(defXlsPath) != true) {
			tmpXlsPath = null;	//新規作成
			MyUtils.SystemLogPrint("  Excel新規オープン... 種別: " + objName);
		} else {
	        tmpXlsPath = templePath + objName + "_tmp.xlsx";
			try {
				MyFiles.copyOW(defXlsPath, tmpXlsPath);	//上書き
			} catch (IOException e) {
				e.printStackTrace();
				return e.getMessage();
			}
			MyUtils.SystemLogPrint("  Excelオープン...: " + tmpXlsPath + " 種別: " + objName);
		}
		
		MyExcel xlsx = new MyExcel();
		try {
			//Excelファイルオープン(tmpXlsPath=nullなら、新規作成)
			xlsx.open(tmpXlsPath, objName);
			//データ転記、データ転記した範囲をテーブル化
			int i = 0;
			for (ArrayList<ArrayList<String>> list: listList) {
				xlsx.setColFormat(colFmtList.get(i));
				xlsx.writeData(objList.get(i), list, true);
				i++;
			}
			xlsx.refreshPivot("pivot");
			//Excelファイル保存
			//saveXlsPath = outputPath + objName + "_" + MyUtils.getDateStr() +".xlsx";
			saveXlsPath = outputPath + objName + ".xlsx";
			MyUtils.SystemLogPrint("  XLSXファイル保存: " + saveXlsPath);
			xlsx.save(saveXlsPath);
			xlsx.close();
		} catch (IOException e) {
			e.printStackTrace();
			return e.toString();
		}
		
		return null;
	}
	
	//---------------------------------------
	//メール添付送信        
	//---------------------------------------
	public String sendMail() {
		String mailBody = "";
		String subject = "[" + sysName + "]連絡(" + objName + " " + MyUtils.getDate() + ")";
		sendMail.execute(subject, mailBody, saveXlsPath);
		
		return null;
	}
}
