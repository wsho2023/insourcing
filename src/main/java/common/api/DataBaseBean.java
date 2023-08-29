package common.api;

import java.util.ArrayList;

public class DataBaseBean {

	public ArrayList<String> data;

	//DataBaseBean(int colNum) {
	//}
	
	//タブ区切り1行文字列を作成
	public String getDataTsv() {
		String retStr = "";

		for (int c=0; c<data.size()-1; c++) {
			retStr = retStr + data.get(c) + "\t";
		}
		retStr = retStr + data.get(data.size()-1) + "\r\n";

		return retStr;
	}
}
