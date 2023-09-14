package com.example.demo;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("spring")
public class SpringConfig {
 
    private Map<String,String> datasource;
    private Map<String,String> mail;
    private Map<String,String> proxy;
    private Map<String,String> scan;
    private Map<String,String> ocr;
    private Map<String,String> path;
    
    public Map<String, String> getDatasource() {	return datasource;	}
    public Map<String, String> getMail() {	return mail;	}
    public Map<String, String> getProxy() {	return proxy;	}
    public Map<String, String> getScan() {	return scan;	}
    public Map<String, String> getOcr() {	return ocr;	}
    public Map<String, String> getPath() {	return path;	}

    public void setDatasource(Map<String, String> datasource) {	this.datasource = datasource;	}
    public void setMail(Map<String, String> mail) {	this.mail = mail;	}
    public void setProxy(Map<String, String> proxy) {	this.proxy = proxy;	}
    public void setScan(Map<String, String> scan) {	this.scan = scan;	}
    public void setOcr(Map<String, String> ocr) {	this.ocr = ocr;	}
    public void setPath(Map<String, String> path) {	this.path = path;	}
    
    public String getDBUrl(){	return datasource.get("url");	}
    public String getDBUsername(){	return datasource.get("username");    }
    public String getDBPassword(){	return datasource.get("password");    }
    public String getDBDriverClassName(){	return datasource.get("driverClassName");    }
    
    public String getMailHost(){	return mail.get("host");	}
    public String getMailPort(){	return mail.get("port");    }
    public String getMailUsername(){	return mail.get("username");    }
    public String getMailPassword(){	return mail.get("password");    }
    public String getMailSmtpAuth(){	return mail.get("properties.mail.smtp.auth");    }
    public String getMailSmtpStarttlsEnable(){	return mail.get("properties.mail.smtp.starttls.enable");	}
    public String getMailFrom(){	return mail.get("from");	}
    
    public String getProxyHost(){	return proxy.get("host");	}
    public String getProxyPort(){	return proxy.get("port");    }
    public String getProxyUsername(){	return proxy.get("username");    }
    public String getProxyPassword(){	return proxy.get("password");    }
      
    public String getScanTestFlag(){	return scan.get("test_flag");	}
    public String getScanDefTgt1(){	return scan.get("def_tgt1");	}
    public String getScanDefTgt2(){	return scan.get("def_tgt2");    }
    public String getScanPath1(){	return scan.get("path1");    }
    public String getScanPath2(){	return scan.get("path2");    }
    public String getScanFormType1(){	return scan.get("form_type1");    }
    public String getScanFormType2(){	return scan.get("form_type2");    }
    
    public String getOcrUploadPath(){	return ocr.get("upload_path");	}
    public String getOcrInputPath(){	return ocr.get("input_path");    }
    public String getOcrOutputPath(){	return ocr.get("output_path");    }
    public String getOcrHostUrl(){	return ocr.get("host_url");    }
    public String getOcrAddPage(){	return ocr.get("add_page");    }
    public String getOcrAddSort(){	return ocr.get("add_sort");    }
    public String getOcrReadUnit(){	return ocr.get("read_unit");    }
    public String getOcrReadSort(){	return ocr.get("read_sort");    }
    public String getOcrUnitExport(){	return ocr.get("unit_export");    }
    public String getOcrLinkEntry(){	return ocr.get("link_entry");    }
    public String getOcrApiKey(){	return ocr.get("api_key");    }
    public String getOcrApiKeyValye(){	return ocr.get("api_key_value");    }
    public String getOcrUserId(){	return ocr.get("user_id");    }
    
    public String getPathTempletePath(){	return path.get("templete_path");	}
	public String getPathInputPath(){	return path.get("input_path");    }
	public String getPathOutputPath(){	return path.get("output_path");    }
	
}