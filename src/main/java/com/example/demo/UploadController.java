package com.example.demo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.login.SecuritySession;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import common.ocr.OcrDataFormBean;
import common.ocr.OcrDataFormDAO;
import common.ocr.OcrFormBean;
import common.ocr.OcrFormDAO;
import common.po.PoFormBean;
import common.po.PoFormDAO;
import common.po.PoUploadBean;
import common.po.PoUploadDAO;
import common.utils.MyMail;
import common.utils.MyUtils;

@Controller
public class UploadController {
    @Autowired
    private SpringConfig config;
    @Autowired
    private SecuritySession securitySession;
    @Autowired
    private MenuService menuService;
    
    @GetMapping("/login")
    public String login(Model model) {
		String title = "ログインページ";
		// 次の画面(jsp)に値を渡す
		model.addAttribute("title", title);
        return "login";
    }

	@GetMapping("/")
	public String getDefault(Model model){
		//トップページアクセス時のリダイレクト先
		return "redirect:/upload";
	}
	
	//ログイン成功しているのに、/error?continueにリダイレクトされる対処
	@GetMapping("/error")
	public String getError() {
	    return "redirect:/upload";
	}
    
    @GetMapping("/upload")
    public String uploadGet(Model model){
		String title = menuService.getTitle("/upload");
		String userId = securitySession.getUsername();
		String userName = securitySession.getName();
		String code = securitySession.getCode();

		// 次の画面に値を渡す
        model.addAttribute("menu", menuService.getItems());
		model.addAttribute("title", title);
		model.addAttribute("userId", userId);
		model.addAttribute("userName", userName);
		model.addAttribute("code", code);

		// 次の画面に遷移
		return "upload";
    }
     
