import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.example.ApplicationStarter.PruneLanguagesToRussian;
import static org.junit.jupiter.api.Assertions.*;

public class AplicationStarterTest {

    @BeforeAll
    static void setup() {
        try (MongoClient mongoClient = MongoClients.create("mongodb://root:example@localhost:27017/")) {
            mongoClient.getDatabase("jUnitTestDataBase").drop();
            mongoClient.getDatabase("jUnitTestDataBase").getCollection("jUnitTestCollection").insertOne(new Document("word", "test"));

        }
    }

    @AfterAll
    static void tearDown() {
        try (MongoClient mongoClient = MongoClients.create("mongodb://root:example@localhost:27017/")) {
            mongoClient.getDatabase("jUnitTestDataBase").drop();
        }
    }

    @DisplayName("Testing MongoDB connection")
    @Test
    void testConnectionToDatabase() {
        assertDoesNotThrow(() -> {
            try (MongoClient mongoClient = MongoClients.create("mongodb://root:example@localhost:27017/")) {
                assertEquals("jUnitTestDataBase",
                        mongoClient.getDatabase("jUnitTestDataBase").getName());
            }
        });

        assertThrows(MongoException.class, () -> {
            try (MongoClient mongoClient = MongoClients.create("mongodb://rot:example@localhost:27017/")) {
                mongoClient.listDatabaseNames().first();
            }
        });

    }


    @DisplayName("Testing MongoDB CRUD Operations")
    @Test
    void testMongoDBCRUD() {
        try (MongoClient mongoClient = MongoClients.create("mongodb://root:example@localhost:27017/")) {
            MongoCollection<Document> mongoDB = mongoClient.getDatabase("jUnitTestDataBase").getCollection("jUnitTestCollection");

            Document newDocument = new Document()
                    .append("_id", new ObjectId("674c2c2c2c2c2c2c2c2c2c2c"))
                    .append("pos", "noun")
                    .append("translations", List.of(
                            new Document()
                                    .append("lang", "Russian")
                                    .append("code", "xal")
                                    .append("lang_code", "xal")
                                    .append("sense", "publication that explains the meanings of an ordered list of words")
                                    .append("roman", "tolʹ")
                                    .append("word", "толь")
                                    .append("_dis1", "24 8 2 28 25 12"),
                            new Document()
                                    .append("lang", "Kannada")
                                    .append("code", "kn")
                                    .append("lang_code", "kn")
                                    .append("sense", "publication that explains the meanings of an ordered list of words")
                                    .append("roman", "nighaṇṭu")
                                    .append("word", "ನಿಘಂಟು")
                                    .append("_dis1", "24 8 2 28 25 12")
                    ));

            mongoDB.insertOne(newDocument);
            Document documentFromMongoDB = mongoDB.find(newDocument).first();
            assertNotNull(documentFromMongoDB);

            mongoDB.updateOne(Filters.eq("_id", new ObjectId("674c2c2c2c2c2c2c2c2c2c2c")), new Document("$set", new Document("pos", "verb.verb.verb")));
            Document updatedDocument = mongoDB.find(Filters.eq("pos", "verb.verb.verb")).first();
            assertEquals("verb.verb.verb", updatedDocument.getString("pos"));

            mongoDB.deleteOne(Filters.eq("_id", new ObjectId("674c2c2c2c2c2c2c2c2c2c2c")));
            Document deletedDocument = mongoDB.find(Filters.eq("_id", new ObjectId("674c2c2c2c2c2c2c2c2c2c2c"))).first();
            assertNull(deletedDocument);

        }
    }


