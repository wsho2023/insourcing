package common.po;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PoErrlBean implements Serializable {
    @JsonProperty("errlId") String errlId;
    @JsonProperty("createdDate") String createdDate;
    @JsonProperty("createdBy")	String createdBy;
    @JsonProperty("toriMei") String toriMei;
    @JsonProperty("poList") String poList;
    @JsonProperty("subject") String subject;
    @JsonProperty("type") int type;
    @JsonProperty("uploadPath") String uploadPath;
	@JsonProperty("fileName")	String fileName;
        
	public PoErrlBean() {
	}

	public void setErrlId(String errlId)  { this.errlId = errlId;	}
	public void setCreatedDate(String createdDate) {this.createdDate = createdDate;	}
	public void setCreatedBy(String createdBy) {this.createdBy = createdBy;	}
	public void setToriMei(String toriMei)  { this.toriMei = toriMei;	}
	public void setPoList(String poList)  {this.poList = poList;}
	public void setSubject(String subject)  {this.subject = subject;}
	public void setType(int type)  {this.type = type;}
	public void setUploadPath(String uploadPath)  {this.uploadPath = uploadPath;}
    public void setErrlData(String errlId, String createdBy, String toriMei, String poList, String subject, int type, String uploadPath) {
        this.createdBy = createdBy; this.toriMei = toriMei; this.poList = poList; 
        this.type = type; this.uploadPath = uploadPath; 
    }
	public void setFileName(String fileName)  {	this.fileName = fileName;	}
	
	public String getErrlId()  { return this.errlId;}
	public String getCreatedDate() { return this.createdDate;	}
	public String getCreatedBy() { return this.createdBy;	}
	public String getToriMei() { return this.toriMei;	}
	public String getPoList()  { return this.poList;}
	public String getSubject()  { return this.subject;	}
	public int getType()  { return this.type;	}
	public String getUploadPath()  {	return this.uploadPath;	}
	public String getFileName()  {	return this.fileName;	}
}
