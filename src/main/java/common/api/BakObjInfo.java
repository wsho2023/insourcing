package common.api;

import java.util.ArrayList;

import com.example.demo.ApiConfig;

import common.backup.ScanBackupFile;
import common.utils.MyUtils;

public class BakObjInfo {
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
    ScanBackupFile backup;
	
	public BakObjInfo(ApiConfig argConfig, String argSys, String argObj) {
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
		String targetPath = "";
		String backupPath = "";
		int days = 0;
		
        sysName = "Backup";
        if (obj.equals("trace") == true) {
        	//curl -X POST http://localhost:8080/backup?obj=trace
        	targetPath = "D:\\\\pleiades\\upload\\done\\";
        	backupPath = "D:\\\\pleiades\\upload\\done\\backup\\";
        	days = 30;	//日
        	objName = "trace";
        } else if (obj.equals("online") == true) {
        	//curl -X POST http://localhost:8080/backup?obj=online
        	targetPath = "D:\\\\pleiades\\upload\\done\\";
        	backupPath = "D:\\\\pleiades\\upload\\done\\backup\\";
        	days = 30;	//日
        	objName = "online";
        } 
		if (objName == null)
			return "対象Object処理なし";
		
		backup = new ScanBackupFile(targetPath, backupPath, days);
		
		return null;
	}
	
	public String execute() {
        String msg = run();		//Backup実行
		if (msg != null) return msg;
		
		msg = makeExcel();			//Excelに書き出し
		if (msg != null) return msg;
		
		msg = sendMail();			//メール添付送信
		if (msg != null) return msg;
		
		return null;
	}
	
	//---------------------------------------
	//Backup実行
	//---------------------------------------
	public String run() {
    	backup.run();
    	
		return null;
	}
	
	//---------------------------------------
	//Excelに書き出し
	//---------------------------------------
	public String makeExcel() {
		saveXlsPath = "";
		
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
