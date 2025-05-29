package idv.jazz.mapper;

import java.util.List;
import idv.jazz.dto.News;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NewsMapper {

    @InsertProvider(type = SqlBuilder.class, method = "buildBatchInsert")
    void batchInsertNews(List<News> newsList);

    class SqlBuilder {
        public static String buildBatchInsert(final List<News> newsList) {
            StringBuilder sb = new StringBuilder();

            for (News news : newsList) {
                sb.append("INSERT INTO News (title, author, baokan, pubdate, banci, banming, jhuanlan, baodaodi, ftimg) VALUES (")
                        .append("'").append(escape(news.getTitle())).append("', ")
                        .append("'").append(escape(news.getAuthor())).append("', ")
                        .append("'").append(escape(news.getBaokan())).append("', ")
                        .append("'").append(escape(news.getPubdate())).append("', ")
                        .append("'").append(escape(news.getBanci())).append("', ")
                        .append("'").append(escape(news.getBanming())).append("', ")
                        .append("'").append(escape(news.getJhuanlan())).append("', ")
                        .append("'").append(escape(news.getBaodaodi())).append("', ")
                        .append("'").append(escape(news.getFtimg())).append("');\n");
             }

            return sb.toString();
        }

        private static String escape(String value) {
            return value == null ? "" : value.replace("'", "''");
        }
    }
}
