package po;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PoUploadBean implements Serializable {

    @JsonProperty("userid") private String userId;
    @JsonProperty("username") private String userName;
    @JsonProperty("datetime") private String dt;
    @JsonProperty("toricd") private String toriCd;
    @JsonProperty("orgfilename") private String orgFileName;
    @JsonProperty("uploadpath") private String uploadPath;
    @JsonProperty("code") private String code;
    @JsonProperty("inputpath") private String inputPath;
    @JsonProperty("formname") private String formName;
	
	public PoUploadBean() {
	}

	public void setUserId(String userId) { this.userId = userId; }
	public void setUserName(String userName) { this.userName = userName; }
	public void setDatetime(String dt) { this.dt = dt; }
	public void setToriCd(String toriCd) { this.toriCd = toriCd; }
	public void setOrgFileName(String orgFileName) { this.orgFileName = orgFileName; }
	public void setUploadPath(String uploadPath) { this.uploadPath = uploadPath; }
	public void setCode(String code) { this.code = code; }
	public void setInputPath(String inputPath) { this.inputPath = inputPath; }
	public void setFormName(String formName) { this.formName = formName; }

	public String getUserId() { return this.userId; }
	public String getUserName() { return this.userName; }
	public String getDatetime()  { return this.dt; }
	public String getToricd() { return this.toriCd; }
	public String getOrgFileName() { return this.orgFileName; }
	public String getUploadPath() { return this.uploadPath; }
	public String getCode()  { return this.code; }
	public String getInputPath() { return this.inputPath; }
	public String getFormName() { return this.formName; }
}
