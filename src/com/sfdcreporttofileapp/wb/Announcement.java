package com.sfdcreporttofileapp.wb;

public class Announcement {
	
	public String Id;
	public String Name__c;
	public String Name;
	public String zAccount__c;
	public int Announcement_Record_Count__c;
	public String Report_Id__c;
	public String Report_Name__c;
	public String File_Status__c;
	public String File_Format__c;
	
	
	public String getFile_Status__c() {
		return File_Status__c;
	}


	public void setFile_Status__c(String file_Status__c) {
		File_Status__c = file_Status__c;
	}


	public String getFile_Format__c() {
		return File_Format__c;
	}


	public void setFile_Format__c(String file_Format__c) {
		File_Format__c = file_Format__c;
	}


	public Announcement() {
		super();
		this.Id = null;
		this.Name = null;
		this.Name__c = null;
		this.zAccount__c = null;
		this.Announcement_Record_Count__c = 0;
		this.Report_Id__c = null;
		this.Report_Name__c = null;
	}


	public String getId() {
		return Id;
	}


	public void setId(String id) {
		Id = id;
	}


	public String getName__c() {
		return Name__c;
	}


	public void setName__c(String name__c) {
		Name__c = name__c;
	}


	public String getName() {
		return Name;
	}


	public void setName(String name) {
		Name = name;
	}


	public String getzAccount__c() {
		return zAccount__c;
	}


	public void setzAccount__c(String zAccount__c) {
		this.zAccount__c = zAccount__c;
	}


	public int getAnnouncement_Record_Count__c() {
		return Announcement_Record_Count__c;
	}


	public void setAnnouncement_Record_Count__c(int announcement_Record_Count__c) {
		Announcement_Record_Count__c = announcement_Record_Count__c;
	}


	public String getReport_Id__c() {
		return Report_Id__c;
	}


	public void setReport_Id__c(String report_Id__c) {
		Report_Id__c = report_Id__c;
	}


	public String getReport_Name__c() {
		return Report_Name__c;
	}


	public void setReport_Name__c(String report_Name__c) {
		Report_Name__c = report_Name__c;
	}

	

}

