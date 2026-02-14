package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SqlWordsTable {

    public static void textToSqlTable(String urlSqlDb, String usernameSqlDb, String passwordSqlDb, String fileName) {
        List<String> sentences = textToSentencesList(fileName);
        HashMap<String, String> wordsMap = sentansesToHashMap(sentences);
        addWordsToSqlTable(urlSqlDb, usernameSqlDb, passwordSqlDb, wordsMap);
    }

    private static List<String> textToSentencesList(String fileName) {
        List<String> sentences = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("directory_for_text_files/" + fileName))) {
            String line;
            StringBuilder currentSentence = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                currentSentence.append(line).append(" ");

                String[] parts = currentSentence.toString().split("(?<=[!.?])+");

                sentences.addAll(Arrays.asList(parts).subList(0, parts.length - 1));

                if (parts.length > 0) {
                    currentSentence = new StringBuilder(parts[parts.length - 1]);
                }
            }

            if (!currentSentence.isEmpty()) {
                sentences.add(currentSentence.toString().trim());

            }
        } catch (
                IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return sentences;

    }

    private static   HashMap<String, String> sentansesToHashMap(List<String> sentences) {
        HashMap<String, String> wordsMap = new HashMap<>();
        for (String sentence : sentences) {

            String[] words = sentence.split("[\\s,.!?()-]+");

            for (String word : words) {
                word = word.toLowerCase();

                if (word.matches(".*\\d.*")) {
                    continue;
                }

                if (!wordsMap.containsKey(word)) {
                    wordsMap.put(word, sentence);
                } else if (words.length < 12 || words.length < wordsMap.get(word).length()) {
                    wordsMap.put(word, sentence);
                }

            }

        }
        return wordsMap;
    }

    private static void addWordsToSqlTable(String urlSqlDb, String usernameSqlDb, String passwordsqldb, HashMap<String, String> wordsMap) {
        try (Connection connection = DriverManager.getConnection(urlSqlDb, usernameSqlDb, passwordsqldb);
             Statement statement = connection.createStatement()) {

            wordsMap.forEach((word, example) -> {
                try {
                    statement.executeUpdate("INSERT INTO words(word,example) VALUES('" + word + "','" + example + "')");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

