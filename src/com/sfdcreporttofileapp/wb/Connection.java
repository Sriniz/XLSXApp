/**
 * 
 */
package com.sfdcreporttofileapp.wb;



/**
 * @author ctsuser
 *
 */
public interface Connection {
  
	String USERNAME = "srinizkumar.konakanchi@wbconsultant.com.qa";
	String PASSWORD = "";
	String url = "https://test.salesforce.com/services/oauth2/token";
    String client_id = "3MVG9FS3IyroMOh4JaFY_ohSyLR9_04OwuexOfukzHJnjrqp1QFGJutM_0.cHfpXe3U1Q9Y2DVirpQcqevSHq";
    String client_secret = "4483528363863041495";
    String password = "password";


	/*
	String USERNAME = "wbhesalesservices@warnerbros.com";
    String PASSWORD = "abcd#12349b1TBLc2zOVYssgJGrGmv77b";
    String url = "https://login.salesforce.com/services/oauth2/token";
 	String client_id = "3MVG9QDx8IX8nP5RVTD7B_BW.6iWWKEx6tWPjebEUjrqalYs0mC.CRuVkMrlEoHEXDjjPeRUGCDoQ8W39rTJ8";
 	String client_secret = "5380441142701351058";
 	String password = "password";
    */
	
    String getConnection() throws Exception;

}
