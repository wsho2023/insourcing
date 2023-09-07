package common.api;

import java.io.IOException;
import java.util.ArrayList;

import com.example.demo.ApiConfig;

import common.utils.MyExcel;
import common.utils.MyFiles;
import common.utils.MyUtils;

public class GaihiObjInfo {
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
    
	String CONNECT_INFO;
	String TABLE_NAME;
	String TARGET_PATH; 
	String READ_FILE_PATH1;
	String READ_FILE_PATH2;
	String WRITE_PATH;
	String BACKUP_PATH;
	String OUTPUT_PATH;
	String FTP_BAT_PATH;  
	String COPY_BAT_PATH;  
	
	public GaihiObjInfo(ApiConfig argConfig, String argSys, String argObj) {
		config = argConfig;
        sys = argSys;
        sysName = null;
		obj = argObj;
		objName = null;
		colFormat = null;
		System.out.println(sys + " obj: " + obj);
		sendMail = new SendMail(config);
		
		CONNECT_INFO = "scsales@sacles@orcl";
		TABLE_NAME = "GAIHI_HASHIN_TRN";
		TARGET_PATH = System.getProperty("user.dir") + System.getProperty("file.separator"); 
		READ_FILE_PATH1 = "complete.tsv";
		READ_FILE_PATH2 = "complete_makepdf.tsv";
		WRITE_PATH = "complete.txt";
		BACKUP_PATH = "write_%s.tsv";
		OUTPUT_PATH = "Excel.xlsx";
		FTP_BAT_PATH = "gaihi_ftp.bat";  
	}
	
