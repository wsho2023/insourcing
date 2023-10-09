package com.example.demo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

//https://hosochin.com/2020/04/08/post-261/
@Service
public class MenuService {

	//https://qiita.com/rubytomato@github/items/d86039eca031ac1ed511
	@Value("${spring.common.project}")
	private String project;
	@Value("${spring.menu.href}")
	private Set<String> href;
	@Value("${spring.menu.title}") 
	private Set<String> name;
	
	//public MenuService(String menuProject, Set<String> menuHref, Set<String> menuName) {
	//	this.menuProject = menuProject;
	//	this.menuHref = menuHref;
	//	this.menuName = menuName;
	//}
	
    private ArrayList<String[]> items;
	//最後(固定メニュー)
	String[][] constItem = {
		{"/logout","ログアウト"},
		{"javascript:btnClick()","閉じる"}
	};
	
	public String getProject() {
		return (project.equals("") == true)? "":"/" + project;
	}
	
	public ArrayList<String[]> getItems() {
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
