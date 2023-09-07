package common.api;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import com.example.demo.ApiConfig;

import common.utils.MyExcel;
import common.utils.MyFiles;
import common.utils.MyUtils;
import common.utils.WebApi;
import jakarta.servlet.http.HttpServletResponse;

public class ApiObjInfo {
	final String serv1 = "kkk";
	final String serv2 = "hantei";
	final String serv3 = "kyaku";
	
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
	String url;
	int dlFlag;
	boolean attachFlag;
	
	public ApiObjInfo(ApiConfig argConfig, String argSys, String argObj) {
		config = argConfig;
        sys = argSys;
        sysName = null;
		obj = argObj;
		objName = null;
		colFormat = null;
		System.out.println(sys + " obj: " + obj);
		sendMail = new SendMail(config);
	}
	
	public String makeObject() {
		String beseUrl = "";
		String fields = "";
		String filter = "";
		String sort = "";
		dlFlag = 0;	//ファイル出力
		attachFlag = true;	//メールにファイル添付
		if (sysName.equals(serv1) == true) {
			sysName = "kkk";
			dlFlag = 0;	//ファイル出力
	        if (obj.equals("juchzn") == true) {
				//curl -X POST "http://localhost:8080/kkk?obj=juchzn"
				String today = MyUtils.getToday();
				beseUrl = "http://localhost/api/juchzn?";
				filter = "date='" + today + "'";
				sort = "";
				objName = "juchzn";
	        } else if (obj.equals("seisan") == true) {
				//curl -X POST "http://localhost:8080/kkk?obj=seisan"
				String today = MyUtils.getToday();
				beseUrl = "http://localhost/api/seisan?";
				filter = "date='" + today + "'";
				sort = "";
				objName = "seisan";
	        } else if (obj.equals("uriage") == true) {
				//curl -X POST "http://localhost:8080/kkk?obj=uriage"
				String kinou = MyUtils.getToday(-1);	//前日
				beseUrl = "http://localhost/api/uriage?";
				filter = "date='" + kinou + "'";
				sort = "";
				objName = "uriage";
	        } else if (obj.equals("kaigai") == true) {
				//curl -X POST "http://localhost:8080/kkk?obj=kaigai"
				String today = MyUtils.getToday();
				today = today.replace("/", "");	//YYYYMMDDへ変換
				beseUrl = "http://localhost/api/kaigai?";
				filter = "date='" + today + "'";
				sort = "";
				objName = "kaigai";
	        } else if (obj.equals("daicho") == true) {
	        	//curl -X POST "http://localhost:8080/kkk?obj=daicho"
	        	beseUrl = "http://localhost:8090/daicho";
				objName = obj;
				String[][] format = {
					{"1", "DATETIME"},
					{"5", "DATE"},
					{"9", "SURYO"},
					{"10", "TANKA"},
					{"11", "KINGAKU"},
					{"12", "DATE"},
				};
				colFormat = new String[format.length][];
				colFormat = format;
	        } else if (obj.equals("new") == true) {
	        	//curl -X POST "http://localhost:8080/kkk?obj=new"
	        	beseUrl = "http://localhost/api/new?";
				objName = obj;
	        }
		} else if (sysName.equals(serv2) == true) {
			sysName = "hantei";
			dlFlag = 1;	//List読み込み
	    	if (obj.equals("2") == true) {
				String today = MyUtils.getToday();
				beseUrl = "http://localhost/api/2?";
				filter = "today='" + today + "'";
				sort = "";
				objName = "hantei";
			}
		} else if (sysName.equals(serv3) == true) {
			sysName = "kyaku";
			dlFlag = 1;	//List読み込み
	    	if (obj.equals("1") == true) {
				String today = MyUtils.getToday();
				beseUrl = "http://localhost/api/1?";
				filter = "today='" + today + "'";
				sort = "";
				objName = "kokunai";
			} 
		}
		if (objName == null)
			return "対象Object処理なし";
		
		MyUtils.SystemLogPrint("fields " + fields);
		MyUtils.SystemLogPrint("filter " + filter);
		MyUtils.SystemLogPrint("sort " + sort);
		url = beseUrl;
		if (fields.equals("") != true) {
			String encoded;
			try {
				encoded = URLEncoder.encode(fields, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				String msg = e.toString();
				return msg;
			}
			System.out.println("fieldsエンコード結果: " + encoded);
			url = url + "&fields=" + encoded;
		}
		if (filter.equals("") != true) {
			String encoded;
			try {
				encoded = URLEncoder.encode(filter, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				String msg = e.toString();
				return msg;
			}
			System.out.println("filterエンコード結果: " + encoded);
			url = url + "&filter=" + encoded;
		}
		if (sort.equals("") != true) {
			String encoded;
			try {
				encoded = URLEncoder.encode(sort, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				String msg = e.toString();
				return msg;
			}
			System.out.println("sortエンコード結果: " + encoded);
			url = url + "&sort=" + encoded;	//ソート(空白を含まないこと)
		}
		return null;
	}
	
	public String execute() {
		String msg;
		msg = getData(obj);			//データ取得
		if (msg != null) return msg;
		
		saveXlsPath = null;
		if (attachFlag == true) {
			msg = makeExcel();		//添付ファイル用Excelに書き出し
			if (msg != null) return msg;
		}
		
		msg = sendMail();			//メール添付送信
		if (msg != null) return msg;
		
		return null;
	}
	
	public String download(HttpServletResponse response) {
		String msg;
		msg = getData(obj);	//データ取得
		if (msg != null) return msg;
		
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
		outputPath = config.getPathOutputPath();
		String saveTxtPath = outputPath + obj + ".tsv";
		//---------------------------------------
		//HTTP request process
		//---------------------------------------
		WebApi api = new WebApi();
		api.setUrl("GET", url);
		int res = -1;
		try {
            //既存ファイルがあれば削除
			MyFiles.existsDelete(saveTxtPath);
			//データダウンロード
			res = api.download(saveTxtPath, dlFlag);	//dlFlag 0:ファイル出力 1:List読み込み
		} catch (IOException e) {
			e.printStackTrace();
        	return e.toString();
		}
        if (res != 200) {	//HttpURLConnection.HTTP_OK: 200
			String msg = "HTTP Connection Failed: " + res;
	        MyUtils.SystemErrPrint(msg);
        	return msg;
		}
        if (dlFlag == 0) {
    		//---------------------------------------
    		//TSVファイル読み込み
    		//---------------------------------------
            list = null;
    		try {
    			list = MyFiles.parseTSV(saveTxtPath, "UTF-8");	//or "SJIS"
    		} catch (IOException e) {
    			e.printStackTrace();
            	return e.toString();
    		}
        } else {
			list = api.getListData();
        }
		if (list.size() < 2) {
			MyUtils.SystemErrPrint("抽出データなし");
		}
		
		return null;
	}
	
	//---------------------------------------
	//Excelに書き出し
	//---------------------------------------
	public String makeExcel() {
		if (list.size() < 2) {
			String msg = "抽出データなし";
			MyUtils.SystemErrPrint(msg);
			return null;
		}
		templePath = config.getPathTempletePath();
		outputPath = config.getPathOutputPath();
        String defXlsPath = templePath + objName + ".xlsx";
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
			xlsx.setColFormat(colFormat);
			int err = xlsx.writeData(objName, list, true);
			if (err == 0) {
				//xlsx.refreshPivot("pivot");
				//Excelファイル保存
				//saveXlsPath = outputPath + objName + "_" + MyUtils.getDateStr() +".xlsx";
				saveXlsPath = outputPath + objName + ".xlsx";
				MyUtils.SystemLogPrint("  XLSXファイル保存: " + saveXlsPath);
				xlsx.save(saveXlsPath);
			} else {
				saveXlsPath = null;	//添付ファイル無し
			}
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
        int maxRow = list.size();
		if (maxRow > 1) {
			mailBody = "件数: " + (maxRow-1);
			mailBody = mailBody + getGroupShukei(list);
		} else {
			mailBody = "件数: 0";
		}
		
		String subject = "[" + sysName + "]通知(" + objName + " " + MyUtils.getDate() + ")";
		if (attachFlag == false)
			saveXlsPath = null;	//添付不要
		sendMail.execute(subject, mailBody, saveXlsPath);
		
		return null;
	}
	
    public String getGroupShukei(ArrayList<ArrayList<String>> list) {
		int colIdx = 0;
    	String msg = "";
		if (sysName.equals(serv1) == true) {
	        if (obj.equals("seisan") == true) {
				colIdx = 28;
			} if (obj.equals("uriage") == true) {
				colIdx = 23;
			}
		}
		if (colIdx == 0) {
			return "";	//未登録は、何も表示しない。
		}
        int maxRow = list.size();
		String code;
		String name;
		ArrayList<ClassMstInfo> classMstList = new ArrayList<ClassMstInfo>();
    	boolean matching = false;
        for (int rowIdx=1; rowIdx<maxRow; rowIdx++) {
        	code = list.get(rowIdx).get(colIdx);
        	name = list.get(rowIdx).get(colIdx+1);
			matching  = false;
        	for (ClassMstInfo cm : classMstList) {
        		if (cm.code.equals(code) == true) {
        			matching  = true;
        			break;
        		}
        	}
        	if (matching == false) {
	        	ClassMstInfo classMst = new ClassMstInfo(code, name);
        		classMstList.add(classMst);
        	}
        } //rowIdx
        for (int rowIdx=1; rowIdx<maxRow; rowIdx++) {
        	code = list.get(rowIdx).get(colIdx);
        	for (ClassMstInfo cm : classMstList) {
        		if (cm.code.equals(code) == true) {
        			cm.cnt++;
        			break;
        		}
        	}
        } //rowIdx
    	for (ClassMstInfo cm : classMstList) {
    		msg = msg + "\n" + cm.name + "("+ cm.code + "): " + cm.cnt;
    	}
		return msg;
    }
}

class ClassMstInfo {
	String code;
	String name;
	int cnt;
	
	ClassMstInfo() {cnt = 0;}
	
	ClassMstInfo(String cd, String nm) {
		code = cd;
		name = nm;
		cnt = 0;
	}
}

