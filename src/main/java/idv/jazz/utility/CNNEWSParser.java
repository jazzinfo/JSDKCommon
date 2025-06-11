package idv.jazz.utility;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import idv.jazz.dto.News;

//中央日報檔案分析器
public class CNNEWSParser {

    // 建立 FieldName → News屬性名稱的對應表
    private static final Map<String, String> fieldMap = new HashMap<>();

    static {
        fieldMap.put("TI:", "title");
        fieldMap.put("AU:", "author");
        fieldMap.put("SO:", "baokan");
        fieldMap.put("DA:", "pubdate");
        fieldMap.put("PG:", "banci");
        fieldMap.put("PN:", "banming");
        fieldMap.put("RL:", "baodaodi");
        fieldMap.put("SJ:", "jhuanlan");
        fieldMap.put("IM:", "ftimg");
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
