package com.example.demo;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import common.fax.FaxDataBean;
import common.fax.FaxDataDAO;
import common.fax.FaxDeleteFile;
import common.ocr.OcrColmunsBean;
import common.ocr.OcrDaichoBean;
import common.ocr.OcrDaichoDAO;
import common.ocr.OcrDataFormBean;
import common.ocr.OcrDataFormDAO;
import common.ocr.OcrRirekiBean;
import common.ocr.OcrRirekiDAO;
import common.po.PoErrlBean;
import common.po.PoErrlDAO;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class OcrController {
    @Autowired
    private SpringConfig config;
    
    private ArrayList<String[]> menuList;
    //https://qiita.com/rubytomato@github/items/d86039eca031ac1ed511
    @Value("${spring.menu.project}")
    private String menuProject;
    @Value("${spring.menu.href}")
    private Set<String> menuHref;
    @Value("${spring.menu.title}") 
    private Set<String> menuName;
    
    @GetMapping("/fax/{url}")
    public String faxGet(Model model, @PathVariable("url") String url, 
    		@RequestParam(name="form", required = false) String form, 
   			@RequestParam(name="date_fr", required = false) String date_fr, 
   			@RequestParam(name="date_to", required = false) String date_to, 
   			@RequestParam(name="soushimotonashi", required = false) String soushimotonashi) {
    	String kyoten;
    	if (url.equals(config.getScanDefUrl1())==true)
    		kyoten = config.getScanDefTgt1();
    	else if (url.equals(config.getScanDefUrl2())==true)
    		kyoten = config.getScanDefTgt2();
    	else
    		kyoten = config.getScanDefTgt2();
		String title = kyoten + "管理表";
        System.out.println("url: " + url);
        System.out.println("kyoten: " + kyoten);
		
		ArrayList<FaxDataBean> list;
        list = FaxDataDAO.getInstance(config).read(form, date_fr, date_to, soushimotonashi, kyoten);
		// 次の画面に値を渡す
		model.addAttribute("menu", getMenuList());
		model.addAttribute("url", url);
		model.addAttribute("title", title);
		model.addAttribute("list", list);
		
		// 次の画面に遷移
		return "faxlist";
    }
    
    private  ArrayList<String[]> getMenuList() {
		if (menuList == null) {
			menuList = new ArrayList<String[]>();
			String[] init = null;
			Iterator<String> itr1 = menuHref.iterator();
			Iterator<String> itr2 = menuName.iterator();
			System.out.println("MenuList");
			while (itr1.hasNext()) {
				init = new String[2];
				if (menuProject.equals("") == true) 
					init[0] = itr1.next();	//href
				else
					init[0] = "/" + menuProject + itr1.next();	//href
				init[1] = itr2.next();	//title
				System.out.println(init[0] + ":" + init[1]);
				menuList.add(init);
			}
			//最後
			init = new String[2];
			init[0] = "javascript:btnClick()";
			init[1] = "閉じる";
			System.out.println(init[0] + ":" + init[1]);
			menuList.add(init);
		}
		return menuList;
    }
    
    @PostMapping("/fax/{url}")
    public String faxPost(Model model, @PathVariable("url") String url, @RequestParam("form") String form, 
   			@RequestParam("date_fr") String date_fr, @RequestParam("date_to") String date_to, String soushimotonashi) {
    	String kyoten;
    	if (url.equals(config.getScanDefUrl1())==true)
    		kyoten = config.getScanDefTgt1();
    	else if (url.equals(config.getScanDefUrl2())==true)
    		kyoten = config.getScanDefTgt2();
    	else
    		kyoten = config.getScanDefTgt2();
		String title = kyoten + "管理表";
        System.out.println("url: " + url);
        System.out.println("kyoten: " + kyoten);
        System.out.println("form: " + form);
        System.out.println("date_fr: " + date_fr);
        System.out.println("date_to: " + date_to);
        System.out.println("soushimotonashi: " + soushimotonashi);
		
		ArrayList<FaxDataBean> list;
        list = FaxDataDAO.getInstance(config).read(form, date_fr, date_to, soushimotonashi, kyoten);
		// 次の画面に値を渡す
		model.addAttribute("menu", getMenuList());
		model.addAttribute("title", title);
		model.addAttribute("list", list);
		
		// 次の画面に遷移
		return "faxlist";
    }
    
    @GetMapping("/ocr/list")
    public String ocr(Model model){
		String title = "OCR結果一覧表";
		
		ArrayList<OcrDataFormBean> list = null;
        list = OcrDataFormDAO.getInstance(config).read(null, null, null);
		// 次の画面に値を渡す
		model.addAttribute("menu", getMenuList());
		model.addAttribute("title", title);
		model.addAttribute("list", list);
		
		// 次の画面に遷移
		return "ocrlist";
    }
    
    @PostMapping("/ocr/list")
    public String ocrPost(Model model, @RequestParam("form") String form, 
   			@RequestParam("date_fr") String date_fr, @RequestParam("date_to") String date_to) {
        System.out.println("form: " + form);
        System.out.println("date_fr: " + date_fr);
        System.out.println("date_to: " + date_to);

		String title = "OCR結果一覧表";
		ArrayList<OcrDataFormBean> list = null;
        list = OcrDataFormDAO.getInstance(config).read(form, date_fr, date_to);
		// 次の画面に値を渡す
		model.addAttribute("menu", getMenuList());
		model.addAttribute("title", title);
		model.addAttribute("list", list);
		
		// 次の画面に遷移
		return "ocrlist";
    }
    
    @GetMapping("/errl/list")
    public String errlGet(Model model){
		String title = "ERRL結果一覧表";
		
		ArrayList<PoErrlBean> list = null;
        list = PoErrlDAO.getInstance(config).read(null, null, null);
		// 次の画面に値を渡す
		model.addAttribute("menu", getMenuList());
		model.addAttribute("title", title);
		model.addAttribute("list", list);
		
		// 次の画面に遷移
		return "errllist";
    }
    
    @PostMapping("/errl/list")
    public String errlPost(Model model, @RequestParam("form") String form, 
   			@RequestParam("date_fr") String date_fr, @RequestParam("date_to") String date_to) {
        System.out.println("form: " + form);
        System.out.println("date_fr: " + date_fr);
        System.out.println("date_to: " + date_to);

		String title = "ERRL結果一覧表";
		ArrayList<PoErrlBean> list = null;
        list = PoErrlDAO.getInstance(config).read(form, date_fr, date_to);
		// 次の画面に値を渡す
		model.addAttribute("menu", getMenuList());
		model.addAttribute("title", title);
		model.addAttribute("list", list);
		
		// 次の画面に遷移
		return "errllist";
    }
    
    @GetMapping("/ocr/result")
    public String ocrResultGet(Model model, 
    		@RequestParam("type") int type, @RequestParam("unitId") String unitId) {
        //レスポンス
		System.out.println("  unitId: " + unitId + "  type: " + type);
		// 次の画面に値を渡す
		model.addAttribute("unitId", unitId);
		model.addAttribute("type", type);
		
		// 次の画面に遷移
    	return "result";
    }
    
    @PostMapping("/ocr/result")
    @ResponseBody	//＠ResponseBody アノテーションを付けることで、戻り値を HTTP レスポンスのコンテンツとすることができます。
    public Map<String, Object> ocrResultPost(
			@RequestParam("type") int type, @RequestParam("unitId") String unitId) {
        //レスポンス
		System.out.println("  unitId: " + unitId + "  type: " + type);
		
		ArrayList<OcrDataFormBean> dao = null;
        dao = OcrDataFormDAO.getInstance(config).queryWithUnitId(unitId);
		OcrDataFormBean ocrDataForm = (OcrDataFormBean)dao.get(0);
		//String status = ocrDataForm.getStatus();
		String createdAt = ocrDataForm.getCreatedAt();
		String formName = ocrDataForm.getName();
		String documentId = ocrDataForm.getDocumentId();
		String colOutput = ocrDataForm.getColOutput();
		int colSuryo = ocrDataForm.getColSuryo();
		int colTanka = ocrDataForm.getColTanka();
		int colKingaku = ocrDataForm.getColKingaku();
		String fields = "";
		int dataWidth = 0;
		if (colOutput.equals("") != true) {
			dataWidth = 10;
			fields = colOutput;
		} else {
			int headerNum = ocrDataForm.getHeaderNum();
			int meisaiNum = ocrDataForm.getMeisaiNum();

			dataWidth = headerNum + meisaiNum + 1;	//結果列分 ＋１
			for (int c=0; c<dataWidth; c++)
				fields = fields + "COL" + (c+3) + ",";
			fields = fields.substring(0, fields.length() - 1);	//末尾 , 削除
		}
		int col_width[] = new int[dataWidth];

		ArrayList<OcrColmunsBean> columns = new ArrayList<OcrColmunsBean>();
        try {
			OcrRirekiBean header = OcrRirekiDAO.getInstance(config).readHeader(fields, documentId);
			for (int j=0; j<dataWidth; j++) {
				int len = header.getCOL(j).getBytes("Shift_JIS").length;	//S-JIS文字長
				if (col_width[j] < len) {
					col_width[j] = len;
				}
			}

			//System.out.println("header width");
			//for (int i=0; i<dataWidth; i++) {
			//	System.out.print(i + ":" + col_width[i] + " ");	//各カラムの文字数
			//}
			//System.out.println("");

	        //Javaオブジェクトに値をセット
			ArrayList<OcrRirekiBean> datalist;
			String title;
			if (type == 0) {
				datalist = OcrRirekiDAO.getInstance(config).readDataUnit(fields, unitId, 0);	//unitId			 
				title = "OCR変換結果: " + formName + "@" + createdAt;
			} else {
				datalist = OcrRirekiDAO.getInstance(config).readDataUnit(fields, formName, 1);//unitName			 
				title = "OCR変換結果: " + formName;
			}
			String colValue;
			int len;
			for (int i=0; i<datalist.size(); i++) {
				OcrRirekiBean rireki = datalist.get(i);
				for (int j=0; j<dataWidth; j++) {
					colValue = rireki.getCOL(j);
					//System.out.println(j + ":" + rireki.getCOL(j));
					if (colValue != null) {
						len = colValue.getBytes("Shift_JIS").length;
						if (col_width[j] < len) {
							col_width[j] = len;
						}
					}
				}
			}
			//System.out.println("data width");
			//for (int i=0; i<dataWidth; i++) {
			//	System.out.print(i + ":" + col_width[i] + " ");	//各カラムの文字数
			//}
			System.out.println("");

			String width;
			OcrColmunsBean cols;
			for (int i=0; i<dataWidth; i++) {
				int colNo = i;
				if ((colSuryo != 0) && (colNo == colSuryo)) {
					width = Integer.valueOf(col_width[i]*11).toString();
					cols = new OcrColmunsBean(header.getCOL(i), "COL"+i, width, "numeric", "#,##", "right");
				} else if ((colTanka != 0) && (colNo == colTanka)) {
					width = Integer.valueOf(col_width[i]*11).toString();	
					cols = new OcrColmunsBean(header.getCOL(i), "COL"+i, width, "numeric", "#,##.00", "right");
				} else if ((colKingaku != 0) && (colNo == colKingaku)) {
					width = Integer.valueOf(col_width[i]*11).toString();
					cols = new OcrColmunsBean(header.getCOL(i), "COL"+i, width, "numeric", "#,##", "right");
				} else {
					//文字列
					width = Integer.valueOf(col_width[i]*10).toString();
					cols = new OcrColmunsBean(header.getCOL(i), "COL"+i, width, "text", "left");
				}

				columns.add(cols);
			}
	        ObjectMapper mapper = new ObjectMapper();
	        // 戻り値用のオブジェクト作成
	        Map<String, Object> resMap = new HashMap<>();
			resMap.put("title", title);
			resMap.put("columns", columns);
	        resMap.put("datalist", datalist);
	        try {
	            //JavaオブジェクトからJSONに変換
	        	String json = mapper.writeValueAsString(resMap);
	        	//String json = mapper.writeValueAsString(rireki);	//単体jsonのケース
	            //JSONの出力
	            System.out.println(json);
	            //return json;
	            return resMap;	//Stringで渡すのではなく、Objectで渡す。
	        } catch (JsonProcessingException e) {
	            e.printStackTrace();
	        }
            return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
            return null;
		}
        //return null;
    }
	
    @GetMapping("/daicho")
    public String getDaicho(Model model){
		String title = "台帳データリスト";
		
		ArrayList<OcrDaichoBean> list;
        list = OcrDaichoDAO.getInstance(config).read(null, null, null);
        
		// 次の画面に値を渡す
		model.addAttribute("menu", getMenuList());
		model.addAttribute("title", title);
		model.addAttribute("list", list);
		
		// 次の画面に遷移
		return "daicholist";
    }
	
    @PostMapping("/daicho")
    public String postDaicho(Model model, @RequestParam("form") String form, 
   			@RequestParam("date_fr") String date_fr, @RequestParam("date_to") String date_to) {
        System.out.println("form: " + form);
        System.out.println("date_fr: " + date_fr);
        System.out.println("date_to: " + date_to);

		String title = "台帳データリスト";
		ArrayList<OcrDaichoBean> list;
        list = OcrDaichoDAO.getInstance(config).read(form, date_fr, date_to);
		// 次の画面に値を渡す
		model.addAttribute("title", title);
		model.addAttribute("list", list);
		
		// 次の画面に遷移
		return "daicholist";
    }
	
    //curl -X POST http://localhost/fax/delete
    @PostMapping("/fax/delete")
    public String postFaxDelete() {
		
    	String scanPath = config.getScanPath2();
    	FaxDeleteFile delete = new FaxDeleteFile(scanPath, "", 60);
    	delete.run();
    	
    	return "post_ok";
    }
    
    @GetMapping("/api/daicho")
    public void getApiDaicho(HttpServletResponse response){
		ArrayList<OcrDaichoBean> list;
		try {
	        list = OcrDaichoDAO.getInstance(config).read(null, null, null);
	        
			response.setContentType("text/tsv;charset=UTF8");
			String fileName = new String("daicho.tsv".getBytes("Shift_JIS"), "ISO-8859-1");
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			PrintWriter writer = response.getWriter();
  			String line;
			//ヘッダ
			line = "ID\t日時\tOCR\tNo\t取引先\t日付\t送付先\t注番\t品名\t数量\t単価\t金額\t備考\r\n";
			writer.write(line);
			//データ
			for (int i=0; i<list.size(); i++) {
				OcrDaichoBean dto = (OcrDaichoBean)list.get(i);
	  			line = dto.getAllData();
	  			writer.write(line);
			}
			writer.close();
		} catch (IOException e) {
			list = null;
			e.printStackTrace();
		}	
    }
}
