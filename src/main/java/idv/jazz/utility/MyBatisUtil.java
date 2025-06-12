package idv.jazz.utility;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import idv.jazz.mapper.NewsMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class MyBatisUtil {

    private static final SqlSessionFactory sqlSessionFactory;

    static {
        try {
            // 建立資料來源
            PooledDataSource dataSource = new PooledDataSource();
            dataSource.setDriver("oracle.jdbc.OracleDriver");
            dataSource.setUrl("jdbc:oracle:thin:@localhost:1521:free");
            dataSource.setUsername("jazz");
            dataSource.setPassword("Orion1234");

            // 建立 TransactionFactory
            TransactionFactory transactionFactory = new JdbcTransactionFactory();

            // 建立 Environment
            Environment environment = new Environment("development", transactionFactory, dataSource);

            // 建立 Configuration 並加入 mapper
            Configuration configuration = new Configuration(environment);
            configuration.addMapper(NewsMapper.class);

            // 建立 SqlSessionFactory
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);

        } catch (Exception e) {
            System.err.println("初始化 MyBatis SqlSessionFactory 失敗：" + e.getMessage());
            throw new RuntimeException("初始化 MyBatis SqlSessionFactory 失敗", e);
        }
    }

    // 取得 MyBatis SqlSession (不自動提交)
    public static SqlSession getSession() {
        return sqlSessionFactory.openSession(false);
    }

    // 取得底層 JDBC Connection
    public static Connection getConnection() throws SQLException {
        return sqlSessionFactory.openSession(false).getConnection();
    }
}

