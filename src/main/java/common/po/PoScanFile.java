package common.po;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;

import com.example.demo.SpringConfig;

import common.utils.MyFiles;
import common.utils.MyUtils;

public class PoScanFile {
	SpringConfig config;
	String targetPath;

	public PoScanFile(SpringConfig argConfig, String argTargetPath) {
		MyUtils.SystemLogPrint("■PoScanFileコンストラクタ");
		config = argConfig;
		this.targetPath = argTargetPath;
	}
	
	public String scanGetTargetPath() {
		return this.targetPath;
	}
	
	public void scanRemainedFile() {
		//指定ディレクトリ配下のファイルのみ(またはディレクトリのみ)を取得
        File file = new File(this.targetPath);
        File fileArray[] = file.listFiles();
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
	
	public void scanProcess(String uploadFilePath) {
		MyUtils.SystemLogPrint("■scanProcess: start... 取込開始");
		//importData(uploadFilePath);
		if (uploadFilePath.equals(config.getOcrUploadPath1())==true) {
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
		} else if (uploadFilePath.equals(config.getOcrUploadPath2())==true) {
			
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

	void importData(String uploadFilePath) {
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
				return;
			}
		} 
		
		String userId = upload.getUserId();
		String datetime = upload.getDatetime();
		String triCd = upload.getToricd();
		String code = upload.getCode();
		String inputPath = upload.getInputPath();
		//⇒引数として渡す
		MyUtils.SystemLogPrint("  userId: " + userId + "  datetime: " + datetime + "  triCd: " + triCd + "  code: " + code + "  inputPath: " + inputPath); 
	}
}
