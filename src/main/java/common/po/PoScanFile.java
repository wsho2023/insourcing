package common.po;

import static java.nio.file.StandardWatchEventKinds.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import com.example.demo.InsourcingConfig;

import common.utils.MyFiles;
import common.utils.MyUtils;

public class PoScanFile implements Runnable {
	String targetPath;
	InsourcingConfig config;

	public PoScanFile(InsourcingConfig argConfig) {
		MyUtils.SystemLogPrint("■PoScanFileコンストラクタ");
		config = argConfig;
		this.targetPath = config.getOcrUploadPath();
	}
	
	//public static void main(String[] args) throws Exception {
	@Override
	public void run() {
		ScanRemainedFile(this.targetPath);	//すでにフォルダにあるpdfをScan
		//指定ディレクトリ配下のファイルのみ(またはディレクトリ)を取得
		// https://qiita.com/fumikomatsu/items/67f012b364dda4b03bf1
		try {
	        WatchService watcher;
			watcher = FileSystems.getDefault().newWatchService();
			Path dir = Paths.get(this.targetPath);
			dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
			MyUtils.SystemLogPrint("■PoScanFile: scan: " + this.targetPath);
	        for (;;) {
	            WatchKey watchKey = watcher.take();
	            for (WatchEvent<?> event: watchKey.pollEvents()) {
	                if (event.kind() == OVERFLOW) continue;
	                //新規作成
	                if (event.kind() == ENTRY_CREATE) {
	                    WatchEvent<Path> ev = cast(event);
	                    Path name = ev.context();		//ファイル名
	                    Path src = dir.resolve(name);	//フルパス
	                    String fileName = name.toString();
	                    System.out.format("%s: %s %s\n", event.kind().name(), src, name);
	                    //String extension = fileName.substring(fileName.lastIndexOf("."));	//
	                    //String extension = fileName.substring(fileName.length()-3);	//拡張子：後ろから3文字
	                    //if (extension.equals("pdf") == true) {
                    		MyUtils.SystemLogPrint("  ファイル検出...: " + fileName);
							scanProcess(src.toString());
						//}
	                }
	            }
	            watchKey.reset();
	        }
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }

	void ScanRemainedFile(String targetPath) {
		//指定ディレクトリ配下のファイルのみ(またはディレクトリのみ)を取得
        File file = new File(targetPath);
        File fileArray[] = file.listFiles();
        
		try {
			for (File f: fileArray){
				if (f.isFile()) {
					String fileName = MyFiles.getFileName(f.toString());	//フルパスからファイル名取得
					MyUtils.SystemLogPrint("  ファイル検出...: " + fileName);
					scanProcess(f.toString());
	            }
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	void scanProcess(String uploadFilePath) {
		importData(uploadFilePath);
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
    	MyUtils.SystemLogPrint("  ファイル移動: " + dstPath);
	}

	void importData(String uploadFilePath) {
		PoUploadBean upload = null;
		upload = PoUploadDAO.getInstance(config).quertyWithUploadPath(uploadFilePath);
		
		String userId = upload.getUserId();
		String datetime = upload.getDatetime();
		String triCd = upload.getToricd();
		String code = upload.getCode();
		String inputPath = upload.getInputPath();
		//⇒引数として渡す
	}
}
