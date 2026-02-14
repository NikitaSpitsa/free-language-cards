package org.example;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.dao.ConfigLoader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PruneLanguagesTest {

    public static final String DATABASE_NAME = "jUnitTestDataBase";
    public static final String COLLECTION_NAME = "jUnitTestCollection";
    public static final String LANGUAGE_TO_LEAVE = "Russian";

    static final String urlMongoDB = ConfigLoader.getProperty("mongoDb.url");

    private static final Document DOC_WITH_DATA_NEED_TO_UPDATE = new Document()
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
    private static final Document DOC_WITH_DATA_NO_NEED_TO_UPDATE = new Document()
            .append("_id", new ObjectId("274c2c2c2c2c2c2c2c2c2c2c"))
            .append("pos", "verb")
            .append("head_templates", List.of(
                    new Document()
                            .append("name", "fr-verb")
                            .append("args", new Document("conjugation", "first group"))
                            .append("expansion", "parler (je parle, tu parles, il parle)")
            ))
            .append("translations", List.of(
                    new Document()
                            .append("lang", "Russian")
                            .append("code", "ru")
                            .append("lang_code", "ru")
                            .append("sense", "a written work of fiction or nonfiction")
                            .append("word", "книга")
                            .append("_dis1", "45 20 15 10 5 5")
            ));
    private static final Document DOC_WITHOUT_DATA_AND_NONEED_TO_UPDATE = new Document()
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
            .append("lang_code", "es");


    @BeforeAll
    static void setup() {
        try (MongoClient mongoClient = MongoClients.create(urlMongoDB)) {
            mongoClient.getDatabase("jUnitTestDataBase").drop();
            mongoClient.getDatabase("jUnitTestDataBase").getCollection("jUnitTestCollection").insertOne(new Document("word", "test"));

        }
    }

    @AfterAll
    static void tearDown() {
        try (MongoClient mongoClient = MongoClients.create(urlMongoDB)) {
            mongoClient.getDatabase("jUnitTestDataBase").drop();
        }
    }

    @DisplayName("Testing MongoDB connection")
    @Test
    void testConnectionToDatabase() {
        assertDoesNotThrow(() -> {
            try (MongoClient mongoClient = MongoClients.create(urlMongoDB)) {
                assertEquals("jUnitTestDataBase",
                        mongoClient.getDatabase("jUnitTestDataBase").getName());
            }
        });

        //try to connect with wrong password
        assertThrows(MongoException.class, () -> {
            try (MongoClient mongoClient = MongoClients.create("mongodb://root:exale@localhost:27017/")) {
                mongoClient.listDatabaseNames().first();
            }
        });

        //try to connect with wrong login
        assertThrows(MongoException.class, () -> {
            try (MongoClient mongoClient = MongoClients.create("mongodb://rot:example@localhost:27017/")) {
                mongoClient.listDatabaseNames().first();
            }
        });

    }


    @DisplayName("Testing MongoDB CRUD Operations")
    @Test
    void testMongoDBCRUD() {
        try (MongoClient mongoClient = MongoClients.create(urlMongoDB)) {
            MongoCollection<Document> mongoDB = mongoClient.getDatabase("jUnitTestDataBase").getCollection("jUnitTestCollection");

            Document newDocument = new Document()
                    .append("_id", new ObjectId("674c2c2c2c2c2c2c2c2c2c2c"))
                    .append("pos", "noun")
                    .append("translations", List.of(
                            new Document()
                                    .append("lang", "Russian"),
                            new Document()
                                    .append("lang", "Kannada")
                    ));

            //try to create
            mongoDB.insertOne(newDocument);
            Document documentFromMongoDB = mongoDB.find(newDocument).first();
            assertEquals(documentFromMongoDB, newDocument);


            //tru to read and update
            mongoDB.updateOne(Filters.eq("_id", new ObjectId("674c2c2c2c2c2c2c2c2c2c2c")), new Document("$set", new Document("pos", "verb.verb.verb")));
            Document updatedDocument = mongoDB.find(Filters.eq("pos", "verb.verb.verb")).first();
            assertEquals("verb.verb.verb", updatedDocument.getString("pos"));

            //try to delete
            mongoDB.deleteOne(Filters.eq("_id", new ObjectId("674c2c2c2c2c2c2c2c2c2c2c")));
            Document deletedDocument = mongoDB.find(Filters.eq("_id", new ObjectId("674c2c2c2c2c2c2c2c2c2c2c"))).first();
            assertNull(deletedDocument);

        }
    }


    @DisplayName("Testing PruneLanguagesToRussian functionality")
    @Test
    void testPruneLanguagesToRussian() {

        // arrange

        addDataForTest();

        try (MongoClient mongoClient = MongoClients.create(urlMongoDB)) {

            // act
            PruneLanguages.pruneLanguages(urlMongoDB, DATABASE_NAME, COLLECTION_NAME, LANGUAGE_TO_LEAVE);

            // assert
            MongoCollection<Document> testCollection = mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);


            assertEquals(DOC_WITH_DATA_NEED_TO_UPDATE, testCollection.find(Filters.eq("_id", new ObjectId("174c2c2c2c2c2c2c2c2c2c2c"))).first());
            assertEquals(DOC_WITH_DATA_NO_NEED_TO_UPDATE, testCollection.find(Filters.eq("_id", new ObjectId("274c2c2c2c2c2c2c2c2c2c2c"))).first());
            assertEquals(DOC_WITHOUT_DATA_AND_NONEED_TO_UPDATE, testCollection.find(Filters.eq("_id", new ObjectId("374c2c2c2c2c2c2c2c2c2c2c"))).first());

        }


    }

    static void addDataForTest() {

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
                                .append("_dis1", "45 20 15 10 5 5")
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
                .append("lang_code", "es");

        try (MongoClient mongoClient = MongoClients.create(urlMongoDB)) {
            MongoCollection<Document> mongoDB = mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);

            mongoDB.insertMany(List.of(newDocument1, newDocument2, newDocument3));
        }
    }
}




