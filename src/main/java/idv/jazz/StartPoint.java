package idv.jazz;

import idv.jazz.jobs.CDNSNewsReadArticleWithEncodingDetect;
import idv.jazz.jobs.CNNewsBatchReadFileWithEncoding;
import idv.jazz.jobs.INews;

public class StartPoint {

    public static void main(String[] args) { 
       	doCNNewsJob();
       	System.out.println("Job End..."); 
    }
    
    public static void doCNNewsJob() {
      	INews cnnews = new CNNewsBatchReadFileWithEncoding();
      	cnnews.toDo();  	
    }    
    
    public static void doCDNSnewsJob() {
        INews cdns = new CDNSNewsReadArticleWithEncodingDetect();
  	    cdns.toDo();    	
    }
}