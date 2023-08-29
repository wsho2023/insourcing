package com.example.demo;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("api")
public class ApiConfig {
 
    private Map<String,String> mail;
    private Map<String,String> datasource;
    private Map<String,String> proxy;
    private Map<String,String> path;
    
    public Map<String, String> getMail() {	return mail;	}
    public Map<String, String> getDatasource() {	return datasource;	}
    public Map<String, String> getProxy() {	return proxy;	}
    public Map<String, String> getPath() {	return path;	}

    public void setMail(Map<String, String> mail) {	this.mail = mail;	}
    public void setDatasource(Map<String, String> datasource) {	this.datasource = datasource;	}
    public void setProxy(Map<String, String> proxy) {	this.proxy = proxy;	}
    public void setPath(Map<String, String> path) {	this.path = path;	}
    
    public String getMailHost(){	return mail.get("host");	}
    public String getMailPort(){	return mail.get("port");    }
    public String getMailUsername(){	return mail.get("username");    }
    public String getMailPassword(){	return mail.get("password");    }
    public String getMailSmtpAuth(){	return mail.get("smtp_auth");    }
    public String getMailSmtpStarttlsEnable(){	return mail.get("smtp_starttls_enable");    }
    
    public String getDBUrl(){	return datasource.get("url");	}
    public String getDBUsername(){	return datasource.get("username");    }
    public String getDBPassword(){	return datasource.get("password");    }
    public String getDBDriverClassName(){	return datasource.get("driverClassName");    }
    
    public String getProxyHost(){	return proxy.get("host");	}
    public String getProxyPort(){	return proxy.get("port");    }
    public String getProxyUsername(){	return proxy.get("username");    }
    public String getProxyPassword(){	return proxy.get("password");    }
      
    public String getPathTempletePath(){	return path.get("templete_path");	}
    public String getPathInputPath(){	return path.get("input_path");    }
    public String getPathOutputPath(){	return path.get("output_path");    }
    
}