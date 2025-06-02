package idv.jazz.utility;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import idv.jazz.dto.News;

//讀取檔案範例
public class NewsArticleParser {

    public static List<News> parseArticles(String filePath) throws IOException {
        List<News> articles = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);

        News current = new News();

        for (String line : lines) {
            line = line.trim();

            if (line.isEmpty()) continue;

            if (line.startsWith("---")) {
                articles.add(current);
                current = new News();
                continue;
            }

            String[] parts = line.split(":", 2);
            if (parts.length < 2) continue;

            String key = parts[0].trim().replace(" ", "");
            String value = parts[1].trim();

            switch (key) {
                case "標題" -> current.setTitle(value);
                case "作者" -> current.setAuthor(value);
                case "報刊別" -> current.setBaokan(value);
                case "日期" -> current.setPubdate(value);
                case "版次" -> current.setBanci(value);
                case "版名" -> current.setBanming(value);
                case "專欄" -> current.setJhuanlan(value);
                case "報導地" -> current.setBaodaodi(value);
                case "全文影像" -> current.setFtimg(value);
            }
        }

        // 加入最後一筆
        if (current.getTitle() != null) {
            articles.add(current);
        }

        return articles;
    }
}

