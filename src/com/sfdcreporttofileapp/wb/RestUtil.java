/**
 * 
 */
package com.sfdcreporttofileapp.wb;

import java.io.InputStreamReader;

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
public class RestUtil {
	
	String accessToken;
    String instanceUrl;
    HttpClient httpclient;

	public RestUtil(String accessToken, String instanceUrl) {
		super();
		this.accessToken = accessToken;
		this.instanceUrl = instanceUrl;
		httpclient = new HttpClient();
	}

	public RestUtil() {
		super();
		httpclient = new HttpClient();
		// TODO Auto-generated constructor stub
	}
	
	public Announcement restGetAnnouncementDetail(String id) throws Exception{
		Announcement announcement = new Announcement();
		String reportURL = instanceUrl + "/services/data/v20.0/sobjects/Announcement__c/"+id;
        GetMethod myget = new GetMethod(reportURL);
        myget.setRequestHeader("Authorization", "OAuth " + accessToken);
        httpclient.executeMethod(myget);
        JSONObject json = new JSONObject(
        		new JSONTokener(new InputStreamReader(
        				myget.getResponseBodyAsStream())));
        announcement.setId(json.getString("Id"));
        announcement.setName(json.getString("Name"));
        announcement.setName__c(json.getString("Name__c"));
        announcement.setReport_Id__c(json.getString("Report_Id__c"));
        announcement.setReport_Name__c(json.getString("Report_Name__c"));
        //announcement.setzAccount__c(json.getString("zAccount__c"));
        announcement.setAnnouncement_Record_Count__c(json.getInt("Announcement_Record_Count__c"));
        try {
        	announcement.setFile_Format__c(json.getString("File_Format__c"));
        	announcement.setFile_Status__c(json.getString("File_Status__c"));
        }catch (Exception e){
        	announcement.setFile_Format__c("XLSX");
        	announcement.setFile_Status__c("Done");
        }
        
        return announcement;
	}
	
	
	public JSONArray restQuery(String query) throws Exception{
		JSONArray results;
		String reportURL = instanceUrl + "/services/data/v20.0/query";
        GetMethod myget = new GetMethod(reportURL);
        myget.setRequestHeader("Authorization", "OAuth " + accessToken);
        NameValuePair[] params = new NameValuePair[1];
        params[0] = new NameValuePair("q", query);
        myget.setQueryString(params);
        httpclient.executeMethod(myget);
        //System.out.println("Response : "+myget.getResponseBodyAsString());
        JSONObject myquery = new JSONObject(
        		new JSONTokener(new InputStreamReader(
        				myget.getResponseBodyAsStream())));
        //System.out.println("Query Result: "+myquery);
        results = myquery.getJSONArray("records");
        return results;
	}

	
	public JSONObject getReportDetails(String reportId) throws Exception{
		String reportURL = instanceUrl + "/services/data/v29.0/analytics/reports/"+reportId+"/describe";
		System.out.println("reportURL :"+reportURL);
        GetMethod myget = new GetMethod(reportURL);
        myget.setRequestHeader("Authorization", "OAuth " + accessToken);
        httpclient.executeMethod(myget);
        System.out.println("Response : "+myget.getResponseBodyAsString());
        JSONObject myquery = new JSONObject(
        		new JSONTokener(new InputStreamReader(
        				myget.getResponseBodyAsStream())));
        System.out.println("myquery :"+myquery);
        return myquery;
	}
}
