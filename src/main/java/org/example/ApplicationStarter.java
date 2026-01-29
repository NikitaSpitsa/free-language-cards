package org.example;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class ApplicationStarter {
    public static void main(String[] args) {

        System.out.print("Starting our application \n");

//        String urlSqlDB = "jdbc:mysql://localhost:3333/free_cards_mySql";
//        String usernameSqlDB = "root";
//        String passwordSqlDB = "root";
//
//        List<String> sentences = new ArrayList<>();
//
//        HashMap<String, String> wordsMap = new HashMap<>();
//
//        try (Connection connection = DriverManager.getConnection(urlSqlDB, usernameSqlDB, passwordSqlDB);
//             Statement statement = connection.createStatement()) {
//
//            System.out.println("Let's check Sql tables");
//            statement.executeUpdate("DROP TABLE IF EXISTS words");
//            statement.executeUpdate("CREATE TABLE IF NOT EXISTS words (id INT PRIMARY KEY AUTO_INCREMENT, word VARCHAR(50), example VARCHAR(1000))");
//            statement.executeUpdate("INSERT INTO words(word,example) VALUES('Hello','Hello world')");
//            ResultSet resultSet = statement.executeQuery("SELECT * FROM words");
//
//            while (resultSet.next()) {
//                int id = resultSet.getInt("id");
//                String name = resultSet.getString("word");
//                String email = resultSet.getString("example");
//
//                System.out.println("We try to put into database 'Hello' in 'word' and 'Hello world' in 'example' ");
//                System.out.println("We got from database \n ID: " + id + ", word: " + name + "\n example: " + email);
//
//
//                if (resultSet.getString("word").equals("Hello")) {
//                    System.out.println("Sql is working correct. All is fine!");
//                } else {
//                    System.out.println("Something went wrong!");
//                }
//            }
//
//            statement.executeUpdate("TRUNCATE TABLE words");
//
//        } catch (SQLException e) {
//            System.out.println("Error: " + e.getMessage());
//        }
//
//        try (BufferedReader reader = new BufferedReader(new FileReader("directory for text files/input.txt"))) {
//            String line;
//            StringBuilder currentSentence = new StringBuilder();
//
//            while ((line = reader.readLine()) != null) {
//                currentSentence.append(line).append(" ");
//
//                String[] parts = currentSentence.toString().split("(?<=[!.?])+");
//
//                sentences.addAll(Arrays.asList(parts).subList(0, parts.length - 1));
//
//                if (parts.length > 0) {
//                    currentSentence = new StringBuilder(parts[parts.length - 1]);
//                }
//            }
//
//            if (!currentSentence.isEmpty()) {
//                sentences.add(currentSentence.toString().trim());
//            }
//
//        } catch (IOException e) {
//            System.out.println("Error: " + e.getMessage());
//        }
//
//        for (String sentence : sentences) {
//            String[] words = sentence.split("[\\s,.!?()-]+");
//
//            for (String word : words) {
//                word = word.toLowerCase();
//
//                if (word.matches(".*\\d.*")) {
//                    continue;
//                }
//
//                if (!wordsMap.containsKey(word)) {
//                    wordsMap.put(word, sentence);
//                } else if (words.length < 12 || words.length < wordsMap.get(word).length()) {
//                    wordsMap.put(word, sentence);
//                }
//
//            }
//
//        }
//
//        try (Connection connection = DriverManager.getConnection(urlSqlDB, usernameSqlDB, passwordSqlDB);
//             Statement statement = connection.createStatement()) {
//
//            wordsMap.forEach((word, example) -> {
//                try {
//                    statement.executeUpdate("INSERT INTO words(word,example) VALUES('" + word + "','" + example + "')");
//                } catch (SQLException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//        } catch (SQLException e) {
//            System.out.println("Error: " + e.getMessage());
//        }
//

        String urlMongoDB = "mongodb://root:example@localhost:27017/";
        String databaseName = "WorkKaikki";
        String collectionName = "adv";

        PruneLanguagesToRussian(urlMongoDB, databaseName, collectionName);


    }

    public static void PruneLanguagesToRussian(String urlMongoDB, String databaseName, String collectionName) {

        try (MongoClient mongoClient = MongoClients.create(urlMongoDB)) {
            MongoCollection<Document> mongoDB = mongoClient.getDatabase(databaseName).getCollection(collectionName);

            for (Document doc : mongoDB.find()) {

                Object objectId = doc.getObjectId("_id");
                System.out.println("ID: " + objectId);
                List<Document> translationsObjectList = doc.getList("translations", Document.class);

                if (translationsObjectList != null) {
                    System.out.println(translationsObjectList.size() + " records found with _id " + objectId);

                    List<Document> russionTranslationsList = new ArrayList<>();

                    for (Document translationObject : translationsObjectList) {
                        String langName = translationObject.get("lang").toString();

                        if (langName.equals("Russian")) {
                            russionTranslationsList.add(translationObject);
                        }

                        mongoDB.updateOne(
                                Filters.eq("_id", objectId),
                                new Document("$set", new Document("translations", russionTranslationsList)));

                        System.out.println("New translations list have been formed and added");

                    }
                }

            }
        }
    }
}




