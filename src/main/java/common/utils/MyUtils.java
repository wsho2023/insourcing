package common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

	public static String getToday() {
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
		return sdf2.format(new Date());
	}

	public static String getToday(int day) {
        //日時を格納するためのDateクラスを宣言(現在時刻)
        Date date = new Date();
		if (day != 0) {
	        Calendar calendar = Calendar.getInstance();
	        calendar.setTime(date);
	        //Date型の持つ日時を表示
	        //System.out.println(date);
	        //Date型の持つ日時の4年後を表示(日時の加算/減算)
	        calendar.add(Calendar.DAY_OF_MONTH, day);
	        date = calendar.getTime();
	        //System.out.println(date);
		}
		
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
		return sdf2.format(date);
	}
	
	public static String getDateStr() {
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		return sdf2.format(new Date());
	}
	
	//コマンド実行
	public static void exeCmd(String[] cmdList) throws IOException, InterruptedException {
		//https://blog.goo.ne.jp/xmldtp/e/beb03fb01fb1d1a2c37db8d69b43dcdd
		//コマンドラインから****.vbsを呼び出せる。
		System.out.print("  ");
		for (String cmd : cmdList)
			System.out.print(cmd + " ");
		System.out.print("\n");
		//https://qiita.com/mitsumizo/items/836ce2e00e91c33fcf95
		ProcessBuilder pb = new ProcessBuilder(cmdList);
		Process process = pb.start();
		System.out.println("  戻り値：" + process.waitFor());	//応答待ち
		try (BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream(), "MS932"))) {	//Charset.defaultCharset()
            String line;
            while ((line = r.readLine()) != null) {
                MyUtils.SystemLogPrint(line);
            }
        }
	}
}
