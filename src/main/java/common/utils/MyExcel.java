package common.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFPivotTable;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFTableStyleInfo;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class MyExcel {
	
	public Workbook book;
	//public Sheet sheet;
	public XSSFSheet sheet;
	public Row row;
	public Cell cell;
	XSSFCellStyle headerStyle;
	XSSFCellStyle textStyle;
	XSSFCellStyle suryoStyle;
	XSSFCellStyle tankaStyle;
	XSSFCellStyle kingakuStyle;
	XSSFCellStyle dateStyle;
	XSSFCellStyle dtimeStyle;
	String[][] colFormat;
	private static final short FONT_SIZE = 11;
	
	//public MyExcel() {
	//}
	
	public Boolean sheetExist() {
		if (this.sheet == null)
			return false;
		else
			return true;
	}

	public void openXlsm(String xlsPath, boolean readOnly) throws IOException {
		if (readOnly == true) 
			book = WorkbookFactory.create(new File(xlsPath), null, true);
		else
			book = WorkbookFactory.create(new File(xlsPath));
	}
	
	public void open(String xlsPath, String sheetName) throws IOException {
		//new
		if (xlsPath == null) {
			book = new XSSFWorkbook();	//OOXML(Office Open XML)形式のファイルフォーマット
			
			if (sheetName == null)
				sheet = (XSSFSheet) book.createSheet("sheet0");
			else if (sheetName.equals("") == true)
				sheet = (XSSFSheet) book.createSheet("sheet0");
			else
				sheet = (XSSFSheet) book.createSheet(sheetName);
		//open
		} else {
			try {
				book = new XSSFWorkbook(new File(xlsPath));
			} catch (InvalidFormatException e) {
				e.printStackTrace();
			}
			
			if (sheetName == null)
				sheet = (XSSFSheet)book.getSheetAt(0);
			else
				sheet = (XSSFSheet)book.getSheet(sheetName);
		}
		setCellStyle();
	}

	public void open(String xlsPath, String sheetName, boolean readOnly) throws IOException {
		//拡張子は、xlsxのみ
		//Workbook book = WorkbookFactory.create(new File(xlsPath));	//使えない？
		//既存ファイルのオープン
		try {
			if (readOnly == true) 
				book = WorkbookFactory.create(new File(xlsPath), null, true);
			else
				book = new XSSFWorkbook(new File(xlsPath));
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}
		if (sheetName == null)
			sheet = (XSSFSheet)book.getSheetAt(0);
		else
			sheet = (XSSFSheet)book.getSheet(sheetName);
		
		setCellStyle();
	}
	
	private void setCellStyle() {
		//データ用Cell Styleの作成
		Font font = book.createFont();
        font.setFontHeightInPoints(FONT_SIZE);
        font.setFontName("Meiryo UI");
        //https://www.javadrive.jp/poi/style/index6.html
		suryoStyle = makeDataCellStyle(font, "#,##0_ "); 
		tankaStyle = makeDataCellStyle(font, "#,##0.00"); 
		kingakuStyle = makeDataCellStyle(font, "#,##0"); 
		dateStyle = makeDataCellStyle(font, "yyyy/mm/dd;@"); 
		dtimeStyle = makeDataCellStyle(font, "yyyy/mm/dd hh:mm:ss"); 
		textStyle = makeDataCellStyle(font, "@"); 
		
        //ヘッダ用Cell Styleの作成（センタリング、文字色 白）
		Font font2 = book.createFont();
        font2.setFontHeightInPoints(FONT_SIZE);
        font2.setFontName("Meiryo UI");
		font2.setColor((short) 9);	//IndexedColors.WHITE(9)
		headerStyle = makeHeaderCellStyle(font2);
	}
	
	//font と Dataformatを設定して、CellStyle を作成、罫線付き
	XSSFCellStyle makeDataCellStyle(Font font, String format) {
        DataFormat dformat = book.createDataFormat();
        XSSFCellStyle cellStyle = (XSSFCellStyle)book.createCellStyle();
        cellStyle.setDataFormat(dformat.getFormat(format));
        cellStyle.setFont(font);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN); 
        cellStyle.setBorderBottom(BorderStyle.THIN); 
		
		return cellStyle;
	}
	
	//font と Dataforma(中央揃え)を設定して、CellStyle を作成、罫線付き
	XSSFCellStyle makeHeaderCellStyle(Font font) {
        XSSFCellStyle cellStyle = (XSSFCellStyle)book.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);			//水平
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);	//垂直
        cellStyle.setFont(font);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN); 
        cellStyle.setBorderBottom(BorderStyle.THIN); 
		
		return cellStyle;
	}

	public void setSheet(String string) {
		this.sheet = (XSSFSheet)book.getSheet(string);
		if (this.sheet != null)
			System.out.println("setSheet: " + this.sheet.getSheetName());
		else
			System.err.println("setSheet: error!");
	}
	
	public Boolean getRow(int rowIdx) {
		row = sheet.getRow(rowIdx);
		if (row == null)
			return false;
		else
			return true;
	}

	public void createRow(int rowIdx) {
		row = sheet.createRow(rowIdx);
	}

	public boolean getCell(int colIdx) {
		cell = row.getCell(colIdx);
		if (cell == null)
			return false;
		else
			return true;
	}

	public void createCell(int colIdx) {
		cell = row.createCell(colIdx);
	}

	public CellType getCellType(int colIdx) {
		return cell.getCellType();
	}

	public CellStyle createCellStyle() {
		return book.createCellStyle();
	}

	//カラム位置(ロウ位置は事前に設定)のセルにStyleを設定
	public void setCellStyle(int colIdx, CellStyle style) {
		cell = row.getCell(colIdx);
		cell.setCellStyle(style);
	}

    public void setCellHeaderStyle(int colIdx) {
		cell = row.getCell(colIdx);
		cell.setCellStyle(headerStyle);
    }

	public boolean checkCellTypeNumeric() {
		CellType ctype = cell.getCellType();
		if (ctype == CellType.NUMERIC) {
			return true;
		} else {
			return false;
		}

	}

	public Object getCellValue(Cell cell) {
		CellType ctype;
		ctype = cell.getCellType();
		if (ctype == CellType.STRING) {
            return cell.getRichStringCellValue().getString();
		} else if (ctype == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue();
            } else {
                return cell.getNumericCellValue();
            }
		} else if (ctype == CellType.BOOLEAN) {
            return cell.getBooleanCellValue();
		} else if (ctype == CellType.FORMULA) {
            return cell.getCellFormula();
		} else {
			return null;
		}
    } 

	public String getStringCellValue() {
		return cell.getStringCellValue();
	}
	
	public String getStringCellValue(int rowIdx, int colIdx) {
		row = sheet.getRow(rowIdx);
		cell = row.getCell(colIdx);
		return cell.getStringCellValue();
	}

	public double getNumericCellValue() {
		return cell.getNumericCellValue();
	}
	
	public void setCellValue(int colIdx, String strValue) {
		cell = row.createCell(colIdx);
		cell.setCellValue(strValue);
		cell.setCellStyle(textStyle);
	}

	public void setCellValue(int colIdx, double dblValue) {
		cell = row.createCell(colIdx);
		cell.setCellValue(dblValue);
	}

	//ブックの別名保存
	public void save(String saveXlsPath) throws IOException {
		FileOutputStream out;
		try {
			out = new FileOutputStream(saveXlsPath);
			book.write(out);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	//ブックのクローズ
	public void close() throws IOException {
		book.close();
	}
	
	//結果をrowにセット。マッチしなければ最下行をセット。
	public String search(int colIdx, String target) {
		System.out.println("search sheet: " + this.sheet.getSheetName());
		//setSheet()されていることが前提
		String strValue = null;
		CellType ctype;
		boolean match = false;
		//https://plus-idea.net/how_to_read_excel_java_apache_poi/#i-2
		for (Row row2 : this.sheet) {
			strValue = "";
			cell = row2.getCell(colIdx);	//マッチング対象列を設定
			if (cell != null) {
				ctype = cell.getCellType();
				if (ctype == CellType.STRING) {
					if (cell.getStringCellValue() != null) {
						strValue = cell.getStringCellValue();
						strValue = strValue.trim();	//前後に空白が入っていたら除去
					} 
				} else if (ctype == CellType.NUMERIC) {
					strValue = String.valueOf(cell.getNumericCellValue());
					strValue = strValue.trim();		//前後に空白が入っていたら除去
				}
				if (strValue.equals(target) == true) {
					match = true;
					row = row2;
					cell = row.getCell(3);			//マッチング対象列を設定
					strValue = cell.getStringCellValue();
					strValue = strValue.trim();				//前後に空白が入っていたら除去
					break;
				}
			}
			row = row2;
		} //for
		//matchなし(最下行)
		if (match == false) {
			cell = row.getCell(colIdx);		//マッチング対象列を設定
			strValue = "00送信元なし";		//固定設定
		}
		
		return strValue;
	}

	public void setColFormat(String[][] argColFormat) {
		if (argColFormat == null) 
			return;

		//設定がある場合
		colFormat = new String[argColFormat.length][];
		colFormat = argColFormat;
	}
	
	//listデータ（2次元）をExcelに書き出し(1行目はヘッダ行)
	public int writeData(String sheetName, ArrayList<ArrayList<String>> list, boolean tableFlag) {
        int maxRow = list.size();
		if (maxRow < 2) {
			System.err.println("書き込みデータ無し");
			return -1;
		}
        final var tableSheet = (XSSFSheet) book.getSheet(sheetName);
		if (tableSheet == null) {
			System.err.println("sheetName: " + sheetName + " error!");
			return -1;
		}
		
		int maxCol = list.get(0).size();
		String strValue;
		int rowIdx = 0;
	    //1行目の高さを取得する
		//row = sheet.getRow(0);
		//System.out.println("1行目の高さ：" + row.getHeight());
		//ヘッダ行
		row = tableSheet.createRow(rowIdx);			//行の生成
		row.setHeight((short) 315);		//15.75pt
		for (int colIdx=0; colIdx<maxCol; colIdx++) {
			strValue = list.get(rowIdx).get(colIdx);
			cell = row.createCell(colIdx);
			cell.setCellStyle(textStyle);
			cell.setCellValue(strValue);
		}
		//データ行
		boolean matchFlag = false;
		for (rowIdx=1; rowIdx<maxRow; rowIdx++) {
		    //1行目の高さを取得する
			//row = sheet.getRow(rowIdx);
		    //System.out.println(rowIdx + "行目の高さ：" + row.getHeight());
		    row = tableSheet.createRow(rowIdx);		//行の生成
			row.setHeight((short) 315);		//15.75pt	//https://blog.java-reference.com/poi-width-height/
			for (int colIdx=0; colIdx<maxCol; colIdx++) {
				strValue = list.get(rowIdx).get(colIdx);
				cell = row.createCell(colIdx);
				matchFlag = false;
				if (colFormat != null) {
					for (String[] calFmt: colFormat) {
						if (Integer.parseInt(calFmt[0]) == colIdx) {
							if (calFmt[1].equals("SURYO")) {
								try {
									int tmpVal = Integer.parseInt(strValue);
									cell.setCellStyle(suryoStyle);
									cell.setCellValue(tmpVal);
								} catch(NumberFormatException e) {
									System.err.println("変換NG: " + strValue);
									cell.setCellValue(strValue);
								}
								//System.out.println(calFmt[1] + ":" + strValue);
								matchFlag = true;
								break;
							} else if (calFmt[1].equals("TANKA")) {
								try {
									double tmpVal = Double.parseDouble(strValue);
									cell.setCellStyle(tankaStyle);
									cell.setCellValue(tmpVal);
								} catch(NumberFormatException e) {
									System.err.println("変換NG: " + strValue);
									cell.setCellValue(strValue);
								}
								//System.out.println(calFmt[1] + ":" + strValue);
								matchFlag = true;
								break;
							} else if (calFmt[1].equals("KINGAKU")) {
								try {
									int tmpVal = Integer.parseInt(strValue);
									cell.setCellStyle(kingakuStyle);
									cell.setCellValue(tmpVal);
								} catch(NumberFormatException e) {
									System.err.println("変換NG: " + strValue);
									cell.setCellValue(strValue);
								}
								//System.out.println(calFmt[1] + ":" + strValue);
								matchFlag = true;
								break;
							} else if (calFmt[1].equals("DATE")) {		//yyyy/mm/dd;@
								try {
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
									Date tmpVal = sdf.parse(strValue);
									cell.setCellStyle(dateStyle);
									cell.setCellValue(tmpVal);
								} catch (ParseException e) {
									System.err.println("変換NG: " + strValue);
									cell.setCellValue(strValue);
								}								
								//System.out.println(calFmt[1] + ":" + strValue);
								matchFlag = true;
								break;
							} else if (calFmt[1].equals("DATETIME")) {	//yyyy/mm/dd hh:mm:ss
								try {
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
									Date tmpVal = sdf.parse(strValue);
									cell.setCellStyle(dtimeStyle);
									cell.setCellValue(tmpVal);
								} catch (ParseException e) {
									System.err.println("変換NG: " + strValue);
									cell.setCellValue(strValue);
								}								
								//System.out.println(calFmt[1] + ":" + strValue);
								matchFlag = true;
								break;
							} else if (calFmt[1].equals("TEXT")) {
								cell.setCellStyle(textStyle);
								cell.setCellValue(strValue);
								//System.out.println(calFmt[1] + ":" + strValue);
								matchFlag = true;
								break;
							} 
						}
					}
				}
				if (matchFlag == false) {
					cell.setCellStyle(textStyle);
					cell.setCellValue(strValue);
					//System.out.println("TEXT:" + strValue);
				}
			} //colIdx
		} //rowIdx
		
		if (tableFlag == true) {
			List<XSSFTable> tableList = tableSheet.getTables();
			if (tableList.size() == 0) {
				createTable(sheetName, 0, 0, (maxRow-1), (maxCol-1));	//テーブル新規作成
			} else {
				refreshTable(sheetName, 0, 0, (maxRow-1), (maxCol-1));	//同名で作成しなおし
			}
			//sheet.removeTable(table);	//既存テーブル削除
		}
		return 0;
	}
	
	//指定したシート名と指定範囲をテーブル化
	public int createTable(String tableName, int r_s, int c_s, int r_e, int c_e) {
        final var sheet = (XSSFSheet) book.getSheet(tableName);
		if (sheet == null) {
			System.err.println("sheetName: " + tableName + " error!");
			return -1;
		}

        //1行目ヘッダをセンタリング
		row = sheet.getRow(0);
		for (int colIdx=c_s; colIdx<=c_e; colIdx++) {
			setCellHeaderStyle(colIdx);
		} //colIdx
		//1行目ウィンドウ枠の固定
		sheet.createFreezePane(0, 1);

        final var creationHelper = book.getCreationHelper();
        //テーブルとして書式設定
        final var tableArea = creationHelper.createAreaReference(
	        new CellReference(r_s, c_s),
	        new CellReference(r_e, c_e)
        );
        final var table = sheet.createTable(tableArea);
        table.setName(tableName);
        table.setDisplayName(tableName); // テーブル名
        table.setStyleName("TableStyleMedium2"); // スタイル: TableStyleLight## | TableStyleMedium## | TableStyleDark##
        final var style = (XSSFTableStyleInfo) table.getStyle();
        style.setShowRowStripes(true); // 縞模様 (行)
        final var ctAutoFilter = table.getCTTable().addNewAutoFilter();
        ctAutoFilter.setRef(tableArea.formatAsString()); // フィルター ボタン
    
        //https://dukelab.hatenablog.com/entry/2015/02/17/130731
        // 列幅を自動調整(AutoFit + フィルタ分幅追加)する。
		for (int colIdx=c_s; colIdx<=c_e; colIdx++) {
            sheet.autoSizeColumn(colIdx, true);
			//width = xlsx.sheet.getColumnWidth(colIdx);
            //xlsx.sheet.setColumnWidth(colIdx, width+660);
			//System.out.println(colIdx + ": " + (width+660));
		} //colIdx
		
		return 0;
	}
	
	public int refreshTable(String tableName, int r_s, int c_s, int r_e, int c_e) {
        final var sheet = (XSSFSheet) book.getSheet(tableName);
		if (sheet == null) {
			System.err.println("sheetName: " + tableName + " error!");
			return -1;
		}

		XSSFTable table = sheet.getTables().get(0);
		System.out.println("テーブル名: " + table.getName());
		
		//1行目ヘッダをセンタリング(クリアされているので再度設定)
		row = sheet.getRow(0);
		for (int colIdx=c_s; colIdx<=c_e; colIdx++) {
			setCellHeaderStyle(colIdx);
		} //colIdx
		//1行目ウィンドウ枠の固定
		sheet.createFreezePane(0, 1);
    
        final var creationHelper = book.getCreationHelper();
        //テーブルの範囲更新
        final var tableArea = creationHelper.createAreaReference(
	        new CellReference(r_s, r_s),
	        new CellReference(r_e, c_e)
        );
		table.setArea(tableArea);
        //final var ctAutoFilter = table.getCTTable().addNewAutoFilter();
		//ctAutoFilter.setRef(tableArea.formatAsString()); // フィルター ボタン
        
        //https://dukelab.hatenablog.com/entry/2015/02/17/130731
        // 列幅を自動調整(AutoFit + フィルタ分幅追加)する。
		int width;
		for (int colIdx=c_s; colIdx<=c_e; colIdx++) {
            sheet.autoSizeColumn(colIdx, true);
			width = sheet.getColumnWidth(colIdx);
            sheet.setColumnWidth(colIdx, width+660);
			//System.out.println(colIdx + ": " + (width+660));
		} //colIdx
		
		return 0;
	}
	
	//https://stackoverflow.com/questions/1010673/refresh-pivot-table-with-apache-poi
	public int refreshPivot(String sheetName) {
	    final var pivotSheet = (XSSFSheet) book.getSheet(sheetName);
		if (pivotSheet == null) {
			System.err.println("sheetName: " + sheetName + " error!");
			return -1;
		}
		List<XSSFPivotTable> pivotList = pivotSheet.getPivotTables();
		//if (pivotList.size() != 0) {
		//	XSSFPivotTable pivotTable = pivotSheet.getPivotTables().get(0);     
		//	pivotTable.getPivotCacheDefinition().getCTPivotCacheDefinition().setRefreshOnLoad(true);			
		//}
		//シート上に複数ある場合は、全部refresh
		for (XSSFPivotTable pivot: pivotList) {
			pivot.getPivotCacheDefinition().getCTPivotCacheDefinition().setRefreshOnLoad(true);
		}
		
		return 0;
	}
}
