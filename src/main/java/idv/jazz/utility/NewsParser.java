package idv.jazz.utility;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import idv.jazz.dto.NewsDto;

public class NewsParser {

    // 建立 FieldName → NewsDto屬性名稱的對應表
    private static final Map<String, String> fieldMap = new HashMap<>();

    static {
        fieldMap.put("標    題:", "title");
        fieldMap.put("作    者:", "author");
        fieldMap.put("報 刊 別:", "baokan");
        fieldMap.put("日    期:", "date");
        fieldMap.put("版    次:", "banci");
        fieldMap.put("版    名:", "banming");
        fieldMap.put("專    欄:", "jhuanlan");
        fieldMap.put("報 導 地:", "locale");
        fieldMap.put("全文影像:", "images");
    }

    public static NewsDto parseLinesToDto(List<String> lines) {
        NewsDto newsDto = new NewsDto();

        for (String line : lines) {
            for (Map.Entry<String, String> entry : fieldMap.entrySet()) {
                String fieldLabel = entry.getKey();
                String fieldName = entry.getValue();

                if (line.startsWith(fieldLabel)) {
                    String value = line.substring(fieldLabel.length()).trim();
                    try {
                        Field field = NewsDto.class.getDeclaredField(fieldName);
                        field.setAccessible(true);
                        field.set(newsDto, value);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return newsDto;
    }
}
