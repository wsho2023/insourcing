package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import common.utils.MyMail;
import common.utils.MyUtils;

@Service
public class SendMailService {
	SimpleMailMessage msg;
	@Autowired
    MailSender mailSender;   
	@Value("${spring.mail.from}")
	String MAIL_FROM;
	
	public String from;
	public String to;
	public String cc;
	public String bcc;
	public String subject;
	public String body;
	public String attach;
	
	public SendMailService() {
		//https://itsakura.com/sb-mailsend
		//https://spring.pleiades.io/spring-framework/docs/current/javadoc-api/org/springframework/mail/SimpleMailMessage.html
        msg = new SimpleMailMessage();
		from = MAIL_FROM;
		to = "";
		cc = "";
		bcc = "";
	}
	
	@Async
	public void run(InsourcingConfig config, String userId, String dt, String toriCd, String fileName) {
		MyMail mailConf = new MyMail();
		mailConf.host = config.getMailHost();
		mailConf.port = config.getMailPort();
		mailConf.username = config.getMailUsername();
		mailConf.password = config.getMailPassword();
		mailConf.smtpAuth = config.getMailSmtpAuth();
		mailConf.starttlsEnable = config.getMailSmtpStarttlsEnable();

		mailConf.fmAddr = mailConf.username;
		mailConf.toAddr = userId;
		mailConf.ccAddr = "";
		mailConf.bccAddr = "";
		
		mailConf.subject = "アップロードされました。";
		mailConf.body = "ID: " + userId + "\n"
					  + "dt: " + dt  + "\n"
				   	  + "toriCd: " + toriCd + "\n"
				   	  + "file: " + fileName + "\n";
		//String attach = null;
		MyUtils.SystemLogPrint("  メール送信...");
		MyUtils.SystemLogPrint("  MAIL FmAddr: " + mailConf.fmAddr);
		MyUtils.SystemLogPrint("  MAIL ToAddr: " + mailConf.toAddr);
		MyUtils.SystemLogPrint("  MAIL CcAddr: " + mailConf.ccAddr);
		MyUtils.SystemLogPrint("  MAIL BcAddr: " + mailConf.bccAddr);
		MyUtils.SystemLogPrint("  MAIL Subject: " + mailConf.subject);
		MyUtils.SystemLogPrint("  MAIL Body: " + mailConf.body);
		mailConf.sendRawMail();
	}
	
	@Async
	public void run_new() {
        //msg.setFrom(from);
        msg.setTo(msg.getFrom());
        msg.setCc(cc);
        msg.setBcc(bcc);
        msg.setSubject(subject);               
        msg.setText(body);

        try {
            mailSender.send(msg);
        } catch (MailException e) {
            e.printStackTrace();
        }
        System.out.println("メール送信完了");
    }
}
