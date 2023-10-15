package common.po;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.Comparator;

import com.example.demo.SpringConfig;

import common.ocr.OcrDataFormBean;
import common.utils.MyFiles;
import common.utils.MyMail;
import common.utils.MyUtils;

public class PoScanFile {
	SpringConfig config;
	String targetPath;
	MyMail mailConf;

	public PoScanFile(SpringConfig argConfig, String argTargetPath) {
		MyUtils.SystemLogPrint("■PoScanFileコンストラクタ");
		config = argConfig;
		targetPath = argTargetPath;
		
		mailConf = new MyMail();
		mailConf.host = config.getMailHost();
		mailConf.port = config.getMailPort();
		mailConf.username = config.getMailUsername();
		mailConf.password = config.getMailPassword();
		mailConf.smtpAuth = config.getMailSmtpAuth();
		mailConf.starttlsEnable = config.getMailSmtpStarttlsEnable();
		mailConf.fmAddr = config.getMailFrom();	//固定
	}
	
	public String scanGetTargetPath() {
		return this.targetPath;
	}
	
	public void scanRemainedFile() {
		//指定ディレクトリ配下のファイルのみ(またはディレクトリのみ)を取得
		File fileArray[] = sortedFiles(this.targetPath);
		//MyUtils.SystemLogPrint("■scanRemainedFile: start..." + this.targetPath);
		try {
			for (File f: fileArray){
				if (f.isFile()) {
					String fileName = MyFiles.getFileName(f.toString());	//フルパスからファイル名取得
					MyUtils.SystemLogPrint("  ファイル検出: " + fileName);
					scanProcess(f.toString());
	            }
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		//MyUtils.SystemLogPrint("■scanRemainedFile: end");
	}
	
	//ファイル日時時系列でソートしてリストアップ		//https://teratail.com/questions/107345
    private File[] sortedFiles(String path) {
        File dir = new File(path);
        File fileArray[] = dir.listFiles();
        Arrays.sort(fileArray, new Comparator<File>() {
            public int compare(File file1, File file2) {
                return file1.lastModified() >= file2.lastModified() ? 1 : -1;
            }
        });
        return fileArray;
    }
    
	public void scanProcess(String uploadFilePath) {
		MyUtils.SystemLogPrint("■scanProcess: start... 取込開始");
		//importData(uploadFilePath);
	    //------------------------------------------------------
	    //取り込み実行
	    //------------------------------------------------------
		//https://blog.goo.ne.jp/xmldtp/e/beb03fb01fb1d1a2c37db8d69b43dcdd
		//コマンドラインから****.vbsを呼び出せる。
		String[] cmdList = new String[5];
		cmdList[0]	=	"cscript";
		cmdList[1]	=	"impChu.vbs";		//VBSファイル指定
		cmdList[2]	=	"/file:impChu.xlsm";
		cmdList[3]	=	"/method:run";
		cmdList[4]	=	"/importFilePath:" + uploadFilePath;
		try {
		    MyUtils.exeCmd(cmdList);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		}
		
		//------------------------------------------------------
	    //フォルダ整理（フォルダ存在を確認し、なければフォルダ作成）
	    //------------------------------------------------------
	    String dstDirPath = this.targetPath + "done\\";
	    try {
			MyFiles.notExistsCreateDirectory(dstDirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    //------------------------------------------------------
	    //フォルダへ退避（doneフォルダへ移動）
	    //------------------------------------------------------
		String fileName = MyFiles.getFileName(uploadFilePath);
	    String dstPath = this.targetPath + "done\\" + fileName;
		try {
			MyFiles.moveOW(uploadFilePath, dstPath);	//上書き移動
		} catch (NoSuchFileException e) {
			e.printStackTrace();
			return;	//★あやしいので、いったん、登録処理はしない。
		} catch (IOException e) {
			e.printStackTrace();
			return;	//★あやしいので、いったん、登録処理はしない。
		}
    	MyUtils.SystemLogPrint("■scanProcess: end... ファイル移動: " + dstPath);
	}
	
	public void sendMailProcess(OcrDataFormBean ocrData, String pdfPath) throws Throwable {
		PoUploadBean upload = importData(ocrData.getUploadFilePath());
		if (upload == null)
			return;
		String unitName = ocrData.getUnitName();
		//String soshinMoto = "";
		String mailBody1="";
		String mailBody2="";

		MyUtils.SystemLogPrint("sendMailProcess: start");
		//フルパスからファイル名取得
		File file = new File(pdfPath);
		String fileName  = file.getName() ;		
		//配信メール情報:From
		mailConf.fmAddr = upload.getUserId();
		//配信メール情報:Bcc
		mailConf.bccAddr = "";
		mailConf.toAddr = upload.getUserId();

		//パターン①: 注文書のケース
		mailConf.subject = ocrData.getDocSetName() + "受信連絡" + "(" + ocrData.getCreatedAt() + " " + ocrData.getUnitName() + ")";
		mailConf.attach = pdfPath;
		if (ocrData.getRenkeiResult().equals("") != true) {
			mailConf.body = mailBody1 + "\n<OCR読取り結果>\n" + ocrData.getRenkeiResult() + "\n" + mailBody2 + "\n" + fileName + "\n"; 
		} else if (ocrData.getCheckResult().equals("") != true) {
			mailConf.body = mailBody1 + "\n<OCR読取り結果>\n" + ocrData.getCheckResult() + "\n" + mailBody2 + "\n" + fileName + "\n"; 
		} else {
			mailConf.body = mailBody1 + "\n<OCR読取り結果>\n正常終了\n\n" + mailBody2 + "\n" + fileName + "\n"; 
		}
		sendAttachMail();
		
        //------------------------------------------------------
        //ERRL登録処理
        //------------------------------------------------------
		String subject = ocrData.getDocSetName() + "注文書" + "(" + ocrData.getCreatedAt() + " " + ocrData.getUnitName() + ")";
		String chubanlist = ocrData.getChubanlist();
		String chubanlistMsg = "注文番号(PO)\n" + chubanlist;
		chubanlist = chubanlist.replace("\n", " ");
    	PoErrlBean errl = new PoErrlBean();
    	errl.setErrlData("OCR"+ocrData.getUnitId(), ocrData.getDocSetName(), ocrData.getUnitName(), chubanlist, subject, 0, pdfPath);
    	PoErrlDAO.getInstance(config).insertDB(errl);
		
		//メール送信第2弾
		//本文に ocrData.getChubanlist()
		//sendScanMail(ocrData.getDocSetName());
    	
		MyUtils.SystemLogPrint("sendMailProcess: end");
	}

	private PoUploadBean importData(String uploadFilePath) {
		PoUploadBean upload = null;
		int retryCnt = 0;
		for (;;) {
			upload = PoUploadDAO.getInstance(config).quertyWithUploadPath(uploadFilePath);
			if (upload != null)
				break;
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//取得データがまだないときがありリトライを追加
			retryCnt++;
			if (retryCnt > 10) {
				MyUtils.SystemErrPrint("アップロード情報を取得できませんでした。処理を中止します。");
				return null;
			}
		} 
		
		String userId = upload.getUserId();
		String datetime = upload.getDatetime();
		String triCd = upload.getToricd();
		String code = upload.getCode();
		String inputPath = upload.getInputPath();
		//⇒引数として渡す
		MyUtils.SystemLogPrint("  userId: " + userId + "  datetime: " + datetime + "  triCd: " + triCd + "  code: " + code + "  inputPath: " + inputPath);
		
		return upload;
	}
	
	//------------------------------------------------------
	//ファイル添付メール送信
	//------------------------------------------------------
	private int sendAttachMail() {
		MyUtils.SystemLogPrint("  メール送信...");
		MyUtils.SystemLogPrint("  MAIL FmAddr: " + mailConf.fmAddr);
		MyUtils.SystemLogPrint("  MAIL ToAddr: " + mailConf.toAddr);
		MyUtils.SystemLogPrint("  MAIL CcAddr: " + mailConf.ccAddr);
		MyUtils.SystemLogPrint("  MAIL BcAddr: " + mailConf.bccAddr);
		MyUtils.SystemLogPrint("  MAIL Subject: " + mailConf.subject);
		MyUtils.SystemLogPrint("  MAIL Body: \n" + mailConf.body);
		MyUtils.SystemLogPrint("  MAIL Attach: " + mailConf.attach);
		mailConf.toAddr = mailConf.fmAddr;	//for debug
		mailConf.ccAddr = "";				//for debug
		mailConf.bccAddr = mailConf.fmAddr;	//for debug
		mailConf.sendRawMail();
		
		return 0;
	}
}
