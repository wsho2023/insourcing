package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import common.api.ApiObjInfo;
import common.api.BakObjInfo;
import common.api.DbObjInfo;
import common.api.GaihiObjInfo;
import common.api.ShukeiObjInfo;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class ApiController {

    @Autowired
    private ApiConfig config;

    //----------------------------------------------------------------------
	final String serv1 = "/api/kkk";
    @PostMapping(serv1)
    public String serv1Post(@RequestParam("obj") String obj) {
    //----------------------------------------------------------------------
    	ApiObjInfo objInfo = new ApiObjInfo(config, serv1, obj);
    	String msg = objInfo.makeObject();
		if (msg != null) return msg;
        
        msg = objInfo.execute();
		if (msg != null) return msg;
		
        return "OK";
    }
    
    //----------------------------------------------------------------------
	final String serv2 = "/api/hantei";
    @PostMapping(serv2)
    public String serv2Post(@RequestParam("obj") String obj) {
    //----------------------------------------------------------------------
    	ApiObjInfo objInfo = new ApiObjInfo(config, serv2, obj);
    	String msg = objInfo.makeObject();
		if (msg != null) return msg;
        
        msg = objInfo.execute();
		if (msg != null) return msg;
		
        return "OK";
    }
    
    //----------------------------------------------------------------------
	final String serv3 = "/api/kyaku";
    @PostMapping(serv3)
    public String serv3Post(@RequestParam("obj") String obj) {
    //----------------------------------------------------------------------
    	ApiObjInfo objInfo = new ApiObjInfo(config, serv3, obj);
    	String msg = objInfo.makeObject();
		if (msg != null) return msg;
        
        msg = objInfo.execute();
		if (msg != null) return msg;
		
        return "OK";
    }
    
    //----------------------------------------------------------------------
    final String db = "/api/db";
    @PostMapping(db)
    public String dbPost(@RequestParam("obj") String obj) {
    //----------------------------------------------------------------------
        DbObjInfo objInfo = new DbObjInfo(config, db, obj);
    	String msg = objInfo.makeObject();
		if (msg != null) return msg;
        
        msg = objInfo.execute();
		if (msg != null) return msg;
		
        return "OK";
    }
    
    //----------------------------------------------------------------------
    final String shukei = "/api/shukei";
    @PostMapping(shukei)
    public String shukeiPost(@RequestParam("obj") String obj) {
    //----------------------------------------------------------------------
    	ShukeiObjInfo objInfo = new ShukeiObjInfo(config, shukei, obj);
    	String msg = objInfo.makeObject();
		if (msg != null) return msg;
        
        msg = objInfo.execute();
		if (msg != null) return msg;
		
        return "OK";
    }
    
    //----------------------------------------------------------------------
    @GetMapping(shukei)
    public String shukeiGet(HttpServletResponse response, @RequestParam("obj") String obj) {
    //----------------------------------------------------------------------
    	ShukeiObjInfo objInfo = new ShukeiObjInfo(config, shukei, obj);
    	String msg = objInfo.makeObject();
		if (msg != null) return msg;
        
        msg = objInfo.download(response);
		if (msg != null) return msg;
		
        return "OK";
    }
    
    //----------------------------------------------------------------------
    final String backup = "/api/backup";
    @PostMapping(backup)
    public String backupPost(@RequestParam("obj") String obj) {
    //----------------------------------------------------------------------
    	BakObjInfo objInfo = new BakObjInfo(config, backup, obj);
    	String msg = objInfo.makeObject();
		if (msg != null) return msg;
        
        msg = objInfo.execute();
		if (msg != null) return msg;
		
    	return "OK";
    }
    
    //----------------------------------------------------------------------
    final String gaihi = "/api/gaihi";
    @PostMapping(gaihi)
    //public String gaihiPost(@RequestParam("obj") String obj) {
    public String gaihiPost() {
    //----------------------------------------------------------------------
    	GaihiObjInfo objInfo = new GaihiObjInfo(config, gaihi, null);
    	String msg = objInfo.makeObject();
		if (msg != null) return msg;
        
        msg = objInfo.execute();
		if (msg != null) return msg;
		
    	return "OK";
    }
	    
    //テストファイルダウンロード
    //curl -X GET "http://localhost:8080/daicho"
    /* @GetMapping("daicho")
    public void apiDaicho(HttpServletResponse response){
		ArrayList<OcrDaichoBean> list;
		try {
	        list = OcrRirekiDAO.getInstance(config).getDaicho();
	        
			response.setContentType("text/tsv;charset=UTF8");
			String fileName = new String("daicho.tsv".getBytes("UTF-8"), "ISO-8859-1");
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
    }*/    
}
