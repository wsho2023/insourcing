package fax;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import utils.MyFiles;
import utils.MyUtils;

public class FaxDeleteFile {
	public String targetPath;
	public String backupPath;
	public int daysOfDeletion;
	public long deleteCount;
	public long totalCount;
	public long deleteSize;
	public long totalSize;
	public int totalMbSize;
	public int deleteMbSize;
	
	public FaxDeleteFile(String argTargetPath, String argBackupPath, int argDays) {
		targetPath = argTargetPath;
		backupPath = argBackupPath;
		daysOfDeletion = argDays;
		deleteCount = 0;
		totalCount = 0;
		deleteSize = 0;
		totalSize = 0;
		totalMbSize = 0;
		deleteMbSize = 0;
	}
	
	public void run() {
		MyUtils.SystemLogPrint("「" + targetPath + "」フォルダ内のファイル更新日が"
							+ daysOfDeletion + "日以上のものを削除します。");
		scanDeleteFile(targetPath, targetPath + backupPath, true);
		MyUtils.SystemLogPrint("「" + targetPath + "」フォルダ内のファイル更新日"
				+ daysOfDeletion + "日以上のものを削除しました(ファイル数: " + deleteCount +")。");
		totalCount = totalCount - deleteCount;
		totalSize = totalSize - deleteSize;
		totalMbSize = Math.round(totalSize/(1024*1024));
		MyUtils.SystemLogPrint("トータルファイル数:" + totalCount + " 削除ファイル数: " + deleteCount
				+ " サイズ(MB): " + totalMbSize);
		
		DeleteInfo info = new DeleteInfo();
		info.TotalMbSize2 = totalMbSize;
		info.TotalCount2 = totalCount;
		
		//書き込み
		try {
			MyFiles.WriteJson2File(info, ".\\data\\deleteinfo.json");
			//MyFiles.WriteString2File(jsonStr, ".\\data\\deleteinfo.json");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//読み込み
		try {
			String jsonPath = ".\\data\\deleteinfo.json";
			JsonNode deleteInfo = MyFiles.parseJson(jsonPath);
			int TotalMbSize2 = deleteInfo.get("TotalMbSize2").asInt();
			String diskInfo = "\n※サイズ情報: \n フォルダ " + this.targetPath
							+ " / サイズ: " + Integer.valueOf(TotalMbSize2).toString()
			 				+ "MB（2か月以前のpdfファイルは自動削除しています。）\n";
			MyUtils.SystemLogPrint("  diskInfo: \n" + diskInfo);
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	private void scanDeleteFile(String targetPath, String backupPath, boolean deleteFlag) {
		//指定ディレクトリ内のファイルのみ(またはディレクトリのみ)を取得
        File file = new File(targetPath);
        File fileArray[] = file.listFiles();
        
        for (File f: fileArray) {
            // フォルダ
            if (f.isDirectory()) {
                System.out.println(f.toString());//フォルダを表示
                scanDeleteFile(f.toString(), backupPath, deleteFlag);
            }
            
            // ファイル
            if (f.isFile()) {
                //MyUtils.SystemLogPrint(f.toString());//ファイルを表示
                String filePath = f.toString();
                String extension = filePath.substring(filePath.length()-3);	//拡張子：後ろから3文字
                if (extension.equals("pdf") == true) {
                    totalSize = totalSize + f.length();
                    totalCount++;
					//https://qiita.com/fumikomatsu/items/b98cc4d0dee782323096
                	// 現在日時を取得
                    Calendar st = Calendar.getInstance();	//Calendarクラスで現在日時を取得
                    st.add(Calendar.DATE, -daysOfDeletion);	//現在値を取得(n日前)
                    Date start = st.getTime();           	//Dateに変換
                    // ファイルの更新日時
                    Long lastModified = f.lastModified();
                    Date koushin = new Date(lastModified);	//Dateに変換
                    
                    if (start.compareTo(koushin) > 0) {		//compareToで比較
                        String update_time = MyUtils.sdf.format(koushin);
	                    deleteSize = deleteSize + f.length();
	                    deleteCount++;
                        if (deleteFlag == true) {
                        	System.out.println("delete... " + update_time+"： " + filePath);
	                        try {
	                        	MyFiles.delete(filePath);
	                        } catch (IOException e) {
	                        	e.printStackTrace();
	                        }
                        }
                    }
                }
            }
        }
	}
}

class DeleteInfo {
	@JsonProperty("UpdatedDate") String UpdatedDate;
    @JsonProperty("TotalMbSize1") int TotalMbSize1;
    @JsonProperty("TotalCount1") long TotalCount1;
    @JsonProperty("TotalMbSize2") int TotalMbSize2;
    @JsonProperty("TotalCount2") long TotalCount2;

	DeleteInfo () {
		UpdatedDate = MyUtils.sdf.format(new Date());
		TotalMbSize1 = 0;
		TotalCount1 = 0;
		TotalMbSize2 = 0;
		TotalCount2 = 0;
	}
}
