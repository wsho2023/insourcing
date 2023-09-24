package com.example.demo;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import common.ocr.OcrProcess;
import common.utils.MyFiles;
import common.utils.MyUtils;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class InsourcingApplication implements CommandLineRunner {

    @Autowired
    private SpringConfig config;
    @Autowired
    private FaxScanService faxScanService1;
    @Autowired
    private FaxScanService faxScanService2;
    @Autowired
    private PoScanService poScanService1;
    @Autowired
    private PoScanService poScanService2;
    
	OcrProcess process;
    private boolean ocrExlusiveFlag = false;
    private int count = 0;
    
    public static void main(String[] args) {
		SpringApplication.run(InsourcingApplication.class, args);
	}
	 
    @Override
    public void run(String... strings) throws Exception {
    	faxScanService1.run(config, config.getScanDefTgt1());
		Thread.sleep(1000);
		faxScanService2.run(config, config.getScanDefTgt2());
		Thread.sleep(1000);
		poScanService1.run(config, config.getOcrUploadPath1());
		Thread.sleep(1000);
		poScanService2.run(config, config.getOcrUploadPath2());
		ocrExlusiveFlag = false;
		count = 0;
		process = new OcrProcess(config);
    }
    
	@Scheduled(fixedRate = 30000)	// 30000ms間隔
	public void runOcrProcess() {
		if (process == null) {
			MyUtils.SystemErrPrint("wait... Ocr Initialization");
			return;
		}
	    // 定期的に実行したい処理
	    count++;
	    //MyUtils.SystemLogPrint(count + "回目のタスクが実行されました。");
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
			String str = MyUtils.getDateStr()+"\n" + count+"\n";
			MyFiles.WriteString2File(str, ".\\data\\watchdog.dat");
		} catch (IOException e){
			MyUtils.SystemErrPrint(e.toString());
		}                
	    //---------------------------------
	}	 
}
