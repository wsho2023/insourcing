package watchdogfax;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.ResourceBundle;

import common.utils.MyFiles;
import common.utils.MyMail;
import common.utils.MyUtils;

public class WatchdogFax {
	static String targetPath;
	static ResourceBundle rb;
	static MyMail mailConf;
	
	public static void main(String[] args) {
		//①フォルダチェック
		rb = ResourceBundle.getBundle("prop");
		targetPath = rb.getString("TARGET_PATH");
		ArrayList<String> list = scanRemaindFile(targetPath);
		if (list.size() > 0) {
			String msg = "(" + list.size() + "件)"
					   + "\n<" + targetPath + ">";
			sendMail(msg);
		}
		
		//②稼働監視チェック
		String watchdogPath = targetPath + "data\\watchdog.dat";
		try {
			ArrayList<String> lines = MyFiles.readText2List(watchdogPath);
            Date kiroku = MyUtils.parseDate(lines.get(0));
            
        	// 現在日時を取得
            Calendar st = Calendar.getInstance();	//Calendarクラスで現在日時を取得
            st.add(Calendar.MINUTE, -10);			//現在値を取得(10分前)
            Date start = st.getTime();           	//Dateに変換
            if (start.compareTo(kiroku) > 0) {		//compareToで比較
    			String msg = "kiroku: " + kiroku + "\nstart : " + start;
    			sendMail(msg);
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static ArrayList<String> scanRemaindFile(String targetPath) {
		//指定ディレクトリ配下のファイルのみ(またはディレクトリのみ)を取得
		File fileArray[] = sortedFiles(targetPath);
		ArrayList<String> remainList = new ArrayList<String>();
		try {
			for (File f: fileArray){
				if (f.isFile()) {
					String fileName = MyFiles.getFileName(f.toString());	//フルパスからファイル名取得
					
					String extension = fileName.substring(fileName.length()-3);	//拡張子：後ろから3文字
					if (extension.equals("pdf") == true) {
						remainList.add(fileName);
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		return remainList;
	}
	
	//ファイル日時時系列でソートしてリストアップ		//https://teratail.com/questions/107345
    private static File[] sortedFiles(String path) {
        File dir = new File(path);
        File fileArray[] = dir.listFiles();
        Arrays.sort(fileArray, new Comparator<File>() {
            public int compare(File file1, File file2) {
                return file1.lastModified() >= file2.lastModified() ? 1 : -1;
            }
        });
        return fileArray;
    }

	private static void sendMail(String msg) {
		mailConf = new MyMail();
		mailConf.host = rb.getString("MAIL_HOST");
		mailConf.port = rb.getString("MAIL_PORT");
		mailConf.username = rb.getString("MAIL_USER");
		mailConf.password = rb.getString("MAIL_PASS");
		mailConf.smtpAuth = rb.getString("MAIL_SMTP_AUTH");
		mailConf.starttlsEnable = rb.getString("MAIL_SMTP_STARTTLS_ENABLE");
		
		//-------------------------------------------------------
		//ファイル添付メール送信
		//-------------------------------------------------------
		mailConf.fmAddr = rb.getString("MAIL_FROM");
		mailConf.toAddr = rb.getString("MAIL_TO");
		mailConf.ccAddr = rb.getString("MAIL_CC");
		mailConf.bccAddr = "";
		mailConf.subject = rb.getString("MAIL_SUBJECT");
		mailConf.body = msg;
		mailConf.attach =  null;
		
		MyUtils.SystemLogPrint("  メール送信...");
		MyUtils.SystemLogPrint("  MAIL FmAddr: " + mailConf.fmAddr);
		MyUtils.SystemLogPrint("  MAIL ToAddr: " + mailConf.toAddr);
		MyUtils.SystemLogPrint("  MAIL CcAddr: " + mailConf.ccAddr);
		MyUtils.SystemLogPrint("  MAIL BcAddr: " + mailConf.bccAddr);
		MyUtils.SystemLogPrint("  MAIL Subject: " + mailConf.subject);
		MyUtils.SystemLogPrint("  MAIL Body: \n" + mailConf.body);
		//MyUtils.SystemLogPrint("  MAIL Attach: " + mailConf.attach);
		mailConf.sendRawMail();
        System.out.println("メール送信完了");
	}
}
