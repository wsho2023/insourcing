package com.example.demo;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import common.ocr.OcrProcess;
import common.utils.MyFiles;
import common.utils.MyUtils;

@SpringBootApplication
@EnableAsync
public class InsourcingApplication implements CommandLineRunner {

    @Autowired
    private InsourcingConfig config;
    @Autowired
    private PoScanService poScanService;
    @Autowired
    private FaxScanService faxScanService1;
    @Autowired
    private FaxScanService faxScanService2;
    private boolean ocrExlusiveFlag;

	public static void main(String[] args) {
		SpringApplication.run(InsourcingApplication.class, args);
	}
	 
    @Override
    public void run(String... strings) throws Exception {
    	faxScanService1.run(config, config.getScanDefTgt1());
		Thread.sleep(1000);
		faxScanService2.run(config, config.getScanDefTgt2());
		Thread.sleep(1000);
		poScanService.run(config);
		OcrProcess process = new OcrProcess(config);
        Timer timer = new Timer(); // 今回追加する処理
        ocrExlusiveFlag = false;
        TimerTask task = new TimerTask() {
            int count = 0;
            public void run() {
            // 定期的に実行したい処理
            count++;
            //MyUtils.SystemLogPrint(count + "回目のタスクが実行されました。");
            if (ocrExlusiveFlag == true ) {
            	MyUtils.SystemLogPrint("wait...");
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
    	}};
        timer.schedule(task, 0, 30000); // 30000ms間隔
   	}
}
