package idv.jazz;


import java.util.Arrays;
import java.util.List;

import idv.jazz.dto.News;
import idv.jazz.service.NewsService;

public class Main {
    public static void main(String[] args) throws Exception {
        NewsService service = new NewsService();

        List<News> newsList = Arrays.asList(
            new News("標題1", "作者A", "報紙X", "20240501", "1", "A版", "專欄1", "台北", "img1.jpg"),
            new News("標題2", "作者B", "報紙Y", "20240502", "2", "B版", "專欄2", "高雄", "img2.jpg")
        );

        service.insertBatch(newsList);
        System.out.println("批量插入成功！");
    }
}
