package com.example.demo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class MenuList {
	public static ArrayList<String[]> items;
	//最後(固定メニュー)
	static String[][] constItem = {
		{"/logout","ログアウト"},
		{"javascript:btnClick()","閉じる"}
	};
    
	public static ArrayList<String[]> getItems(
		String project, Set<String> href, Set<String> name) {
		if (items == null) {
			items = new ArrayList<String[]>();
			String[] item = null;
			Iterator<String> itr1 = href.iterator();
			Iterator<String> itr2 = name.iterator();
			System.out.println("MenuList");
			String prefix = (project.equals("") == true)? "":"/" + project;
			//登録メニュー追加
			while (itr1.hasNext()) {
				item = new String[2];
				item[0] = prefix + itr1.next();	//href
				item[1] = itr2.next();			//title
				System.out.println(item[0] + ":" + item[1]);
				items.add(item);
			}
			//固定メニュー追加
			constItem[0][0] = prefix + constItem[0][0];
			System.out.println(constItem[0][0] + ":" + constItem[0][1]);
			items.add(constItem[0]);
			System.out.println(constItem[1][0] + ":" + constItem[1][1]);
			items.add(constItem[1]);
		}
		return items;
    }
}
