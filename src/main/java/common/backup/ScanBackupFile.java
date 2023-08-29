package common.backup;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import common.utils.MyFiles;
import common.utils.MyUtils;

public class ScanBackupFile {
	public String targetPath;
	public String backupPath;
	public int daysOfDeletion;
	public long deleteCount;
	public long totalCount;
	public long deleteSize;
	public long totalSize;
	public int totalMbSize;
	public int deleteMbSize;
	public String zipFilePath;
	ArrayList<String> deleteList;
	
	public ScanBackupFile(String argTargetPath, String argBackupPath, int argDays) {
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
		String zipFileName = MyFiles.getFileName(targetPath) + "_" + MyUtils.getDateStr() + ".zip";
		zipFilePath = backupPath + zipFileName;
        Charset charset = Charset.forName("MS932");
        deleteList = new ArrayList<String>();
        try {
			MyFiles.existsDelete(zipFilePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
        //https://itsakura.com/java-writezip
		try (
			FileOutputStream fos = new FileOutputStream(zipFilePath);
	        BufferedOutputStream bos = new BufferedOutputStream(fos);
			ZipOutputStream zos = new ZipOutputStream(bos, charset);
		) {
			System.out.println("「" + targetPath + "」フォルダ内のファイル更新日が"
					+ daysOfDeletion + "日以上のものを削除します。");
			scanDeleteFile(zos, targetPath, targetPath + backupPath, false);	//backup
			System.out.println("「" + targetPath + "」フォルダ内のファイル更新日"
					+ daysOfDeletion + "日以上のものを削除しました(ファイル数: " + deleteCount +")。");
			totalCount = totalCount - deleteCount;
			totalSize = totalSize - deleteSize;
			totalMbSize = Math.round(totalSize/(1024*1024));
			deleteMbSize = Math.round(deleteSize/(1024*1024));
			
			////backupPathへzipファイルを移動(targetPathへ作っていた場合移動)
			//String backFilePath = zipFilePath = backupPath + zipFileName;
			//MyFiles.moveOW(zipFilePath, backFilePath);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		//移動済みファイルをdelete
		for (String f: deleteList) {
			System.out.println("delete... " + f);
			try {
				MyFiles.delete(f);
			} catch (IOException e) {
				e.printStackTrace();	//アクセス不可ファイルは、失敗するけど、catchして無視
			}
		}
	}
	
	private void scanDeleteFile(ZipOutputStream zos, String targetPath, String backupPath, boolean deleteFlag) {
		//指定ディレクトリ内のファイルのみ(またはディレクトリのみ)を取得
        File file = new File(targetPath);
        File fileArray[] = file.listFiles();
        
        for (File f: fileArray) {
            // フォルダ
            if (f.isDirectory()) {
                System.out.println(f.toString());//フォルダを表示
                scanDeleteFile(zos, f.toString(), backupPath, deleteFlag);
            }
            
            // ファイル
            if (f.isFile()) {
                //MyUtils.SystemLogPrint(f.toString());//ファイルを表示
                String filePath = f.toString();
                //String extension = filePath.substring(filePath.length()-3);	//拡張子：後ろから3文字
                //if (extension.equals("tsv") == true) {
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
                        	System.out.println("delete... " + update_time + "： " + filePath);
	                        try {
	                        	MyFiles.delete(filePath);
	                        } catch (IOException e) {
	                        	e.printStackTrace();
	                        }
	                    } else {
	                        System.out.println("backup... " + update_time + "： " + filePath);
							try {
								Path p = Paths.get(filePath);	// 入力ファイルパス
								String fileName =  MyFiles.getFileName(filePath);
								byte[] fb = Files.readAllBytes(p);
	                    		ZipEntry zip = new ZipEntry(fileName);
	                    		zos.putNextEntry(zip);
	                    		zos.write(fb);
							} catch (IOException e) {
								e.printStackTrace();
							}
							deleteList.add(filePath);
	                    }
                    }
                }
            //}
        }
	}
}
	