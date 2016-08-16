/**
 * 
 */
package com.sfdcreporttofileapp.wb;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * @author ctsuser
 *
 */
public abstract class createFileAbstract {

	private static final String SF_ADD_ATTACHMENT = "/services/data/v20.0/sobjects/Attachment/";
	private static final String SF_UPDATE_ANNOUNCEMENT = "/services/data/v20.0/sobjects/Announcement__c/";
	private static final String SF_CREATE_REPORT = "/services/apexrest/WB_CreateReport";

	List<List<String>> parseJSON(JSONArray jsonArray,List<String> col) throws Exception{
		List<List<String>> recList = new ArrayList<List<String>>();
		System.out.println("jsonArray :"+jsonArray );
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObj = jsonArray.getJSONObject(i);
			List<String> data = new ArrayList<String>();
			for(String ccol : col){
				//System.out.println("COL NAME :"+ccol);
				String[] colObj = ccol.split("\\.");
				if(colObj.length == 1){
					//System.out.println("ccol 1:"+ccol +" :"+jsonObj.get(ccol));
					if(!jsonObj.get(ccol).toString().startsWith("null")){
					data.add(jsonObj.get(ccol).toString());
					}else{
						data.add("");
					}
				}else if(colObj.length == 2){
					//System.out.println("ccol 2:"+ccol +"JSON :"+jsonObj );
					JSONObject clientAvail = jsonObj.getJSONObject(colObj[0]);
					try {
						if(!clientAvail.get(colObj[1]).toString().startsWith("null")){
							data.add(clientAvail.get(colObj[1]).toString());
						}else{
								data.add("");
						}
						//data.add(clientAvail.getString(colObj[1]));
						//data.add(clientAvail.get(colObj[1]).toString());
					} catch (Exception e) {
						data.add("");
					}
				}else if(colObj.length == 3){
					//System.out.println("ccol 3:"+ccol  +colObj[0] +"JSON :" +jsonObj );
					JSONObject clientAvail = jsonObj.getJSONObject(colObj[0]);
					//System.out.println("ccol 33:"+clientAvail );
					//JSONObject obj = clientAvail.getJSONObject(colObj[1]);
					try {
						JSONObject obj = clientAvail.getJSONObject(colObj[1]);
						//System.out.println("LEN 4 "+obj.getString(colObj[2]));
						data.add(obj.getString(colObj[2]));
					} catch (Exception e) {
						data.add("");
					}
				}else if(colObj.length == 4){
					try {
						JSONObject clientAvail = jsonObj.getJSONObject(colObj[0]);
						JSONObject rp = clientAvail.getJSONObject(colObj[1]);
						JSONObject obj = rp.getJSONObject(colObj[2]);
						data.add(obj.getString(colObj[3]));
					} catch (Exception e) {
						data.add("");
					}
				}else {
					data.add("BUG"+colObj.length);
				}
			}
			recList.add(data);
		}
		return recList;
	}
	
	
	public void uploadAttachment(String accessToken,String instanceUrl,Announcement ann,String fileName) throws Exception{
		// Read the file
        byte[] data = IOUtils.toByteArray(new FileInputStream(fileName)); //#1
        JSONObject content = new JSONObject(); //#2
        if (fileName != null) {
            content.put("Name", fileName); //#3
        }
        if (fileName != null) {
            content.put("Description", fileName); //#4
        }     

		content.put("Body", new String(Base64.encodeBase64(data))); //#5
        content.put("ParentId", ann.Id); //#6
        PostMethod post = new PostMethod(instanceUrl  + SF_ADD_ATTACHMENT); //#7
        post.setRequestHeader("Authorization", "OAuth " + accessToken); //#8
        post.setRequestEntity(new StringRequestEntity(content.toString(), "application/json", null)); //#9
        String contentId = null;
        HttpClient httpclient = new HttpClient();

        try {
            httpclient.executeMethod(post); //#10
            if (post.getStatusCode() == HttpStatus.SC_CREATED) {
                JSONObject response = new JSONObject(new JSONTokener(new InputStreamReader(post.getResponseBodyAsStream())));
                if (response.getBoolean("success")) {
                    contentId = response.getString("id"); //#11
                }
            } else if(post.getStatusCode() == HttpStatus.SC_UNAUTHORIZED){
                throw new Exception();
            }
        } finally {
            post.releaseConnection();
        }
	}
	
	public void updateAnnouncementStatus(String accessToken,String instanceUrl,Announcement ann ) throws Exception{
		PostMethod post = new PostMethod(instanceUrl  + SF_UPDATE_ANNOUNCEMENT + ann.Id){
	        @Override public String getName() { return "PATCH"; }
	    };
        post.setRequestHeader("Authorization", "OAuth " + accessToken); 
        HttpClient httpclient = new HttpClient();
        JSONObject announcement = new JSONObject();
        announcement.put("File_Status__c", "Done");
        post.setRequestEntity(new StringRequestEntity(announcement.toString(), "application/json", "UTF-8"));
        int sc = httpclient.executeMethod(post);
        //System.out.println("updateAnnouncementStatus " + sc);
        return;
	}
	
	public void callCreateReport(String accessToken,String instanceUrl,Announcement ann) throws Exception{
		HttpClient httpclient = new HttpClient();
		PostMethod post = new PostMethod(instanceUrl  + SF_CREATE_REPORT); 
        post.setRequestHeader("Authorization", "OAuth " + accessToken); 
        JSONObject reqBody = new JSONObject();
        reqBody.put("ids", ann.Id);
        post.setRequestEntity(new StringRequestEntity(reqBody.toString(), "application/json", "UTF-8"));
        httpclient.executeMethod(post);
        return;
	}
}
