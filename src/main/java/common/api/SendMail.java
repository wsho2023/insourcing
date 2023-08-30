package common.api;

import com.example.demo.ApiConfig;

import common.utils.MyMail;
import common.utils.MyUtils;

public class SendMail {
	MyMail mailConf;
	
	public SendMail(ApiConfig config) {
		mailConf = new MyMail();
		mailConf.host = config.getMailHost();
		mailConf.port = config.getMailPort();
		mailConf.username = config.getMailUsername();
		mailConf.password = config.getMailPassword();
		mailConf.smtpAuth = config.getMailSmtpAuth();
		mailConf.starttlsEnable = config.getMailSmtpStarttlsEnable();
	}
	
	//------------------------------------------------------
	//ファイル添付メール送信
	//------------------------------------------------------
	public int execute(String subject, String body, String attach) {
        mailConf.fmAddr = mailConf.username;
        mailConf.toAddr = mailConf.fmAddr;	//for debug
		mailConf.ccAddr = "";				//for debug
		mailConf.bccAddr = "";				//for debug
		mailConf.subject = subject;
		mailConf.body = body;
		mailConf.attach = attach;
		
		MyUtils.SystemLogPrint("  メール送信...");
		System.out.println("  MAIL FmAddr: " + mailConf.fmAddr);
		System.out.println("  MAIL ToAddr: " + mailConf.toAddr);
		System.out.println("  MAIL CcAddr: " + mailConf.ccAddr);
		System.out.println("  MAIL BcAddr: " + mailConf.bccAddr);
		System.out.println("  MAIL Subject: " + mailConf.subject);
		System.out.println("  MAIL Body: \n" + mailConf.body);
		System.out.println("  MAIL Attach: " + mailConf.attach);
		mailConf.sendRawMail();
		
		return 0;
	}
}
