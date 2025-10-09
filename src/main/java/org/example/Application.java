package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class Application {
    public static void main(String[] args) {

        System.out.print("Application starting\n");
        String url = "jdbc:mysql://localhost:3333/free_cards_mySql";
        String username = "root";
        String password = "root";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("DROP TABLE IF EXISTS words");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS words (id INT PRIMARY KEY AUTO_INCREMENT, word VARCHAR(50), example VARCHAR(1000))");
            statement.executeUpdate("INSERT INTO words(word,example) VALUES('Hello','Hello world')");

            ResultSet resultSet = statement.executeQuery("SELECT * FROM words");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("word");
                String email = resultSet.getString("example");


                if (resultSet.getString("word").equals("Hello")){
                System.out.println("Sql is working. All is fine!");
                    }else {
                System.out.println("Something went wrong!");
                    }

                System.out.println("We try to put into database 'Hello' in 'word' and 'Hellow world' in 'example' ");
                System.out.println("We got from batabase \n ID: " + id + ", word: " + name + "\n example: " + email);
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        List<String> sentences = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("directory for text files/input.txt"))) {
            String line;
            StringBuilder currentSentence = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                currentSentence.append(line).append(" ");

                String[] parts = currentSentence.toString().split("(?<=[.!?])\\s+");

                sentences.addAll(Arrays.asList(parts).subList(0, parts.length - 1));

                if (parts.length > 0) {
                    currentSentence = new StringBuilder(parts[parts.length - 1]);
                }
            }

            if (!currentSentence.isEmpty()) {
                sentences.add(currentSentence.toString().trim());
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        for (String sentence : sentences) {
            System.out.println("-");
            System.out.println(sentence);
            System.out.println("--=--");
        }



    }
}