	public String makeObject() {
        sysName = "Gaihi";
        //curl -X POST http://localhost:8080/api/gaihi
		return null;
	}
/*
	①FTPから取得するバッチ実行
	②complete.tsv(SJIS)から 前稼働日読み込み
	③0だったら、もう一つのファイルから 前稼働日読み込み（5のつく日）
	④listに格納 UTF-8.tsvに出力
	⑤sqrldrで、TMPテーブルへロード
	⑥upload.sqlを実行（sqlplus）

	⑦listに格納をExcelマスタ末尾に書き込み
	⑧そのExcelマスタをサーバーへコピー
*/
	public String execute() {
		String[] cmdList;
		//---------------------------------------
		//①FTPから取得するバッチ実行
		//---------------------------------------
		if (MyFiles.exists(FTP_BAT_PATH) != true) {
			return "ファイルなしエラー: " + FTP_BAT_PATH;
		}
		cmdList = new String[1];
		cmdList[0]	=	FTP_BAT_PATH;
		try {
		    MyUtils.exeCmd(cmdList);
		} catch (Exception e) {
			e.printStackTrace();
			return e.toString();
		}
		cmdList = null;
		
		//---------------------------------------
		//②complete.tsv(SJIS)から 前日分読込み
		//---------------------------------------
		if (MyFiles.exists(READ_FILE_PATH1) != true) {
			return "ファイルなしエラー: " + READ_FILE_PATH1;
		}
		ArrayList<ArrayList<String>> list1 = null;
		ArrayList<ArrayList<String>> list2 = null;
		list1 = new ArrayList<ArrayList<String>>();
		try {
			//https://itsakura.com/it-shiftjis-ms932
			list1 = MyFiles.parseTSV(READ_FILE_PATH1, "MS932");	//"SJIS" or "UTF-8"
		} catch (IOException e) {
			e.printStackTrace();
        	return e.toString();
		}
		if (list1.size() == 0) {
			MyUtils.SystemErrPrint("抽出データなし");
		}
		String kinou = MyUtils.getToday(-1);	//前日(YYYY/MM/DD)
		String procDate11;
		list2 = new ArrayList<ArrayList<String>>();
		for (ArrayList<String> line : list1) {
			procDate11 = line.get(24);	//処理日時11
			if (procDate11.equals("") != true && procDate11.equals("処理日時11") != true) {
				procDate11 = procDate11.substring(0,10);	//YYYY/MM/DDの前方10桁を抽出
				if (procDate11.equals(kinou) == true) {
					list2.add(line);
				}
			}
		}
		
		//---------------------------------------
		//③0だったら、もう1つのファイルから 前日分読込み（5のつく日）
		//---------------------------------------
		if (list2.size() == 0) {
			if (MyFiles.exists(READ_FILE_PATH2) != true) {
				return "ファイルなしエラー: " + FTP_BAT_PATH;
			}
			list2 = null;
			list1 = new ArrayList<ArrayList<String>>();
			try {
				list1 = MyFiles.parseTSV(READ_FILE_PATH2, "MS932");	//"SJIS" or "UTF-8"
			} catch (IOException e) {
				e.printStackTrace();
	        	return e.toString();
			}
			kinou = MyUtils.getToday(-1);	//前日(YYYY/MM/DD)
			//String data;
			list2 = new ArrayList<ArrayList<String>>();
			for (ArrayList<String> line : list1) {
				procDate11 = line.get(24);	//処理日時11
				if (procDate11.equals("") != true && procDate11.equals("処理日時11") != true) {
					procDate11 = procDate11.substring(0,10);	//YYYY/MM/DDの前方10桁を抽出
					if (procDate11.equals(kinou) == true) {
						list2.add(line);
					}
				}
			}
		}
		
		//---------------------------------------
		//④listに格納 UTF-8.tsvに出力
		//---------------------------------------
		if (list2.size() == 0) {
			String msg = "抽出データなし"; 
			return msg;
		}
		
		//TSVファイル書き出し(UTF-8)
		try {
			MyFiles.WriteList2File(list2, WRITE_PATH);
		} catch (IOException e) {
			e.printStackTrace();
			return e.toString();
		}
		
		//---------------------------------------
		//⑤sqrldrで、TMPテーブルへロード
		//---------------------------------------
		cmdList = new String[6];
		cmdList[0]	=	"sqlldr";
		cmdList[1]	=	CONNECT_INFO;
		cmdList[2]	=	"control=" + TABLE_NAME + ".ctl";
		cmdList[3]	=	"log=" + TABLE_NAME + "_" + MyUtils.getDateStr() + ".log";;
		cmdList[4]	=	"readsize=1000000";
		cmdList[5]	=	"rows=128";
		try {
		    MyUtils.exeCmd(cmdList);
		} catch (Exception e) {
			e.printStackTrace();
			return e.toString();
		}
		cmdList = null;
		
		//Backupの作成
		String yomitoriDay = kinou.replace("/", "");	//YYYYMMDD
		String backupPath = String.format(BACKUP_PATH, yomitoriDay);
		try {
			MyFiles.copyOW(WRITE_PATH, backupPath);
		} catch (IOException e) {
			e.printStackTrace();
        	return e.toString();
		}

		//⑥upload.sqlを実行（sqlplus）
		cmdList = new String[4];
		cmdList[0]	=	"sqlplus";
		cmdList[1]	=	"-S";
		cmdList[2]	=	CONNECT_INFO;
		cmdList[3]	=	"@updateGaihi.sql";
		try {
		    MyUtils.exeCmd(cmdList);
		} catch (Exception e) {
			e.printStackTrace();
			return e.toString();
		}
		cmdList = null;
		
		//---------------------------------------
		//⑦listに格納をExcelマスタ末尾に書き込み
		//---------------------------------------
		String xlsPath = OUTPUT_PATH;
		String sheetName = "sheet1";
		MyExcel xlsx = new MyExcel();
		try {
			//Excelファイルオープン(xlsPath=nullなら、新規作成)
			xlsx.open(xlsPath, sheetName);
			int lastRow = xlsx.sheet.getLastRowNum();
			int maxRow = list2.size();
			int maxCol;
			String strValue;
			int rowIdx = lastRow + 1;	//書き込み行＝末尾行＋１
			for (int Idx=0; Idx<maxRow; Idx++) {
				xlsx.createRow(rowIdx);			//行の生成
				maxCol = list1.get(Idx).size();
				for (int colIdx=0; colIdx<maxCol; colIdx++) {
					strValue = list2.get(Idx).get(colIdx);
					xlsx.cell = xlsx.row.createCell(colIdx);
					xlsx.cell.setCellValue(strValue);
				}
				rowIdx++;
			}
			//末尾行をアクティブセルにする。
			xlsx.getRow(rowIdx-1);	
			xlsx.cell = xlsx.row.getCell(0);
			xlsx.cell.setAsActiveCell();
			xlsx.save(xlsPath);
			xlsx.close();
		} catch (IOException e) {
			e.printStackTrace();
			return e.toString();
		}
		
		//---------------------------------------
		//⑧そのExcelマスタをサーバーへコピー
		//---------------------------------------
		cmdList = new String[1];
		cmdList[0]	=	"gaihi_copy.bat";
		try {
		    MyUtils.exeCmd(cmdList);
		} catch (Exception e) {
			e.printStackTrace();
			return e.toString();
		}
		cmdList = null;
		
		MyUtils.SystemErrPrint("完了");
		
		return null;
	}
}