    @DisplayName("Testing PruneLanguagesToRussian functionality")
    @Test
    void testPruneLanguagesToRussian() {

        String urlMongoDB = "mongodb://root:example@localhost:27017/";
        String databaseName = "jUnitTestDataBase";
        String collectionName = "jUnitTestCollection";
        String collectionOfExamples = "jUnitExampleCollectionForTest";


        Document newDocument1 = new Document()
                .append("_id", new ObjectId("174c2c2c2c2c2c2c2c2c2c2c"))
                .append("pos", "noun")
                .append("head_templates", List.of(
                        new Document()
                                .append("name", "en-noun")
                                .append("args", new Document())
                                .append("expansion", "dictionary (plural dictionaries)")
                ))
                .append("word", "book")
                .append("lang", "English")
                .append("lang_code", "en")
                .append("translations", List.of(
                        new Document()
                                .append("lang", "Russian")
                                .append("code", "ru")
                                .append("lang_code", "ru")
                                .append("sense", "a written work of fiction or nonfiction")
                                .append("word", "книга")
                                .append("_dis1", "45 20 15 10 5 5"),
                        new Document()
                                .append("lang", "Japanese")
                                .append("code", "ja")
                                .append("lang_code", "ja")
                                .append("sense", "a written work")
                                .append("word", "本")
                                .append("_dis1", "50 20 15 10 3 2")
                ));

        Document newDocument2 = new Document()
                .append("_id", new ObjectId("274c2c2c2c2c2c2c2c2c2c2c"))
                .append("pos", "verb")
                .append("head_templates", List.of(
                        new Document()
                                .append("name", "fr-verb")
                                .append("args", new Document("conjugation", "first group"))
                                .append("expansion", "parler (je parle, tu parles, il parle)")
                ))
                .append("word", "parler")
                .append("lang", "French")
                .append("lang_code", "fr")
                .append("translations", List.of(
                        new Document()
                                .append("lang", "German")
                                .append("code", "de")
                                .append("lang_code", "de")
                                .append("sense", "to speak or talk")
                                .append("word", "sprechen")
                                .append("_dis1", "30 25 20 15 5 5"),
                        new Document()
                                .append("lang", "Japanese")
                                .append("code", "ja")
                                .append("lang_code", "ja")
                                .append("sense", "a written work")
                                .append("word", "本")
                                .append("_dis1", "50 20 15 10 3 2"),
                        new Document()
                                .append("lang", "Russian")
                                .append("code", "ru")
                                .append("lang_code", "ru")
                                .append("sense", "a written work of fiction or nonfiction")
                                .append("word", "книга")
                                .append("_dis1", "45 20 15 10 5 5")
                ));

        Document newDocument3 = new Document()
                .append("_id", new ObjectId("374c2c2c2c2c2c2c2c2c2c2c"))
                .append("pos", "adjective")
                .append("head_templates", List.of(
                        new Document()
                                .append("name", "es-adj")
                                .append("args", new Document("gender", "mf"))
                                .append("expansion", "grande (m and f plural grandes)")
                ))
                .append("word", "grande")
                .append("lang", "Spanish")
                .append("lang_code", "es")
                .append("translations", List.of(
                        new Document()
                                .append("lang", "Italian")
                                .append("code", "it")
                                .append("lang_code", "it")
                                .append("sense", "large in size")
                                .append("word", "grande")
                                .append("_dis1", "40 30 15 10 3 2"),
                        new Document()
                                .append("lang", "English")
                                .append("code", "en")
                                .append("lang_code", "en")
                                .append("sense", "large in size")
                                .append("word", "big")
                                .append("_dis1", "35 25 20 15 3 2"),
                        new Document()
                                .append("lang", "Japanese")
                                .append("code", "ja")
                                .append("lang_code", "ja")
                                .append("sense", "a written work")
                                .append("word", "本")
                                .append("_dis1", "50 20 15 10 3 2"),
                        new Document()
                                .append("lang", "Japanese")
                                .append("code", "ja")
                                .append("lang_code", "ja")
                                .append("sense", "a written work")
                                .append("word", "本")
                                .append("_dis1", "50 20 15 10 3 2"),
                        new Document()
                                .append("lang", "Russian")
                                .append("code", "ru")
                                .append("lang_code", "ru")
                                .append("sense", "a written work of fiction or nonfiction")
                                .append("word", "книга")
                                .append("_dis1", "45 20 15 10 5 5")
                ));

        Document newDocument4 = new Document()
                .append("_id", new ObjectId("474c2c2c2c2c2c2c2c2c2c2c"))
                .append("pos", "noun")
                .append("head_templates", List.of(
                        new Document()
                                .append("name", "zh-noun")
                                .append("args", new Document("classifier", "本"))
                                .append("expansion", "书 (shū)")
                ))
                .append("word", "书")
                .append("lang", "Chinese")
                .append("lang_code", "zh")
                .append("translations", List.of(
                        new Document()
                                .append("lang", "Japanese")
                                .append("code", "ja")
                                .append("lang_code", "ja")
                                .append("sense", "a written work")
                                .append("word", "本")
                                .append("_dis1", "50 20 15 10 3 2"),
                        new Document()
                                .append("lang", "Japanese")
                                .append("code", "ja")
                                .append("lang_code", "ja")
                                .append("sense", "a written work")
                                .append("word", "本")
                                .append("_dis1", "50 20 15 10 3 2"),
                        new Document()
                                .append("lang", "Russian")
                                .append("code", "ru")
                                .append("lang_code", "ru")
                                .append("sense", "a written work of fiction or nonfiction")
                                .append("word", "книга")
                                .append("_dis1", "45 20 15 10 5 5")
                ));

        Document newDocument5 = new Document()
                .append("_id", new ObjectId("574c2c2c2c2c2c2c2c2c2c2c"))
                .append("pos", "noun")
                .append("head_templates", List.of(
                        new Document()
                                .append("name", "ar-noun")
                                .append("args", new Document("plural", "كُتُب"))
                                .append("expansion", "كِتَاب (kitāb)")
                ))
                .append("word", "كتاب")
                .append("lang", "Arabic")
                .append("lang_code", "ar")
                .append("translations", List.of(
                        new Document()
                                .append("lang", "English")
                                .append("code", "en")
                                .append("lang_code", "en")
                                .append("sense", "a book or written document")
                                .append("word", "book")
                                .append("_dis1", "60 15 10 8 5 2"),
                        new Document()
                                .append("lang", "Japanese")
                                .append("code", "ja")
                                .append("lang_code", "ja")
                                .append("sense", "a written work")
                                .append("word", "本")
                                .append("_dis1", "50 20 15 10 3 2"),
                        new Document()
                                .append("lang", "Russian")
                                .append("code", "ru")
                                .append("lang_code", "ru")
                                .append("sense", "a written work of fiction or nonfiction")
                                .append("word", "книга")
                                .append("_dis1", "45 20 15 10 5 5")
                ));


        Document exampleDocument1 = new Document()
                .append("_id", new ObjectId("174c2c2c2c2c2c2c2c2c2c2c"))
                .append("pos", "noun")
                .append("head_templates", List.of(
                        new Document()
                                .append("name", "en-noun")
                                .append("args", new Document())
                                .append("expansion", "dictionary (plural dictionaries)")
                ))
                .append("word", "book")
                .append("lang", "English")
                .append("lang_code", "en")
                .append("translations", List.of(
                        new Document()
                                .append("lang", "Russian")
                                .append("code", "ru")
                                .append("lang_code", "ru")
                                .append("sense", "a written work of fiction or nonfiction")
                                .append("word", "книга")
                                .append("_dis1", "45 20 15 10 5 5")
                ));

        Document exampleDocument2 = new Document()
                .append("_id", new ObjectId("274c2c2c2c2c2c2c2c2c2c2c"))
                .append("pos", "verb")
                .append("head_templates", List.of(
                        new Document()
                                .append("name", "fr-verb")
                                .append("args", new Document("conjugation", "first group"))
                                .append("expansion", "parler (je parle, tu parles, il parle)")
                ))
                .append("word", "parler")
                .append("lang", "French")
                .append("lang_code", "fr")
                .append("translations", List.of(
                        new Document()
                                .append("lang", "Russian")
                                .append("code", "ru")
                                .append("lang_code", "ru")
                                .append("sense", "a written work of fiction or nonfiction")
                                .append("word", "книга")
                                .append("_dis1", "45 20 15 10 5 5")
                ));

        Document exampleDocument3 = new Document()
                .append("_id", new ObjectId("374c2c2c2c2c2c2c2c2c2c2c"))
                .append("pos", "adjective")
                .append("head_templates", List.of(
                        new Document()
                                .append("name", "es-adj")
                                .append("args", new Document("gender", "mf"))
                                .append("expansion", "grande (m and f plural grandes)")
                ))
                .append("word", "grande")
                .append("lang", "Spanish")
                .append("lang_code", "es")
                .append("translations", List.of(
                        new Document()
                                .append("lang", "Russian")
                                .append("code", "ru")
                                .append("lang_code", "ru")
                                .append("sense", "a written work of fiction or nonfiction")
                                .append("word", "книга")
                                .append("_dis1", "45 20 15 10 5 5")
                ));

        Document exampleDocument4 = new Document()
                .append("_id", new ObjectId("474c2c2c2c2c2c2c2c2c2c2c"))
                .append("pos", "noun")
                .append("head_templates", List.of(
                        new Document()
                                .append("name", "zh-noun")
                                .append("args", new Document("classifier", "本"))
                                .append("expansion", "书 (shū)")
                ))
                .append("word", "书")
                .append("lang", "Chinese")
                .append("lang_code", "zh")
                .append("translations", List.of(
                        new Document()
                                .append("lang", "Russian")
                                .append("code", "ru")
                                .append("lang_code", "ru")
                                .append("sense", "a written work of fiction or nonfiction")
                                .append("word", "книга")
                                .append("_dis1", "45 20 15 10 5 5")
                ));

        Document exampleDocument5 = new Document()
                .append("_id", new ObjectId("574c2c2c2c2c2c2c2c2c2c2c"))
                .append("pos", "noun")
                .append("head_templates", List.of(
                        new Document()
                                .append("name", "ar-noun")
                                .append("args", new Document("plural", "كُتُب"))
                                .append("expansion", "كِتَاب (kitāb)")
                ))
                .append("word", "كتاب")
                .append("lang", "Arabic")
                .append("lang_code", "ar")
                .append("translations", List.of(
                        new Document()
                                .append("lang", "Russian")
                                .append("code", "ru")
                                .append("lang_code", "ru")
                                .append("sense", "a written work of fiction or nonfiction")
                                .append("word", "книга")
                                .append("_dis1", "45 20 15 10 5 5")
                ));


        try (MongoClient mongoClient = MongoClients.create(urlMongoDB)) {
            MongoCollection<Document> mongoDB = mongoClient.getDatabase(databaseName).getCollection(collectionName);

            mongoDB.insertMany(List.of(newDocument1, newDocument2, newDocument3, newDocument4, newDocument5));

            MongoCollection<Document> mongoDBExampleCollection = mongoClient.getDatabase(databaseName).getCollection(collectionOfExamples);

            mongoDBExampleCollection.insertMany(List.of(exampleDocument1, exampleDocument2, exampleDocument3, exampleDocument4, exampleDocument5));

            PruneLanguagesToRussian(urlMongoDB, databaseName, collectionName);

            MongoCollection<Document> testCollection = mongoClient.getDatabase(databaseName).getCollection(collectionName);

         MongoCollection<Document> exampleCollection = mongoClient.getDatabase(databaseName).getCollection(collectionOfExamples);

         for (Document doc : exampleCollection.find()){
                String exampleDoc = exampleCollection.find(Filters.eq("_id", doc.get("_id"))).first().toString();
                String testingDoc = testCollection.find(Filters.eq("_id", doc.get("_id"))).first().toString();
                assertEquals(exampleDoc, testingDoc);
            }
        }

    }
}




