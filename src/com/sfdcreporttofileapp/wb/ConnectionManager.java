/**
 * 
 */
package com.sfdcreporttofileapp.wb;

/**
 * @author ctsuser
 *
 */
public class ConnectionManager {
	
	
	public ConnectionManager() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Connection getConnection(String conName){
		if(conName == null){
	         return null;
	    }else if(conName == "REST"){
	    	return new RESTConnection();
	    }else if(conName == "SOAP"){
	    	return new SOAPConnection();
	    }else {
	    	return null;
	    }
	}

}
