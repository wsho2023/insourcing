package common.po;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PoErrlBean implements Serializable {
    @JsonProperty("errlId") String errlId;
    @JsonProperty("createdDate") String createdDate;
    @JsonProperty("createdBy")	String createdBy;
    @JsonProperty("toriMei") String toriMei;
    @JsonProperty("poList") String poList;
        
	public PoErrlBean() {
	}

	public void setErrlId(String errlId)  { this.errlId = errlId;	}
	public void setCreatedDate(String createdDate) {this.createdDate = createdDate;	}
	public void setCreatedBy(String createdBy) {this.createdBy = createdBy;	}
	public void setToriMei(String toriMei)  { this.toriMei = toriMei;	}
	public void setPoList(String poList)  {this.poList = poList;}
    public void setErrlData(String errlId, String createdDate, String createdBy, String toriMei, String poList) {
        this.errlId = errlId; this.createdDate = createdDate; this.createdBy = createdBy; this.toriMei = toriMei; this.poList = poList; }
	
	public String getErrlId()  { return this.errlId;}
	public String getCreatedDate() { return this.createdDate;	}
	public String getCreatedBy() { return this.createdBy;	}
	public String getToriMei() { return this.toriMei;	}
	public String getPoList()  { return this.poList;}
}
