package org.example;

import org.example.dao.ConfigLoader;

public class ApplicationStarter {
    static void main(String[] args) {

        System.out.print("Starting our application \n");

        String fileName = "input.txt";

        String sqlDbUrl = ConfigLoader.getProperty("sqlDb.url");
        String sqlDbUsername = ConfigLoader.getProperty("sqlDb.user");
        String sqlDbPassword = ConfigLoader.getProperty("sqlDb.password");


        String mongoDbUrl = ConfigLoader.getProperty("mongoDb.url");

        String mongoDatabaseName = "WorkKaikki";
        String mongoCollectionName = "";
        String languageToLeave = "Russian";

        System.out.println("""
                Choose 1 if you want work with txt file and add all words from it to sql table.
                Choose 2 if you want work with mongo base to prune unused languages.
                """
        );
        int namber = 0;
        switch (namber) {
            case 1:
                SqlWordsTable.textToSqlTable(sqlDbUrl, sqlDbUsername, sqlDbPassword, fileName);
                break;
            case 2:
                PruneLanguages.pruneLanguages(mongoDbUrl, mongoDatabaseName, mongoCollectionName, languageToLeave);
                break;

            default:
                System.out.println("You need to choose 1 or 2");
        }


    }
}




