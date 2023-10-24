package com.example.demo;

import java.io.IOException;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import common.ocr.OcrProcess;
import common.utils.MyFiles;
import common.utils.MyUtils;

@Service
@EnableScheduling
public class OcrProcService {
    
	OcrProcess process;
    boolean ocrExlusiveFlag;
    int count;
    String watchdogPath;
    
	public void run(SpringConfig config) {
		ocrExlusiveFlag = false;
		count = 0;
		watchdogPath = config.getScanPath2() + "data\\watchdog.dat";
		process = new OcrProcess(config);
	}
	
	@Scheduled(fixedRate = 30000)	// 30000ms間隔
	public void runOcrProcess() {
		if (process == null) {
			//MyUtils.SystemErrPrint("wait... Ocr Initialization");
			return;
		}
	    // 定期的に実行したい処理
	    count++;
	    MyUtils.SystemLogPrint(count + "回目のタスクが実行されました。");
	    if (ocrExlusiveFlag == true ) {
	    	MyUtils.SystemErrPrint("wait... Ocr ExlusiveFlag");
	    	return;
	    }
	    ocrExlusiveFlag = true;
	    process.pollingReadingUnit();
	    ocrExlusiveFlag = false;
	    //---------------------------------
	    //watchdog 書き込み処理
		try {
			String str = MyUtils.getDate()+"\n" + count+"\n";
			MyFiles.writeString2File(str, watchdogPath);
		} catch (IOException e){
			MyUtils.SystemErrPrint(e.toString());
		}                
	    //---------------------------------
	} 
}
