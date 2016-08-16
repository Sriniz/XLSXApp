package com.sfdcreporttofileapp.wb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class ReportUtil {
	LinkedHashMap <String,String> colToMap = new LinkedHashMap <String,String>();
			


	public ReportUtil() {
		super();
		colToMap.put("Client_Avail__c.Client__c.", "Client_Avail__r.Client__r.");
		colToMap.put("Client_Avail__c.Client__c", "Client_Avail__r.Client__c");
		colToMap.put("Client_Avail__c.Local_Title__c.", "Client_Avail__r.Local_Title__r.");
		colToMap.put("Client_Avail__c.Local_Title__c", "Client_Avail__r.Local_Title__c");
		colToMap.put("Announcement_Record__c.", "");
		colToMap.put("Collection_Announcement_Record__c.", "");
		colToMap.put("Title__c", "Client_Avail__r.Commercial_Avail__r.Title__r");
		colToMap.put("Client_Avail__c", "Client_Avail__r");
	}
	

	public HashMap<String,String> getReportHeaderMap(JSONObject reportDetails) throws Exception{
		HashMap<String,String> reportkeyHeader = new HashMap<String,String>();
		JSONObject reportExtendedMetadata = reportDetails.getJSONObject("reportExtendedMetadata");
		JSONObject detailColumns = reportExtendedMetadata.getJSONObject("detailColumnInfo");
		@SuppressWarnings("rawtypes")
		Iterator iter = detailColumns.keys();
	    while(iter.hasNext()){
	        String col = (String)iter.next();
	        JSONObject value = detailColumns.getJSONObject(col);
	        for (Map.Entry<String, String> entry : colToMap.entrySet()) {
	            String key = entry.getKey();
	            String kvalue = entry.getValue();
	            if(col.startsWith(key)){
	          		col = col.replace(key, kvalue);
	          		reportkeyHeader.put(col, value.getString("label"));
	          	}
	        }
	    }
		return reportkeyHeader;
	}
	
	
	public String getQueryString(JSONObject reportDetails,String annId) throws Exception {
		String reportQuery=null;
		System.out.println("Sriniz...reportDetails :"+reportDetails);
		JSONObject reportMetadata = reportDetails.getJSONObject("reportMetadata");
		//System.out.println("reportMetadata :"+reportMetadata);
		JSONArray detailColumns = reportMetadata.getJSONArray("detailColumns");
		//System.out.println("detailColumns :"+detailColumns);
		//System.out.println("\ndetailColumns :"+detailColumns.length());
		reportQuery = "SELECT ";
		for (int i = 0; i < detailColumns.length(); i++) {
			String col = detailColumns.get(i).toString();
			for (Map.Entry<String, String> entry : colToMap.entrySet()) {
	            String key = entry.getKey();
	            String kvalue = entry.getValue();
	            if(col.startsWith(key)){
	            	col = col.replace(key, kvalue);
	            	reportQuery += col + ",";
	          	}
	        }
        }
		reportQuery = reportQuery.substring(0, reportQuery.length()-1); // remove the last comma
		System.out.println("getReportName(reportDetails)....sriniz"+getReportName(reportDetails));
		if(getReportName(reportDetails).equals("EMA_TV_v1_6")){
			System.out.println("Im doing custom sorting Sriniz ema tv!!");
			reportQuery += " FROM Announcement_Record__c WHERE Announcement__c='"+annId+"' AND Announce_Override__c!='Suppress' ORDER BY Client_Avail__r.EMA_SeriesTitleInternalAlias__c,Client_Avail__r.EMA_SeasonNumber__c,Client_Avail__r.EMA_Territory__c,Client_Avail__r.EMA_EpisodeNumber__c,Client_Avail__r.EMA_Start__c";
		}
		else if(getReportName(reportDetails).equals("WB_Standard_TV")){
			System.out.println("Im doing custom sorting Sriniz standard tv!!");
			reportQuery += " FROM Announcement_Record__c WHERE Announcement__c='"+annId+"' AND Announce_Override__c!='Suppress' ORDER BY Client_Avail__r.Title_Alias__c,Client_Avail__r.Title_Season__c,Client_Avail__r.Release_Plan_Country__c,Client_Avail__r.EMA_EpisodeNumber__c,Client_Avail__r.EMA_Start__c";
		}
		else
			reportQuery += " FROM Announcement_Record__c WHERE Announcement__c='"+annId+"' AND Announce_Override__c!='Suppress' ORDER BY Global_Title__c,Country__c,Language__c,Channel__c,Format__c,Client_Start__c";	
		System.out.println("\nFinal Query :"+reportQuery);
		return reportQuery;
	}

	public String getQueryStringCollection(JSONObject reportDetails,String annId) throws Exception {
		String reportQuery=null;
		//System.out.println("reportDetails :"+reportDetails);
		JSONObject reportMetadata = reportDetails.getJSONObject("reportMetadata");
		//System.out.println("reportMetadata :"+reportMetadata);
		JSONArray detailColumns = reportMetadata.getJSONArray("detailColumns");
		//System.out.println("detailColumns :"+detailColumns);
		//System.out.println("\ndetailColumns :"+detailColumns.length());
		reportQuery = "SELECT ";
		for (int i = 0; i < detailColumns.length(); i++) {
			String col = detailColumns.get(i).toString();
			for (Map.Entry<String, String> entry : colToMap.entrySet()) {
	            String key = entry.getKey();
	            String kvalue = entry.getValue();
	            if(col.startsWith(key)){
	            	col = col.replace(key, kvalue);
	            	reportQuery += col + ",";
	          	}
	        }
        }
		reportQuery = reportQuery.substring(0, reportQuery.length()-1); // remove the last comma
		reportQuery += " FROM Collection_Announcement_Record__c WHERE Announcement__c='"+annId+"'";	
		System.out.println("\nFinal Query Collection :"+reportQuery);
		return reportQuery;
	}

	
	public List<String> getReportColList(JSONObject reportDetails,HashMap<String,String> fieldColMap) throws Exception {
		List<String> colList= new ArrayList<String>();
		JSONObject reportMetadata = reportDetails.getJSONObject("reportMetadata");
		JSONArray detailColumns = reportMetadata.getJSONArray("detailColumns");
		for (int i = 0; i < detailColumns.length(); i++) {
			String col = detailColumns.get(i).toString();
			for (Map.Entry<String, String> entry : colToMap.entrySet()) {
	            String key = entry.getKey();
	            String kvalue = entry.getValue();
	            if(col.startsWith(key)){
	            	col = col.replace(key, kvalue);
	            	colList.add(fieldColMap.get(col));
	          	}
			}
        }
		return colList;
	}
	
	public List<String> getReportAPIList(JSONObject reportDetails) throws Exception {
		List<String> colList= new ArrayList<String>();
		JSONObject reportMetadata = reportDetails.getJSONObject("reportMetadata");
		JSONArray detailColumns = reportMetadata.getJSONArray("detailColumns");
		for (int i = 0; i < detailColumns.length(); i++) {
			String col = detailColumns.get(i).toString();
			for (Map.Entry<String, String> entry : colToMap.entrySet()) {
	            String key = entry.getKey();
	            String kvalue = entry.getValue();
	            if(col.startsWith(key)){
	            	col = col.replace(key, kvalue);
	            	colList.add(col);
	          	}
			}
        }
		return colList;
	}
	
	public String getReportName(JSONObject reportDetails) throws Exception {
		String repName;
		JSONObject reportMetadata = reportDetails.getJSONObject("reportMetadata");
		repName = reportMetadata.getString("developerName");
		return repName;
	}
	
	public HashMap<String,String> getReportDataTypeMap(JSONObject reportDetails) throws Exception{
		HashMap<String,String> reportkeyDataType = new HashMap<String,String>();
		JSONObject reportExtendedMetadata = reportDetails.getJSONObject("reportExtendedMetadata");
		JSONObject detailColumns = reportExtendedMetadata.getJSONObject("detailColumnInfo");
		@SuppressWarnings("rawtypes")
		Iterator iter = detailColumns.keys();
	    while(iter.hasNext()){
	        String col = (String)iter.next();
	        JSONObject value = detailColumns.getJSONObject(col);
	        for (Map.Entry<String, String> entry : colToMap.entrySet()) {
	            String key = entry.getKey();
	            String kvalue = entry.getValue();
	            if(col.startsWith(key)){
	          		col = col.replace(key, kvalue);
	          		reportkeyDataType.put(col, value.getString("dataType"));
	          	}
	        }
	    }
		return reportkeyDataType;
	}
	
	public List<String> getReportDateTypeList(JSONObject reportDetails,HashMap<String,String> fieldColMap) throws Exception {
		List<String> colList= new ArrayList<String>();
		JSONObject reportMetadata = reportDetails.getJSONObject("reportMetadata");
		JSONArray detailColumns = reportMetadata.getJSONArray("detailColumns");
		for (int i = 0; i < detailColumns.length(); i++) {
			String col = detailColumns.get(i).toString();
			for (Map.Entry<String, String> entry : colToMap.entrySet()) {
	            String key = entry.getKey();
	            String kvalue = entry.getValue();
	            if(col.startsWith(key)){
	            	col = col.replace(key, kvalue);
	            	colList.add(fieldColMap.get(col));
	          	}
			}
        }
		return colList;
	}

}
