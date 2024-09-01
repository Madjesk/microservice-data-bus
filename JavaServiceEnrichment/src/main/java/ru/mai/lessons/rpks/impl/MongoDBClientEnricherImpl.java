package ru.mai.lessons.rpks.impl;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import ru.mai.lessons.rpks.MongoDBClientEnricher;

@Slf4j
public class MongoDBClientEnricherImpl implements MongoDBClientEnricher {
    private final MongoClient mongoClient;
    private final MongoCollection<Document> mongoCollection;


    public MongoDBClientEnricherImpl(Config config) {
        mongoClient = MongoClients.create(config.getString("mongo.connectionString"));
        MongoDatabase mongoDatabase = mongoClient.getDatabase(config.getString("mongo.database"));
        mongoCollection = mongoDatabase.getCollection(config.getString("mongo.collection"));
    }

    @Override
    public Document getEnrichRules(Document doc) {
        return mongoCollection.find(doc).sort(Sorts.descending("_id")).first();
    }

    @Override
    public void closeClient() {
        mongoClient.close();
    }
}