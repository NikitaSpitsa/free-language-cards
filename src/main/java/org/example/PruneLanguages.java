package org.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.WriteModel;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class PruneLanguages {

    public static void pruneLanguages(String urlMongoDb, String databaseName, String collectionName, String languageToLeave) {

        try (MongoClient mongoClient = MongoClients.create(urlMongoDb)) {
            MongoCollection<Document> mongoDb = mongoClient.getDatabase(databaseName).getCollection(collectionName);

            List<WriteModel<Document>> bulkUpdates = new ArrayList<>();

            for (Document doc : mongoDb.find()) {
                if (doc.getList("translations", Document.class) != null) {

                    UpdateOneModel<Document> e = processDocument(languageToLeave, doc);

                    bulkUpdates.add(e);
                }
            }

            if (!bulkUpdates.isEmpty()) {
                mongoDb.bulkWrite(bulkUpdates);
            }

        }

    }

    private static UpdateOneModel<Document> processDocument(String languageToLeave, Document doc) {
        Object objectId = doc.getObjectId("_id");

        List<Document> listOfTranslations = doc.getList("translations", Document.class);

        List<Document> listOfResultTranslations = filterCollection(listOfTranslations, languageToLeave);

        return new UpdateOneModel<>(
                Filters.eq("_id", objectId), new Document("$set", new Document("translations", listOfResultTranslations)));

    }


    private static List<Document> filterCollection(List<Document> listOfTranslations, String languageToLeave) {
        List<Document> resultTranslationsList = new ArrayList<>();

        for (Document objectFromTranslationsList : listOfTranslations) {
            if (languageToLeave.equals(objectFromTranslationsList.getString("lang"))) {
                resultTranslationsList.add(objectFromTranslationsList);
            }
        }

        return resultTranslationsList;
    }


}
