package common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyUtils {
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	//public MyUtils() {
	//}
	public static void SystemLogPrint(String msg) {
        System.out.printf("[%s]%s\n", sdf.format(new Date()), msg);
	}

	public static void SystemErrPrint(String msg) {
        System.err.printf("[%s]%s\n", sdf.format(new Date()), msg);
	}
	
	public static String getDate() {
		return sdf.format(new Date());
	}

	public static String getDateStr() {
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		return sdf2.format(new Date());
	}
}
