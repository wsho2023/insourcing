package common.ocr;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OcrErrlBean implements Serializable {
    @JsonProperty("createddate") String createdDate;
    @JsonProperty("createdby")	String createdBy;
    @JsonProperty("polist") String poList;
    //@JsonProperty("docsetId")	String docsetId;
    @JsonProperty("docsetName") String docsetName;
        
	public OcrErrlBean() {
	}

	public void setCreatedDate(String createdDate) {this.createdDate = createdDate;	}
	public void setCreatedBy(String createdBy) {this.createdBy = createdBy;	}
	public void setPolist(String polist)  {this.poList = polist;}
	
	public String getCreatedDate() { return this.createdDate;	}
	public String getCreatedBy() { return this.createdBy;	}
	public String getPolist()  { return this.poList;}
}
