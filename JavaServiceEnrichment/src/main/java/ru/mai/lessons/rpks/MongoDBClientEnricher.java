package ru.mai.lessons.rpks;

import org.bson.Document;

public interface MongoDBClientEnricher {
    Document getEnrichRules(Document doc);

    void closeClient();
}