    @PostMapping("/select")
    @ResponseBody	//＠ResponseBody アノテーションを付けることで、戻り値を HTTP レスポンスのコンテンツとすることができます。
    public ArrayList<PoFormBean> selectPost(@RequestParam("type") String type, @RequestParam("userId") String userId) {
        System.out.println("type: " + type + "  userId: " + userId + "  userName: " + securitySession.getName());
        //ログインIDをもとに、帳票定義リストを取得する。
		ArrayList<PoFormBean> select = PoFormDAO.getInstance(config).read(userId);
        ObjectMapper mapper = new ObjectMapper();
        try {
            //JavaオブジェクトからJSONに変換
            String json = mapper.writeValueAsString(select);
            //JSONの出力
            System.out.println(json);
            //out.write(json);
            return select;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    @PostMapping("/rireki")	//アップロード履歴
    @ResponseBody	//＠ResponseBody アノテーションを付けることで、戻り値を HTTP レスポンスのコンテンツとすることができます。
    public ArrayList<PoUploadBean> rirekiPost(@RequestParam("type") String type, @RequestParam("userId") String userId) {
        System.out.println("type: " + type + "  userId: " + userId);
	    //履歴リスト
		ArrayList<PoUploadBean> rireki = PoUploadDAO.getInstance(config).read(userId);
        ObjectMapper mapper = new ObjectMapper();
        try {
            //JavaオブジェクトからJSONに変換
            String json = mapper.writeValueAsString(rireki);
            //JSONの出力
            System.out.println(json);
            //out.write(json);
            return rireki;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    @PostMapping("/upload")
    @ResponseBody	//＠ResponseBody アノテーションを付けることで、戻り値を HTTP レスポンスのコンテンツとすることができます。
    public String uploadPost(
    	@RequestParam("type") String type,
    	@RequestParam("file") final MultipartFile uploadFile,
    	@RequestParam("userId") String userId, 
    	@RequestParam("userName") String userName,
    	@RequestParam("code") String code,
    	@RequestParam("dt") String dt,
    	@RequestParam("formId") String formId,
    	@RequestParam("formName") String formName
    ) {
    	MyUtils.SystemLogPrint("■post:/upload start");
		// Upload file
    	//https://qiita.com/tseno/items/6f86dea212089d52ce28
    	if (uploadFile.isEmpty()) {
            return null;
        }
        System.out.println("filename: " + uploadFile.getOriginalFilename());      
        //userName = new String(userName.getBytes("iso-8859-1"), "utf-8");	//https://www.prime-architect.co.jp/myblog/google-app-engine-260
        System.out.println("userName: " + userName);      
        System.out.println("code: " + code);
        System.out.println("dt: " + dt);
        System.out.println("formId: " + formId);
        System.out.println("formName: " + formName);
        
		String ch = formId.substring(5, 6);		//6桁目
		String toriCd;
		if (ch.equals(" ") == true) {
			toriCd = formId.substring(0, 5);	//5桁
			formId = formId.substring(6, formId.length());	//6桁目から最後まで
		} else {
			toriCd = formId.substring(0, 6);	//6桁
			formId = formId.substring(7, formId.length());	//7桁から最後まで
		}
		
		String dtStr = dt.substring(0,4) + dt.substring(5,7) + dt.substring(8,10) + dt.substring(11,13) + dt.substring(14,16) + dt.substring(17,19);
		String ext = uploadFile.getOriginalFilename().substring(uploadFile.getOriginalFilename().lastIndexOf("."));
		String contentType = uploadFile.getContentType();
		System.out.println("content-type: " + contentType);
		String fileName = toriCd + "_" + dtStr + ext;
		if (contentType.equals("application/pdf") == true) {
			String OCR_INPUT_PATH = config.getOcrInputPath();
			String inputFilePath = OCR_INPUT_PATH + fileName;
	        final Path path = Paths.get(OCR_INPUT_PATH, fileName);
	        final byte[] bytes;
	        try {
	            // アップロードされたファイルのバイナリを取得
	            bytes = uploadFile.getBytes();
	            // ファイルの格納
	            Files.write(path, bytes);
	        } catch (IOException e) {
	            return null;
	        }
	        System.out.println("inputfilePath: " + path.toString());

			//pdfファイルは、OCR登録
			registOcrProcess(formId, inputFilePath);
			
			//ファイルをアップロード後、履歴を登録。その際、OCR変換後のxlsxに変更する。
			ext = ".xlsx";
			String PO_UPLOAD_PATH2 = config.getOcrUploadPath2();
			fileName = toriCd + "_" + dtStr + ext;
			String uploadFilePath = PO_UPLOAD_PATH2 + fileName;
			registUploadTable(userId, userName, dt, toriCd, uploadFilePath, code, inputFilePath);
		} else {
	        // アップロードされたファイルを格納するためのPathを取得
			String PO_UPLOAD_PATH2 = config.getOcrUploadPath2();
	        final Path path = Paths.get(PO_UPLOAD_PATH2, fileName);
	        final byte[] bytes;
	        try {
	            // アップロードされたファイルのバイナリを取得
	            bytes = uploadFile.getBytes();
	            // ファイルの格納
	            Files.write(path, bytes);
	        } catch (IOException e) {
	            return null;
	        }
	        System.out.println("filePath: " + path.toString());
	        
			//ファイルをアップロード後、履歴を登録
			String uploadFilePath = PO_UPLOAD_PATH2 + fileName;
			registUploadTable(userId, userName, dt, toriCd, uploadFilePath, code, "NO-OCR");

			/*			
			//uploadフォルダへ直接コピー
	    	src = Paths.get(inputFilePath);
			dst = Paths.get(uploadFilePath);
	    	try {
				Files.copy(src, dst, REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
			*/		
		}
		
		//ユーザーがアップロードしたことを通知するメール送信(別スレッドで非同期)
		//SendMail sendMail = new SendMail(config, userId, dt, toriCd, fileName);
		//new Thread(sendMail).start();        
		String subject = "アップロードされました。";
		String body = "ID: " + userId + "\n"
					+ "dt: " + dt  + "\n"
				   	+ "toriCd: " + toriCd + "\n"
				   	+ "file: " + fileName + "\n";
		MyMail mailConf = new MyMail(config, userId, "", subject, null, body);
        mailConf.sendMailThread();		
        //レスポンス
		return "{\"result\":\"ok\"}";
    }
    
	//OCR登録処理
	private void registOcrProcess(String documentId, String uploadFilePath) {
		OcrFormBean ocrForm = null;
		ocrForm = OcrFormDAO.getInstance(config).queryOcrFormInfoDocumentId(documentId);
        ObjectMapper mapper = new ObjectMapper();
        try {
            //JavaオブジェクトからJSONに変換
            String json = mapper.writeValueAsString(ocrForm);
            //JSONの出力
            System.out.println(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
		
		OcrDataFormBean ocrData = new OcrDataFormBean();
		//ocrData.setUnitId("");
		ocrData.setUnitName(ocrForm.getFormName());
		ocrData.setUploadFilePath(uploadFilePath);
		ocrData.setStatus("REGIST");
		//ocrData.setCsvFileName("");
		//ocrData.setDocsetId("");
		ocrData.setDocsetName(ocrForm.getDocsetName());
		ocrData.setDocumentId(documentId);
		ocrData.setDocumentName(ocrForm.getDocumentName());
		//ocrData.setCreatedAt("");
		int type = 1;	//1: 2:本番
		ocrData.setType(type);
		OcrDataFormDAO.getInstance(config).insertReadingUnitDB(ocrData);
	}
	
	private void registUploadTable(String userId, String userName, String dt, String toriCd, String uploadFilePath, String code, String inputFilePath) {
		PoUploadBean poUpload = new PoUploadBean();
		poUpload.setUserId(userId);
		poUpload.setUserName(userName);
		poUpload.setDatetime(dt);
		poUpload.setToriCd(toriCd);
		poUpload.setUploadPath(uploadFilePath);
		poUpload.setCode(code);
		poUpload.setInputPath(inputFilePath);
        ObjectMapper mapper = new ObjectMapper();
        try {
            //JavaオブジェクトからJSONに変換
            String json = mapper.writeValueAsString(poUpload);
            //JSONの出力
            System.out.println(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
		PoUploadDAO.getInstance(config).registData(poUpload);
	}
}
	