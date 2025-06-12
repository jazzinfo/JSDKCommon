package idv.jazz.utility;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import idv.jazz.dto.News;

//中華日報檔案分析器
public class NewsParser {

    // 建立 FieldName → News屬性名稱的對應表
    private static final Map<String, String> fieldMap = new HashMap<>();

    static {
        fieldMap.put("標    題:", "title");
        fieldMap.put("作    者:", "author");
        fieldMap.put("報 刊 別:", "baokan");
        fieldMap.put("日    期:", "pubdate");
        fieldMap.put("版    次:", "banci");
        fieldMap.put("版    名:", "banming");
        fieldMap.put("專    欄:", "jhuanlan");
        fieldMap.put("報 導 地:", "baodaodi");
        fieldMap.put("全文影像:", "ftimg");
    }

    public static News parseLinesToDto(List<String> lines) {
        News news = new News();

        for (String line : lines) {
            for (Map.Entry<String, String> entry : fieldMap.entrySet()) {
                String fieldLabel = entry.getKey();
                String fieldName = entry.getValue();

                if (line.startsWith(fieldLabel)) {
                    String value = line.substring(fieldLabel.length()).trim();
                    try {
                        Field field = News.class.getDeclaredField(fieldName);
                        field.setAccessible(true);
                        field.set(news, value);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return news;
    }
}

