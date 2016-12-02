/**
 * 
 */
package com.sfdcreporttofileapp.wb;

import java.util.List;

/**
 * @author ctsuser
 *
 */
public interface CreateFileInterface {
	
	public String createFile(String query,List<String> col,String FileName,List<String> colAPI,Announcement ann,boolean EMACheck,List<String> dataType,String annDateType) throws Exception;
}
