package common.fax;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.Comparator;

import com.example.demo.SpringConfig;
import com.fasterxml.jackson.databind.JsonNode;

import common.ocr.OcrDataFormBean;
import common.ocr.OcrDataFormDAO;
import common.ocr.OcrFormBean;
import common.ocr.OcrFormDAO;
import common.po.PoErrlBean;
import common.po.PoErrlDAO;
import common.utils.MyExcel;
import common.utils.MyFiles;
import common.utils.MyMail;
import common.utils.MyUtils;

public class FaxScanFile {
	SpringConfig config;
	String kyoten;
	String targetPath;
	String TEST_FLAG;
	String SCAN_CLASS1;
	String SCAN_CLASS2;
	String FORM_TYPE1;
	String FORM_TYPE2;
	String OCR_INPUT_PATH;
	MyMail mailConf;

	public FaxScanFile(SpringConfig argConfig, String argKyoten) {
		MyUtils.SystemLogPrint("■FaxScanFileコンストラクタ: " + argKyoten);
		config = argConfig;
		this.kyoten = argKyoten;

		mailConf = new MyMail();
		mailConf.host = config.getMailHost();
		mailConf.port = config.getMailPort();
		mailConf.username = config.getMailUsername();
		mailConf.password = config.getMailPassword();
		mailConf.smtpAuth = config.getMailSmtpAuth();
		mailConf.starttlsEnable = config.getMailSmtpStarttlsEnable();
		
		this.TEST_FLAG = config.getScanTestFlag();
		this.SCAN_CLASS1 = config.getScanDefTgt1();
		this.SCAN_CLASS2 = config.getScanDefTgt2();
		this.FORM_TYPE1 = config.getScanFormType1();
		this.FORM_TYPE2 = config.getScanFormType2();

		if (this.kyoten.equals(this.SCAN_CLASS1) == true ) {
			this.targetPath = config.getScanPath1();
		} else if (this.kyoten.equals(this.SCAN_CLASS2) == true ) {
			this.targetPath = config.getScanPath2();
		} else {
			MyUtils.SystemErrPrint("  Scan場所が登録されていません");
			this.targetPath = "";			
		}
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
					
					String extension = fileName.substring(fileName.length()-3);	//拡張子：後ろから3文字
					if (extension.equals("pdf") == true) {
						//String extension = fileName.substring(fileName.lastIndexOf("."));	//
						MyUtils.SystemLogPrint("  ファイル検出: " + fileName);
						scanProcess(fileName);
						Thread.sleep(2000);
					//} else {
					//	System.out.println(fileName +":" + f.lastModified());						
					//	Thread.sleep(2000);
					}
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
    
	public void scanProcess(String fileName) throws Throwable {
		String faxNo = "";
		String createdAt = "";
		String dateStr = "";
		String timeStr = "";
		String jobNo = "";
		String soshinMoto = "";
		
		MyUtils.SystemLogPrint("■scanProcess: start");
		//1 ファイル名をパース
		if (this.kyoten.equals(this.SCAN_CLASS1) == true) {
			faxNo = fileName.substring(3, (fileName.length() -20 -4));	//doc + datetimeJOB + .pdf分差し引く
			createdAt = fileName.substring((fileName.length() -24), (fileName.length() -10));	//2020 0727 143359 041084 の 20桁
			dateStr = createdAt.substring(0,4) + "/" + createdAt.substring(4,6) + "/" + createdAt.substring(6,8); 
			timeStr = createdAt.substring(8,10) + ":" + createdAt.substring(10,12) + ":" + createdAt.substring(12,14); 
			jobNo = fileName.substring((3 + faxNo.length() + 14), (3 + faxNo.length() + 19));
		} else if (this.kyoten.equals(this.SCAN_CLASS2) == true) {	//★逆にしている
			faxNo = fileName.substring(3, (fileName.length() -20 -4));	//doc + datetimeJOB + .pdf分差し引く
			createdAt = fileName.substring((fileName.length() -18), (fileName.length() -4));	//2020 0727 143359 041084 の 20桁
			dateStr = createdAt.substring(0,4) + "/" + createdAt.substring(4,6) + "/" + createdAt.substring(6,8); 
			timeStr = createdAt.substring(8,10) + ":" + createdAt.substring(10,12) + ":" + createdAt.substring(12,14); 
			jobNo = fileName.substring((3 + faxNo.length()), (3 + faxNo.length() + 6));
		}
		if (faxNo == "") faxNo = "送信元なし";
		MyUtils.SystemLogPrint("  pdfファイル検出...: faxNo " + faxNo + " datetime: " + dateStr + " " + timeStr + " jobNo " + jobNo);
		
		String mailBody1;
		String mailBody2;
		String syubetsu;	//B列
		//2 Excelオープン
		// Excelファイルへアクセス(eclipse上でパスをしていないとプロジェクトパスになる)
		String xlsPath = this.targetPath + this.kyoten + "-pdf管理表.xlsm";
		MyExcel xlsx = new MyExcel();
		xlsx.openXlsm(xlsPath, true);
		MyUtils.SystemLogPrint("  Excelオープン..." + xlsPath);
		// シートを取得
		xlsx.setSheet("MAIL");
		//配信メール情報:From
		mailConf.fmAddr = xlsx.getStringCellValue(1, 1);
		//配信メール情報:Bcc
		mailConf.bccAddr = xlsx.getStringCellValue(4, 1);
		//配信メール情報:Body1,2
		mailBody1 = xlsx.getStringCellValue(6, 1);
		mailBody2 = xlsx.getStringCellValue(7, 1);
		mailConf.body = mailBody1 + "\n" + mailBody2 + "\n" + fileName + "\n";
		
		//------------------------------------------------------
		//FAXNoからFAX送信元 検索
		xlsx.setSheet("マスタ");
		soshinMoto = xlsx.search(2, faxNo);	//結果をrowにセット。マッチしなければ最下行をセット。
		MyUtils.SystemLogPrint("FAX番号から引き当てた送信元: " + soshinMoto);
		xlsx.getCell(6);			//G列
		mailConf.toAddr = xlsx.getStringCellValue();
		xlsx.getCell(7);			//H列
		mailConf.ccAddr = xlsx.getStringCellValue();
		xlsx.getCell(0);			//A列:FLAG
		//CellType ctype = xlsx.getCellType(0);
		//if (ctype == CellType.NUMERIC) {
		//	int val = (int)xlsx.getNumericCellValue();
		//} else {
		//}
		xlsx.getCell(1);			//B列:種別
		syubetsu = xlsx.getStringCellValue();
		//------------------------------------------------------
		if (faxNo == "送信元なし") faxNo = "";	//表示は空白
		xlsx.close();
    	
    	//------------------------------------------------------
        //フォルダ整理（フォルダ存在を確認し、なければフォルダ作成）
        //------------------------------------------------------
    	String dstDirPath = this.targetPath + soshinMoto;
		MyFiles.notExistsCreateDirectory(dstDirPath);
		
        //------------------------------------------------------
        //送信元フォルダへ移動(上書き)
        //------------------------------------------------------
        String srcPath = this.targetPath + fileName;
        String dstPath = dstDirPath + "\\" + fileName;
    	MyUtils.SystemLogPrint("  ファイル移動: " + dstPath);
		try {
			MyFiles.moveOW(srcPath, dstPath);	//上書き
		} catch (NoSuchFileException e) {
			return;	//★あやしいので、いったん、登録処理はしない。
		}
    	
        //------------------------------------------------------
        //Fax登録処理
        //------------------------------------------------------
    	FaxDataBean fax = new FaxDataBean();
    	fax.setFaxData(faxNo, dateStr, timeStr, jobNo, fileName, soshinMoto, dstPath, this.kyoten);
    	FaxDataDAO.getInstance(config).insertDB(fax);
        
        //------------------------------------------------------
        //OCR登録処理(注文書)
        //------------------------------------------------------
		int type = -1;
		if (this.kyoten.equals(this.SCAN_CLASS1) == true) {
			type = 0;
		} else if (this.kyoten.equals(this.SCAN_CLASS2) == true) {
			type = 0;
			if (syubetsu.equals(FORM_TYPE1) == true) {
				syubetsu = "注文書";
			}
		} else {
			return;
		}
		//注文書以外、番号登録ありは、この時点でメール送信
    	if (syubetsu.equals("注文書") != true && faxNo.equals("") != true && soshinMoto.equals("00送信元なし") != true) {
			mailConf.subject = this.kyoten + "受信連絡("+ MyUtils.getDate() + " " + soshinMoto + ")";
			mailConf.attach = dstPath;
			sendScanMail(this.kyoten);
			MyUtils.SystemLogPrint("■scanProcess: end");
			
			return;
		}
		//それ以外は、OCR登録のプロセス
		MyUtils.SystemLogPrint("  OCR登録...");
		int res = registOcrProcess(dstPath, type);
		if (res != 0) {
			//帳票定義なしのケース ⇒ この時点でメール送信
/*			//2 Excelオープン
			// Excelファイルへアクセス(eclipse上でパスをしていないとプロジェクトパスになる)
			//String xlsPath = this.targetPath + this.kyoten + "-pdf管理表.xlsm";
			//MyExcel xlsx = new MyExcel();
			xlsx.openXlsm(xlsPath, true);
			MyUtils.SystemLogPrint("  Excelオープン..." + xlsPath);
			// シートを取得
			xlsx.setSheet("MAIL");
			//配信メール情報:From
			mailConf.fmAddr = xlsx.getStringCellValue(1, 1);
			//配信メール情報:Bcc
			mailConf.bccAddr = xlsx.getStringCellValue(4, 1);
			//配信メール情報:Body1,2
			mailBody1 = xlsx.getStringCellValue(6, 1);
			mailBody2 = xlsx.getStringCellValue(7, 1);
			
			//------------------------------------------------------
			//FAXNoからFAX送信元 検索
			xlsx.setSheet("マスタ");
			xlsx.search(3, "00送信元なし");	//結果をrowにセット。マッチしなければ最下行をセット。
			//MyUtils.SystemLogPrint("FAX番号から引き当てた送信元: " + soshinMoto);
			xlsx.getCell(6);			//G列
			mailConf.toAddr = xlsx.getStringCellValue();
			xlsx.getCell(7);			//H列
			mailConf.ccAddr = xlsx.getStringCellValue();
			xlsx.getCell(0);			//A列:FLAG
			//CellType ctype = xlsx.getCellType(0);
			//if (ctype == CellType.NUMERIC) {
			//	int val = (int)xlsx.getNumericCellValue();
			//} else {
			//}
			xlsx.getCell(1);			//B列:種別
			syubetsu = xlsx.getStringCellValue();
			//------------------------------------------------------
			if (faxNo == "送信元なし") faxNo = "";	//表示は空白
			xlsx.close();
*/			
			mailConf.subject = this.kyoten + "受信連絡("+ MyUtils.getDate() + " " + soshinMoto + ")";
			mailConf.body = mailBody1 + "\n<OCR読取り結果>\nOCR帳票定義なし\n\n" + mailBody2 + "\n" + fileName + "\n";
			mailConf.attach = dstPath;
			sendScanMail(this.kyoten);
		}
		MyUtils.SystemLogPrint("■scanProcess: end");
		
		return;
	}

	private int registOcrProcess(String uploadFilePath, int type) {
		MyUtils.SystemLogPrint("registOcrProcess: start..." + uploadFilePath.toString());
		if (uploadFilePath.equals("") == true) {
			return -1;
		}
		String importPath = MyFiles.getParent(uploadFilePath);	//ファイルパスからフォルダパス取得
		String subFolder = MyFiles.getFileName(importPath);		//フォルダパスからフォルダ名取得
		
		int sortFlag = 0;
		String status = "";
		OcrDataFormBean ocrData = new OcrDataFormBean();
		if (subFolder.toString().contains("送信元なし") != true) {
			sortFlag = 0;	//定義名確定
			status = "REGIST";
		} else if (subFolder.toString().contains("送信元なし") == true && this.kyoten.equals(SCAN_CLASS2) == true) {
			sortFlag = 1;	//仕分けユニット行き
			status = "SORT";
		}
		//◆ImportPathを、フォルダ名(subFolder)＋kyotenに変更
		OcrFormBean ocrForm = OcrFormDAO.getInstance(config).queryOcrFormImportPath(sortFlag, subFolder, this.kyoten);
		if (ocrForm.getDocumentId() == null) {
			MyUtils.SystemLogPrint("  OCR帳票定義なし → OCR処理スキップしてFAX送信");
			return -1;
		}
		ocrData.setUnitName(ocrForm.getFormName());
		ocrData.setDocumentId(ocrForm.getDocumentId());
		ocrData.setDocumentName(ocrForm.getDocumentName());
		ocrData.setDocsetName(ocrForm.getDocsetName());
		ocrData.setUploadFilePath(uploadFilePath);
		ocrData.setStatus(status);
		ocrData.setType(type);
		OcrDataFormDAO.getInstance(config).insertReadingUnitDB(ocrData);
		
		MyUtils.SystemLogPrint("■registOcrProcess: end");
		
		return 0;		
	}
	
	public void changeFilePath(OcrDataFormBean ocrData) {
    	MyUtils.SystemLogPrint("changeFilePath: start");
		String srcPath = ocrData.getUploadFilePath();
		
		File file = new File(srcPath);
		String fileName  = file.getName();
        String dstDirPath = ocrData.getTargetPath() + ocrData.getUnitName();
		String dstPath = dstDirPath + "\\" + fileName;
		
    	//------------------------------------------------------
        //フォルダ整理（フォルダ存在を確認し、なければフォルダ作成）
        //------------------------------------------------------
		try {
			MyFiles.notExistsCreateDirectory(dstDirPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        //------------------------------------------------------
        //送信元フォルダへ移動(上書き)
        //------------------------------------------------------
    	MyUtils.SystemLogPrint("  ファイル移動: " + dstPath);
		try {
			MyFiles.moveOW(srcPath, dstPath);	//上書き
		} catch (NoSuchFileException e) {
			e.printStackTrace();
			return;	//★あやしいので、いったん、登録処理はしない。
		} catch (IOException e) {
			e.printStackTrace();
			return;	//★あやしいので、いったん、登録処理はしない。
		}
		
		//ファイル移動後、uploadFilePathを更新
		ocrData.setUploadFilePath(dstDirPath + "\\" + fileName);
		//各DBテーブルの更新
		OcrDataFormDAO.getInstance(config).updateUploadFilePath("00送信元なし", ocrData.getUnitName(), srcPath);
		FaxDataDAO.getInstance(config).updateUploadFilePath("00送信元なし", ocrData.getUnitName(), srcPath);
		
    	MyUtils.SystemLogPrint("changeFilePath: end");
	}

	//unitNameから情報引き当て、メール送信
	public void sendMailProcess(OcrDataFormBean ocrData, String pdfPath) throws Throwable {
		String unitName = ocrData.getUnitName();
		//String soshinMoto = "";
		String mailBody1;
		String mailBody2;
		String flag;
		String syubetsu;

		MyUtils.SystemLogPrint("sendMailProcess: start");
		//フルパスからファイル名取得
		File file = new File(pdfPath);
		String fileName  = file.getName() ;
		//2 Excelオープン
		// Excelファイルへアクセス(eclipse上でパスをしていないとプロジェクトパスになる)
		String xlsPath = ocrData.getTargetPath() + ocrData.getDocSetName() + "-pdf管理表.xlsm";
		MyExcel xlsx = new MyExcel();
		xlsx.openXlsm(xlsPath, true);	//read only
		MyUtils.SystemLogPrint("  Excelオープン..." + xlsPath);
		// シートを取得
		xlsx.setSheet("MAIL");
		//配信メール情報:From
		mailConf.fmAddr = xlsx.getStringCellValue(1, 1);
		//配信メール情報:Bcc
		mailConf.bccAddr = xlsx.getStringCellValue(4, 1);
		//配信メール情報:Body1,2
		mailBody1 = xlsx.getStringCellValue(6, 1);
		mailBody2 = xlsx.getStringCellValue(7, 1);
		//mailConf.body = mailBody1 + "\n" + mailBody2 + "\n" + fileName + "\n";
		
		//FAX送信元 検索
		xlsx.setSheet("マスタ");
		xlsx.search(3, unitName);	//結果をrowにセット。マッチしなければ最下行をセット。
		xlsx.getCell(6);				//G列
		mailConf.toAddr = xlsx.getStringCellValue();
		xlsx.getCell(7);				//H列
		mailConf.ccAddr = xlsx.getStringCellValue();
		xlsx.getCell(0);				//A列:FLAG
		if (xlsx.checkCellTypeNumeric() == true) {
			int val = (int)xlsx.getNumericCellValue();
			flag =  String.valueOf(val);
		} else {
			flag = xlsx.getStringCellValue();
		}
		xlsx.getCell(1);				//B列:種別
		syubetsu = xlsx.getStringCellValue();
		//★toAddrが ブランクの時、送信元なしの全員宛先に 変更すること
		
		xlsx.close();

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
		if (mailConf.toAddr == null) {
			//宛先がなければ
			getBroadcastMailAddress(ocrData.getTargetPath(), ocrData.getDocSetName());
		}
		sendScanMail(ocrData.getDocSetName());
		
		if (this.kyoten.equals(this.SCAN_CLASS1) == true) {
			//type = 2;
		} else if (this.kyoten.equals(this.SCAN_CLASS2) == true) {
			//type = 0;
			if (syubetsu.equals("K注文書") == true) {
				syubetsu = "注文書";
			}
		} else {
			return;
		}
		//メール送信第2弾
		//本文に ocrData.getChubanlist()
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

	public void sortMatchMailProcess(OcrDataFormBean ocrData, String readValue) throws IOException {
		String unitName = ocrData.getUnitName();
		String uploadFilePath = ocrData.getUploadFilePath();
		//String soshinMoto = "";
		String mailBody1;
		String mailBody2;
		//String flag = "0";
		//String syubetsu = "";
		
    	MyUtils.SystemLogPrint("sortMatchMailProcess: start");
		//仕分け帳票フォルダへファイル移動、uploadFilePathを更新
		String fileName = MyFiles.getFileName(uploadFilePath);	//パスからファイル名取得
		//2 Excelオープン
		// Excelファイルへアクセス(eclipse上でパスをしていないとプロジェクトパスになる)
		String xlsPath = ocrData.getTargetPath() + ocrData.getDocSetName() + "-pdf管理表.xlsm";
		MyExcel xlsx = new MyExcel();
		xlsx.openXlsm(xlsPath, true);	//read only
		MyUtils.SystemLogPrint("  Excelオープン..." + xlsPath);
		// シートを取得
		xlsx.setSheet("MAIL");
		//配信メール情報:From
		mailConf.fmAddr = xlsx.getStringCellValue(1, 1);
		//配信メール情報:Bcc
		mailConf.bccAddr = xlsx.getStringCellValue(4, 1);
		//配信メール情報:Body1,2
		mailBody1 = xlsx.getStringCellValue(6, 1);
		mailBody2 = xlsx.getStringCellValue(7, 1);
		
		//FAX送信元 検索
		xlsx.setSheet("マスタ");
		xlsx.search(3, unitName);	//結果をrowにセット。マッチしなければ最下行をセット。
		xlsx.getCell(6);				//G列
		mailConf.toAddr = xlsx.getStringCellValue();
		xlsx.getCell(7);				//H列
		mailConf.ccAddr = xlsx.getStringCellValue();
		//xlsx.getCell(0);				//A列:FLAG
		//if (xlsx.checkCellTypeNumeric() == true) {
		//	int val = (int)xlsx.getNumericCellValue();
		//	flag =  String.valueOf(val);
		//} else {
		//	flag = xlsx.getStringCellValue();
		//}
		//xlsx.getCell(1);				//B列:種別
		//syubetsu = xlsx.getStringCellValue();
		//★toAddrが ブランクの時、送信元なしの全員宛先に 変更すること
		
		xlsx.close();

		//パターン①: 仕分け後のケース
		mailConf.subject = ocrData.getDocSetName() + "受信連絡" + "(" + ocrData.getCreatedAt() + " " + ocrData.getUnitName() + ")";
		mailConf.attach = uploadFilePath;
		if (readValue.equals("") == true) {
			if (ocrData.getRenkeiResult() != null) {
				mailConf.body = mailBody1 + "\n<OCR読取り結果>\n" + ocrData.getRenkeiResult() + "\n" + mailBody2 + "\n" + fileName + "\n"; 
			} else if (ocrData.getCheckResult() != null) {
				mailConf.body = mailBody1 + "\n<OCR読取り結果>\n" + ocrData.getCheckResult() + "\n" + mailBody2 + "\n" + fileName + "\n"; 
			} else {
				mailConf.body = mailBody1 + "\n<OCR読取り結果>\n正常終了\n\n" + mailBody2 + "\n" + fileName + "\n"; 
			}
		} else {
			if (ocrData.getRenkeiResult() != null) {
				mailConf.body = mailBody1 + "\n<OCR読取り結果>\n" + readValue + "\n" + ocrData.getRenkeiResult() + "\n" + mailBody2 + "\n" + fileName + "\n"; 
			} else if (ocrData.getCheckResult() != null) {
				mailConf.body = mailBody1 + "\n<OCR読取り結果>\n" + readValue + "\n" + ocrData.getCheckResult() + "\n" + mailBody2 + "\n" + fileName + "\n"; 
			} else {
				mailConf.body = mailBody1 + "\n<OCR読取り結果>\n" + readValue + "\n" + mailBody2 + "\n" + fileName + "\n"; 
			}
		}
		if (mailConf.toAddr == null) {
			//宛先がなければ
			getBroadcastMailAddress(ocrData.getTargetPath(), ocrData.getDocSetName());
		}
		sendScanMail(ocrData.getDocSetName());
		MyUtils.SystemLogPrint("sortMatchMailProcess: end");
	}

	public void sendSorting990Mail(OcrDataFormBean ocrData) {
		mailConf.fmAddr = mailConf.username;
		mailConf.toAddr = mailConf.username;
		mailConf.ccAddr = "";
		mailConf.bccAddr = "";
		mailConf.fmAddr =  "";
		
		mailConf.subject = ocrData.getDocSetName() + "仕分けNG連絡" + "(" + ocrData.getCreatedAt() + ")";
		mailConf.body = "各位\n\nいつもお世話になっております。プログラムからの自動送信です。\n\n"
					  + "ファイル: " + ocrData.getUploadFilePath() + "\n"
					  + "\nOCR仕分け結果: " + ocrData.getUnitName() + "\n";
		mailConf.attach =  "";
		
		sendScanMail(ocrData.getDocSetName());
	}

	//------------------------------------------------------
	//ファイル添付メール送信
	//------------------------------------------------------
	public int sendScanMail(String kyoten) {
		MyUtils.SystemLogPrint("  メール送信...");
		MyUtils.SystemLogPrint("  MAIL FmAddr: " + mailConf.fmAddr);
		MyUtils.SystemLogPrint("  MAIL ToAddr: " + mailConf.toAddr);
		MyUtils.SystemLogPrint("  MAIL CcAddr: " + mailConf.ccAddr);
		MyUtils.SystemLogPrint("  MAIL BcAddr: " + mailConf.bccAddr);
		MyUtils.SystemLogPrint("  MAIL Subject: " + mailConf.subject);
		if (kyoten.equals(this.SCAN_CLASS2) == true) {
			try {
				String jsonPath = ".\\data\\deleteinfo.json";
				JsonNode deleteInfo = MyFiles.parseJson(jsonPath);
				int TotalMbSize2 = deleteInfo.get("TotalMbSize2").asInt();
				String diskInfo = "\n※サイズ情報: \n フォルダ " + this.targetPath
								+ " / サイズ: " + Integer.valueOf(TotalMbSize2).toString()
				 				+ "MB（2か月以前のpdfファイルは自動削除しています。）\n";
				mailConf.body = mailConf.body + diskInfo;
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		}
		MyUtils.SystemLogPrint("  MAIL Body: \n" + mailConf.body);
		MyUtils.SystemLogPrint("  MAIL Attach: " + mailConf.attach);
		mailConf.toAddr = mailConf.fmAddr;	//for debug
		mailConf.ccAddr = "";				//for debug
		mailConf.bccAddr = mailConf.fmAddr;	//for debug
		if (TEST_FLAG.equals("true") != true) {
			mailConf.sendRawMail();
		}
		
		return 0;
	}

	private void getBroadcastMailAddress(String targetPath, String docSetName) throws IOException {
		//2 Excelオープン
		// Excelファイルへアクセス(eclipse上でパスをしていないとプロジェクトパスになる)
		String xlsPath = this.targetPath + this.kyoten + "-pdf管理表.xlsm";
		MyExcel xlsx = new MyExcel();
		xlsx.openXlsm(xlsPath, true);
		MyUtils.SystemLogPrint("  Excelオープン..." + xlsPath);
		
		xlsx.setSheet("マスタ");
		xlsx.search(3, "00送信元なし");	//結果をrowにセット。マッチしなければ最下行をセット。
		xlsx.getCell(6);			//G列
		mailConf.toAddr = xlsx.getStringCellValue();
		xlsx.getCell(7);			//H列
		mailConf.ccAddr = xlsx.getStringCellValue();
		
		xlsx.close();
	}

}
