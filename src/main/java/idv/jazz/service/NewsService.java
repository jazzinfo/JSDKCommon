package idv.jazz.service;

import idv.jazz.dto.News;
import idv.jazz.utility.MyBatisUtil;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class NewsService {

    private static final String INSERT_SQL = "INSERT INTO News "
            + "(title, author, baokan, pubdate, banci, banming, jhuanlan, baodaodi, ftimg) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    // 每批次的最大筆數
    private static final int BATCH_SIZE = 500;

    public void insertBatch(List<News> newsList) {
        try (Connection conn = MyBatisUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {

            conn.setAutoCommit(false);

            int count = 0;

            for (News news : newsList) {
                ps.setString(1, news.getTitle());
                ps.setString(2, news.getAuthor());
                ps.setString(3, news.getBaokan());
                ps.setString(4, news.getPubdate());
                ps.setString(5, news.getBanci());
                ps.setString(6, news.getBanming());
                ps.setString(7, news.getJhuanlan());
                ps.setString(8, news.getBaodaodi());
                ps.setString(9, news.getFtimg());

                ps.addBatch();
                count++;

                if (count % BATCH_SIZE == 0) {
                    ps.executeBatch();
                    ps.clearBatch();
                    System.out.println("新增" + BATCH_SIZE +" 筆 ");
                }
            }

            // 處理剩餘不足一批的資料
            if (count % BATCH_SIZE != 0) {
                ps.executeBatch();
                ps.clearBatch();
            }

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            // 可視需求在此處理回滾，或拋出例外
        }
    }
}
