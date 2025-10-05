package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

public class Main {
    public static void main(String[] args) {

        System.out.print("Hello and welcome!\n");
        String url = "jdbc:mysql://localhost:3333/free_cards_mySql";
        String username = "root";
        String password = "root";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("DROP TABLE IF EXISTS words");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS words (id INT PRIMARY KEY AUTO_INCREMENT, word VARCHAR(50), example VARCHAR(1000))");
            statement.executeUpdate("INSERT INTO words(word,example) VALUES('Hellow','Hellow world')");

            ResultSet resultSet = statement.executeQuery("SELECT * FROM words");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("word");
                String email = resultSet.getString("example");


                if (resultSet.getString("word").equals("Hellow")){
                System.out.println("Sql is working. All is fine!");
                    }else {
                System.out.println("Something goes wrong!");
                    }

                System.out.println("We try to put into database 'Hellow' in 'word' and 'Hellow world' in 'example' ");
                System.out.println("We got from batabase \n ID: " + id + ", word: " + name + "\n example: " + email);
            }

        } catch (SQLException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

            //File must be passed in the project folder
            Path inputPath = Paths.get("input.txt");

            try (BufferedReader reader = Files.newBufferedReader(inputPath)) {

                String line;

                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }

                System.out.println("All is great!");

            } catch (IOException e) {
                System.out.println("Errore with : " + e.getMessage());
            } finally {
                System.out.println("Done");


            }





    }
}