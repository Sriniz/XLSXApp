/**
 * 
 */
package com.sfdcreporttofileapp.wb;

/**
 * @author ctsuser
 *
 */
public class CreateFileFactory {

	String accessToken;
    String instanceUrl;
    
	public CreateFileFactory(String accessToken, String instanceUrl) {
		super();
		this.accessToken = accessToken;
		this.instanceUrl = instanceUrl;
	}
    
	
	public CreateFileInterface getFileCreator(String fileType){
		if(fileType == null){
	         return null;
	    }else if(fileType.equalsIgnoreCase("XML")){
	    	return new CreateXMLFile(accessToken,instanceUrl);
	    }else if(fileType.equalsIgnoreCase("CSV")){
	    	return new CreateCSVFile(accessToken,instanceUrl);
	    }else if(fileType.equalsIgnoreCase("XLSX")){
	    	return new CreateXLSXFile(accessToken,instanceUrl);
	    }else {
	    	return null;
	    }
	}
    
}
