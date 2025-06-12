package idv.jazz.jobs;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import idv.jazz.dto.News;
import idv.jazz.service.NewsService;
import idv.jazz.utility.CommonUtil;
import idv.jazz.utility.PropertyLoader;

public class CDNSNewsReadArticleWithEncodingDetect implements INews {

    private static final String START_MARK = "標    題:";
    private static final String END_MARK = "-----------------------------------------";

    public void toDo() { 
       	PropertyLoader loader = new PropertyLoader("my.properties");
       	String srcFilePath = loader.get("cdns.srcFile.path");
     	List<Path> myFileList = CommonUtil.getSrcFileList(srcFilePath);
     	for(Path item : myFileList) {
     		System.out.println( "讀取:" + item.toFile() );
     		batchInsertJob(item.toFile());
     		System.out.println( "=================================" );
     	}
    }
    
    public void batchInsertJob(File file) {
      // 	NewsService service = new NewsService();
        try {
            Charset charset = CommonUtil.detectCharset(file);
            System.out.println("偵測到編碼: " + charset.displayName());

            List<News> newsList = CommonUtil.parseNewsFromFile(file, START_MARK, END_MARK , charset, "CDNS");
            boolean isValid = CommonUtil.doValidData( newsList );
            if( isValid ) {
                // printNews(newsList);
          //       service.insertBatch(newsList);
                 System.out.println("批量插入成功！");           	
            }
        } catch (IOException e) {
            System.err.println("讀取檔案時發生錯誤: " + e.getMessage());
            e.printStackTrace();
        }
    }    
 
}


