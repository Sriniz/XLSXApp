/**
 * 
 */
package com.sfdcreporttofileapp.wb;



/**
 * @author ctsuser
 *
 */
public interface Connection {
	/* QA    */
	String USERNAME = "srinizkumar.konakanchi@wbconsultant.com.qa";
	String PASSWORD = "";
	String url = "https://test.salesforce.com/services/oauth2/token";
    String client_id = "3MVG9FS3IyroMOh4JaFY_ohSyLR9_04OwuexOfukzHJnjrqp1QFGJutM_0.cHfpXe3U1Q9Y2DVirpQcqevSHq";
    String client_secret = "4483528363863041495";
    String password = "password";

    
    
	 
    
    String getConnection() throws Exception;

}
