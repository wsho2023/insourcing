package common.ocr;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;


public class OcrColmunsBean implements Serializable {
    @JsonProperty("title")	private String Title;
    @JsonProperty("name")	private String Name;
    @JsonProperty("width")	private String Width;
    @JsonProperty("type")	private String Type;
    @JsonProperty("mask")	private String Mask;
    @JsonProperty("align")	private String align;
    //@JsonProperty("decimal")	private String decimal;

	public OcrColmunsBean(String title, String name, String width, String type, String align) {
		this.Title = title;
		this.Name = name;
		this.Width = width;
		this.Type = type;
		this.Mask = null;
		this.align = align;
	}

	public OcrColmunsBean(String title, String name, String width, String type, String mask, String align) {
		this.Title = title;
		this.Name = name;
		this.Width = width;
		this.Type = type;
		this.Mask = mask;
		this.align = align;
	}
}
