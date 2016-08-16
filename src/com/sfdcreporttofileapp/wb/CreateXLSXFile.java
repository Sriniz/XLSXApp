/**
 * 
 */
package com.sfdcreporttofileapp.wb;

import java.io.FileOutputStream;
import java.util.*;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;


/**
 * @author ctsuser
 *
 */
public class CreateXLSXFile extends createFileAbstract implements CreateFileInterface {

	String accessToken;
    String instanceUrl;
    HttpClient httpclient;
    HttpClient httpclientCollection;
    
    
	public CreateXLSXFile(String accessToken, String instanceUrl) {
		super();
		this.accessToken = accessToken;
		this.instanceUrl = instanceUrl;
		httpclient = new HttpClient();
		httpclientCollection = new HttpClient();
	}


	@Override
	public String createFile(String query, List<String> col, String FileName,List<String> colAPI,Announcement ann,boolean emaCheck,List<String> dataType) throws Exception{
		//System.out.println("CreateXLSXFile : createFile");
		String xlsxFileName = FileName+".xlsx";
		FileOutputStream fileOutputStream = new FileOutputStream(xlsxFileName);
		int RowNum=0;
		SXSSFWorkbook workBook = new SXSSFWorkbook(250);
		///////////////////////
		//DataFormat df  = workBook.createDataFormat();
		CellStyle cellStyle = workBook.createCellStyle();
	    CreationHelper createHelper = workBook.getCreationHelper();
	    //short dateFormat = createHelper.createDataFormat().getFormat("yyyy-dd-MM");
	    short dateFormat = 0;
	    if(emaCheck){
	    	dateFormat = createHelper.createDataFormat().getFormat("MM/dd/yyyy");
	    }else {
	    	dateFormat = createHelper.createDataFormat().getFormat("yyyy-MM-dd");
	    }
	    cellStyle.setDataFormat(dateFormat);
		////////////////////
	    SXSSFSheet sheet =(SXSSFSheet) workBook.createSheet(FileName);
	    //sheet.setRandomAccessWindowSize(250);
	    Row headerRow=sheet.createRow(RowNum++);
		//Add header
		for(int i=0;i<col.size();i++){
			headerRow.createCell(i).setCellValue(col.get(i));
		}
				
		//Reading First set of data
		String reportURL = instanceUrl + "/services/data/v29.0/query";
        GetMethod myget = new GetMethod(reportURL);
        myget.setRequestHeader("Authorization", "OAuth " + accessToken);
        myget.setRequestHeader("Sforce-Query-Options","batchSize=2000");
        //myget.setRequestHeader("Accept-Encoding", "gzip, deflate, sdch");
   
        //myget.setRequestHeader("X-PrettyPrint", "1");
        NameValuePair[] params = new NameValuePair[1];
        params[0] = new NameValuePair("q", query);
        myget.setQueryString(params);
        String nextRecordsUrl = null;
        do{
        	//CharsetEncoder encoder3 = Charset.forName("UTF-8").newEncoder();
        	httpclient.executeMethod(myget);
        	//System.out.println("\ngetResponseCharSet" +myget.getResponseCharSet());
            //System.out.println("\ngetRequestCharSet" +myget.getRequestCharSet());
        	/*
            JSONObject myquery = new JSONObject(
            		new JSONTokener(new InputStreamReader(
            				new GZIPInputStream(myget.getResponseBodyAsStream()))));
	        */
        	JSONObject myquery = new JSONObject(
            		new JSONTokener(new InputStreamReader(
            				myget.getResponseBodyAsStream())));
	        //System.out.println("Query Result: "+myquery);
	        JSONArray results = myquery.getJSONArray("records");
	        List<List<String>> dataList = parseJSON(results, colAPI);
	        for(List<String> data:dataList ){
	        	Row currentRow=sheet.createRow(RowNum++);
	        	for(int i=0;i<data.size();i++){
	        		if(dataType.get(i).equalsIgnoreCase("date") && !data.get(i).equalsIgnoreCase("")){
	        			DateFormat format = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
        				Date d = format.parse(data.get(i));
        				Cell c = currentRow.createCell(i);
        				c.setCellValue(d);
        				c.setCellStyle(cellStyle);
	        		}else if (dataType.get(i).equalsIgnoreCase("double") && !data.get(i).equalsIgnoreCase("")){
	        			Double d = Double.valueOf(data.get(i));
	        			Cell c = currentRow.createCell(i);
        				c.setCellValue(d);
	        		}else {
	        			currentRow.createCell(i).setCellValue(data.get(i));
	        		}
	        		/*
	        		if(emaCheck){
	        			//System.out.println("Date : "+data.get(i));
	        			//System.out.println("DateType : "+dataType.get(i));
	        			if(dataType.get(i).equalsIgnoreCase("date") && !data.get(i).equalsIgnoreCase("-")){
	        				DateFormat format = new SimpleDateFormat("yyyy-MM-dd",Locale.US);
	        				Date d = format.parse(data.get(i));
	        				Cell c = currentRow.createCell(i);
	        				c.setCellValue(d);
	        				c.setCellStyle(cellStyle);
	        			}else {
	        				currentRow.createCell(i).setCellValue(data.get(i));
	        			}
	        		}else {
	        			currentRow.createCell(i).setCellValue(data.get(i));
	        		}*/
	        	}
	        }
	        ((SXSSFSheet)sheet).flushRows(0);
	        fileOutputStream.flush();
	        try {
	        	nextRecordsUrl = myquery.getString("nextRecordsUrl");
			} catch (Exception e) {
				nextRecordsUrl = null;
			}
	        reportURL = instanceUrl + nextRecordsUrl;
	        //System.out.println("reportURL "+reportURL);
	        myget = new GetMethod(reportURL);
	        myget.setRequestHeader("Authorization", "OAuth " + accessToken);
	        myget.setRequestHeader("Sforce-Query-Options", "batchSize=2000");
	        //myget.setRequestHeader(headerName, headerValue);
        }while(nextRecordsUrl!=null);
        //////////////////
        /* ER-65 Collections - Sriniz start
        workBook.write(fileOutputStream);
        fileOutputStream.close();
        // dispose of temporary files backing this workbook on disk
        workBook.dispose();
        workBook.close();
        //ER-65 Collections - Sriniz end  */
        
        //ER-65 Collections - Sriniz start
        String q = "select id from Report where DeveloperName = 'Collection_Announcement_Records'";
        String reportURLCollectionChk = instanceUrl + "/services/data/v29.0/query";
        GetMethod mygetCollectionChk = new GetMethod(reportURLCollectionChk);
        mygetCollectionChk.setRequestHeader("Authorization", "OAuth " + accessToken);
        mygetCollectionChk.setRequestHeader("Sforce-Query-Options","batchSize=2000");
        NameValuePair[] paramsCollectionChk = new NameValuePair[1];
        RestUtil restChk = new RestUtil(accessToken,instanceUrl);
        ReportUtil utilChk = new ReportUtil();
        JSONArray jsonArrayChk = restChk.restQuery(q);
        JSONObject jsonObjChk = jsonArrayChk.getJSONObject(0);
        String reportId = jsonObjChk.get("Id").toString();
        JSONObject reportDetailsChk = restChk.getReportDetails(reportId);
        paramsCollectionChk[0] = new NameValuePair("q", utilChk.getQueryStringCollection(reportDetailsChk,ann.getId()));
        mygetCollectionChk.setQueryString(paramsCollectionChk);
        Integer noOfCollRecords = 0;
       	int statusCodeChk = httpclientCollection.executeMethod(mygetCollectionChk);
        if(statusCodeChk != HttpStatus.SC_OK) {
        	System.err.println("Hey Sriniz! I failed: " + mygetCollectionChk.getStatusLine());
        }
        JSONObject myqueryCollectionChk = new JSONObject(
              		new JSONTokener(new InputStreamReader(
              				mygetCollectionChk.getResponseBodyAsStream())));
  	    JSONArray resultsCollectionChk = myqueryCollectionChk.getJSONArray("records");
  	    List<String> colAPIListCollectionChk = utilChk.getReportAPIList(reportDetailsChk);
  	    List<List<String>> dataListCollectionChk = parseJSON(resultsCollectionChk, colAPIListCollectionChk);
  	    noOfCollRecords = noOfCollRecords + dataListCollectionChk.size();
  	    
        
        if(noOfCollRecords != 0){
        	int RowNumCollection=0;
      		SXSSFSheet sheetCollection = (SXSSFSheet) workBook.createSheet("Collection Details");
      	    Row headerRowCollection = sheetCollection.createRow(RowNumCollection++);
      	    RestUtil rest = new RestUtil(accessToken,instanceUrl);
      	    ReportUtil util = new ReportUtil();
      	  
      	    JSONObject reportDetails = rest.getReportDetails(reportId);
      	    System.out.println("Collection reportDetails...."+reportDetails);
      	    HashMap<String,String> headerHashmap = util.getReportHeaderMap(reportDetails);
      	    List<String> colListCollection = util.getReportColList(reportDetails, headerHashmap);
      	    List<String> colAPIListCollection = util.getReportAPIList(reportDetails);
      		//Add header
      		for(int i=0;i<colListCollection.size();i++){
      			headerRowCollection.createCell(i).setCellValue(colListCollection.get(i));
      		}
      		
      		//Reading First set of data
      		String reportURLCollection = instanceUrl + "/services/data/v29.0/query";
            GetMethod mygetCollection = new GetMethod(reportURLCollection);
            mygetCollection.setRequestHeader("Authorization", "OAuth " + accessToken);
            mygetCollection.setRequestHeader("Sforce-Query-Options","batchSize=2000");
            NameValuePair[] paramsCollection = new NameValuePair[1];
            paramsCollection[0] = new NameValuePair("q", util.getQueryStringCollection(reportDetails,ann.getId()));
            mygetCollection.setQueryString(paramsCollection);
            String nextRecordsUrlCollection = null;
            Integer noOfCollectionRecords = 0;
            do{
            	int statusCode = httpclientCollection.executeMethod(mygetCollection);
              	if(statusCode != HttpStatus.SC_OK) {
                    System.err.println("Hey Sriniz! I failed: " + mygetCollection.getStatusLine());
                }
              	JSONObject myqueryCollection = new JSONObject(
                  		new JSONTokener(new InputStreamReader(
                  				mygetCollection.getResponseBodyAsStream())));
      	        JSONArray resultsCollection = myqueryCollection.getJSONArray("records");
      	        List<List<String>> dataListCollection = parseJSON(resultsCollection, colAPIListCollection);
      	        noOfCollectionRecords = noOfCollectionRecords + dataListCollection.size();
      	        for(List<String> data:dataListCollection ){
      	        	Row currentRowCollection=sheetCollection.createRow(RowNumCollection++);
      	        	for(int i=0;i<data.size();i++){
      	        		if(data.get(i) != "-")
      	        			currentRowCollection.createCell(i).setCellValue(data.get(i));
      	        		else
      	        			currentRowCollection.createCell(i).setCellValue("");
      	        	}
      	        }
      	        ((SXSSFSheet)sheetCollection).flushRows(0);
      	        fileOutputStream.flush();
      	        try {
      	        	nextRecordsUrlCollection = myqueryCollection.getString("nextRecordsUrl");
      			} catch (Exception e) {
      				nextRecordsUrlCollection = null;
      			}
      	        reportURLCollection = instanceUrl + nextRecordsUrlCollection;
    	        mygetCollection = new GetMethod(reportURLCollection);
      	        mygetCollection.setRequestHeader("Authorization", "OAuth " + accessToken);
      	        mygetCollection.setRequestHeader("Sforce-Query-Options", "batchSize=2000");
              }while(nextRecordsUrlCollection!=null);
        }
           	  workBook.write(fileOutputStream);
              fileOutputStream.close();
              workBook.dispose();
              //workBook.close();
        //ER-65 Collections - Srinz end
        
		System.out.println("I am done");
		uploadAttachment(accessToken,instanceUrl,ann,xlsxFileName);
		
		//Some finishing touch
		updateAnnouncementStatus(accessToken,instanceUrl,ann);
		callCreateReport(accessToken,instanceUrl,ann);
		return null;
	}

}
