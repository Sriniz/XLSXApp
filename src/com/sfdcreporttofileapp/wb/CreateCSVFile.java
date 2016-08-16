/**
 * 
 */
package com.sfdcreporttofileapp.wb;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * @author ctsuser
 *
 */
public class CreateCSVFile  extends createFileAbstract  implements CreateFileInterface {
	
	String accessToken;
    String instanceUrl;
    HttpClient httpclient;

    
	public CreateCSVFile(String accessToken, String instanceUrl) {
		super();
		this.accessToken = accessToken;
		this.instanceUrl = instanceUrl;
		httpclient = new HttpClient();
	}


	@Override
	public String createFile(String query, List<String> col, String FileName,List<String> colAPI,Announcement ann,boolean emaCheck,List<String> dataType) throws Exception {
		String csvFileName = FileName+".csv";
		
		PrintWriter writer = new PrintWriter(csvFileName, "UTF-8");
		//Add header
		String csvheader = "\uFEFF";
		for(String colh:col){
			csvheader += "\""+colh+"\",";
		}
		csvheader = csvheader.substring(0, csvheader.length()-1); // remove the last comma
		writer.println(csvheader);
		
		//Reading First set of data
		String reportURL = instanceUrl + "/services/data/v20.0/query";
        GetMethod myget = new GetMethod(reportURL);
        myget.setRequestHeader("Authorization", "OAuth " + accessToken);
        myget.setRequestHeader("Sforce-Query-Options","batchSize=2000");
        //myget.setRequestHeader("Content-Type", "text/xml; charset=utf-8");
        NameValuePair[] params = new NameValuePair[1];
        params[0] = new NameValuePair("q", query);
        myget.setQueryString(params);
        
        String nextRecordsUrl = null;
        do{
        	httpclient.executeMethod(myget);
            //System.out.println("\ngetResponseCharSet" +myget.getResponseCharSet());
            //System.out.println("\ngetRequestCharSet" +myget.getRequestCharSet());
            JSONObject myquery = new JSONObject(
            		new JSONTokener(new InputStreamReader(
            				myget.getResponseBodyAsStream())));
	        //System.out.println("Query Result: "+myquery);
	        JSONArray results = myquery.getJSONArray("records");
	        List<List<String>> dataList = parseJSON(results, colAPI);
	        for(List<String> data:dataList ){
	        	String rows = "";
	        	for(String row:data){
	        		rows += "\""+row+"\",";
	        	}
	        	rows = rows.substring(0, rows.length()-1); // remove the last comma
	    		writer.println(rows);
	    		writer.flush();
	        }
	        
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
        }while(nextRecordsUrl!=null);
        //////////////////
		writer.close();
		//System.out.println("I am done");
		uploadAttachment(accessToken,instanceUrl,ann,csvFileName);
		
		//Some finishing touch
		updateAnnouncementStatus(accessToken,instanceUrl,ann);
		callCreateReport(accessToken,instanceUrl,ann);
		// TODO Auto-generated method stub
		return null;
	}

}
