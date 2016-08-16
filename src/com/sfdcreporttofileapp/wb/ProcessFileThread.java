/**
 * 
 */
package com.sfdcreporttofileapp.wb;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author ctsuser
 *
 */
public class ProcessFileThread extends Thread {

	ConnectionManager conMan;
	Connection con;
	String[] urlToken;
	RestUtil rest;
	ReportUtil util;
	/**
	 * 
	 */
	public ProcessFileThread() {
		// TODO Auto-generated constructor stub
	}

	
	public void Initialize(){
		conMan = new ConnectionManager();
		con = conMan.getConnection("REST");
	}

	public void run(){
		try {
			
			//infinite loop
			while(true){
				Initialize();
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Date date = new Date();
				System.out.println("ANNNOUNCEMENT PROCESS IS RUNNING :"+dateFormat.format(date));
				String conDetail = con.getConnection();
				urlToken = conDetail.split(";");
				rest = new RestUtil(urlToken[1],urlToken[0]);
				util = new ReportUtil();
				String q = "SELECT id,Name FROM Announcement__c where File_Status__c='Pending'";
				JSONArray jsonArray = rest.restQuery(q);
				System.out.println("jsonArray length:"+jsonArray.length());
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObj = jsonArray.getJSONObject(i);
					if(jsonObj.get("Id").toString()!=null){
						System.out.println("Announcmenet ID :"+jsonObj.get("Id").toString());
						processAnnouncement(urlToken,jsonObj.get("Id").toString(),rest,util);
					}
				}
				//Thread.sleep(300000); 5 min polling
				Thread.sleep(1500000); //25 min polling
			}
		} catch (Exception e) {
			System.out.println("ProcessFileThread : "+e.toString());
			e.printStackTrace();
		}
	}
	
	
	
	public void processAnnouncement(String[] urlToken,String annId,RestUtil rest,ReportUtil util) {
		try {
			Announcement ann = rest.restGetAnnouncementDetail(annId);
			System.out.println("Announcement : "+ann.getName__c());
			JSONObject reportDetails = rest.getReportDetails(ann.getReport_Id__c());
			System.out.println("Report Name : "+util.getReportName(reportDetails));
			HashMap<String,String> headerHashmap = util.getReportHeaderMap(reportDetails);
			HashMap<String,String> dataTypeHashmap = util.getReportDataTypeMap(reportDetails);
			List<String> colList = util.getReportColList(reportDetails, headerHashmap);
			List<String> dataTypeList = util.getReportDateTypeList(reportDetails, dataTypeHashmap);
			List<String> colAPIList = util.getReportAPIList(reportDetails);
			CreateFileFactory cff = new CreateFileFactory(urlToken[1],urlToken[0]);
			CreateFileInterface cfI;
			if(ann.getFile_Format__c()!="" && ann.getFile_Format__c()!=null){
				cfI = cff.getFileCreator(ann.getFile_Format__c());
			}else{	
				cfI = cff.getFileCreator("XLSX");
			}
			boolean emaCheck = false;
			if (util.getReportName(reportDetails).startsWith("WB")){
				emaCheck = true;
			}
			cfI.createFile(util.getQueryString(reportDetails,ann.getId()), colList, ann.getName__c(),colAPIList,ann,emaCheck,dataTypeList);
		} catch (Exception e) {
			System.out.println("Cannot process Announcement Due to :"+e.toString());
			e.printStackTrace();
			// TODO: handle exception
		}
		
	}
}
