package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class InsourcingApplication implements CommandLineRunner {
	final boolean webOnly = false;
	
    @Autowired
    private SpringConfig config;
    @Autowired
    private MenuService menuService;
    @Autowired
    private FaxScanService faxScanService1;
    @Autowired
    private FaxScanService faxScanService2;
    @Autowired
    private PoScanService poScanService;
    @Autowired
    private OcrProcService ocrProcService;
    
    public static void main(String[] args) {
		SpringApplication.run(InsourcingApplication.class, args);
	}
	 
    @Override
    public void run(String... strings) throws Exception {
    	if (menuService.getWebOnly() == false) {
	    	faxScanService1.run(config, config.getScanDefTgt1());
			Thread.sleep(1000);
			faxScanService2.run(config, config.getScanDefTgt2());
			Thread.sleep(1000);
			poScanService.run(config, config.getOcrUploadPath1());
			Thread.sleep(1000);
			ocrProcService.run(config);
    	}
    }
    
}
