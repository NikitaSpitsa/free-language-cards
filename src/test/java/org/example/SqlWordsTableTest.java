package org.example;

import org.example.dao.ConfigLoader;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;


public class SqlWordsTableTest {


    public static final String SQL_DB_URL = ConfigLoader.getProperty("sqlDb.url");
    public static final String SQL_DB_USERNAME = ConfigLoader.getProperty("sqlDb.username");
    public static final String SQL_DB_PASSWORD = ConfigLoader.getProperty("sqlDb.password");

    @Test
    void testWordsTableTest() {

        try (Connection connection = DriverManager.getConnection(SQL_DB_URL, SQL_DB_USERNAME, SQL_DB_PASSWORD);
             Statement statement = connection.createStatement()) {

            String exampleName = "Hello";
            String exampleForTableExample = "Hello world";

            statement.executeUpdate("DROP TABLE IF EXISTS words");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS words (id INT PRIMARY KEY AUTO_INCREMENT, word VARCHAR(50), example VARCHAR(1000))");
            statement.executeUpdate("INSERT INTO words(word,example) VALUES('" + exampleName + "','" + exampleForTableExample + "')");
            ResultSet resultSet = statement.executeQuery("SELECT * FROM words");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("word");
                String exampleFromTable = resultSet.getString("example");

                assertEquals(id, 1);
                assertEquals(name, exampleName);
                assertEquals(exampleFromTable, exampleForTableExample);

            }

            statement.executeUpdate("TRUNCATE TABLE words");

        } catch (
                SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

    }


}
