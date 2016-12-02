/**
 * 
 */
package com.sfdcreporttofileapp.wb;

import java.util.List;

import org.apache.commons.httpclient.HttpClient;

/**
 * @author ctsuser
 *
 */
public class CreateXMLFile implements CreateFileInterface {
	
	String accessToken;
    String instanceUrl;
    HttpClient httpclient;
    
    

	public CreateXMLFile(String accessToken, String instanceUrl) {
		super();
		this.accessToken = accessToken;
		this.instanceUrl = instanceUrl;
		httpclient = new HttpClient();
	}



	@Override
	public String createFile(String query, List<String> col, String FileName,List<String> colAPI,Announcement ann,boolean emaCheck,List<String> dataType,String annDateFormat) {
		// TODO Auto-generated method stub
		return null;
	}

}
