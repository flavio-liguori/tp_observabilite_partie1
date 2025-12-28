package com.observability.repository;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.observability.exception.ProductAlreadyExistsException;
import com.observability.exception.ProductNotFoundException;
import com.observability.model.Product;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
public class ProductRepository {
    private final MongoCollection<Document> collection;

    private final MongoClient mongoClient;

    public ProductRepository() {
        // Connection string provided by user (password inserted)
        String uri = "mongodb+srv://flavioliguoripro:niNhTiaLJZTXx4Am@cluster0.09ehpdm.mongodb.net/?appName=Cluster0";
        this.mongoClient = MongoClients.create(uri);
        MongoDatabase database = mongoClient.getDatabase("projetJava");
        this.collection = database.getCollection("products");
    }

    public void save(Product product) throws ProductAlreadyExistsException {
        String logUser = com.observability.util.UserContext.getUserId();;
        org.slf4j.LoggerFactory.getLogger(com.observability.repository.ProductRepository.class).info(String.format(java.util.Locale.US, "{\"user\": \"%s\", \"action\": \"save\", \"type\": \"WRITE\", \"data\": %s }", logUser, String.format(java.util.Locale.US, "{\"id\": \"%s\", \"price\": %.2f}", product.getId(), product.getPrice())));;
        Document existing = collection.find(Filters.eq("_id", product.getId())).first();
        if (existing != null) {
            throw new ProductAlreadyExistsException(("Product with ID " + product.getId()) + " already exists.");
        }
        Document doc = new Document("_id", product.getId()).append("name", product.getName()).append("price", product.getPrice()).append("expirationDate", product.getExpirationDate().toString());
        collection.insertOne(doc);
    }

    public void delete(String id) throws ProductNotFoundException {
        String logUser = com.observability.util.UserContext.getUserId();;
        org.slf4j.LoggerFactory.getLogger(com.observability.repository.ProductRepository.class).info(String.format(java.util.Locale.US, "{\"user\": \"%s\", \"action\": \"delete\", \"type\": \"WRITE\", \"data\": %s }", logUser, String.format("{\"id\": \"%s\"}", id)));;
        Document result = collection.findOneAndDelete(Filters.eq("_id", id));
        if (result == null) {
            throw new ProductNotFoundException(("Product with ID " + id) + " not found.");
        }
    }

    public void update(Product product) throws ProductNotFoundException {
        String logUser = com.observability.util.UserContext.getUserId();;
        org.slf4j.LoggerFactory.getLogger(com.observability.repository.ProductRepository.class).info(String.format(java.util.Locale.US, "{\"user\": \"%s\", \"action\": \"update\", \"type\": \"WRITE\", \"data\": %s }", logUser, String.format(java.util.Locale.US, "{\"id\": \"%s\", \"price\": %.2f}", product.getId(), product.getPrice())));;
        Document doc = new Document("_id", product.getId()).append("name", product.getName()).append("price", product.getPrice()).append("expirationDate", product.getExpirationDate().toString());
        Document result = collection.findOneAndReplace(Filters.eq("_id", product.getId()), doc);
        if (result == null) {
            throw new ProductNotFoundException(("Product with ID " + product.getId()) + " not found.");
        }
    }

    public Product findById(String id) throws ProductNotFoundException {
        String logUser = com.observability.util.UserContext.getUserId();;
        org.slf4j.LoggerFactory.getLogger(com.observability.repository.ProductRepository.class).info(String.format(java.util.Locale.US, "{\"user\": \"%s\", \"action\": \"findById\", \"type\": \"READ\", \"data\": %s }", logUser, String.format("{\"id\": \"%s\"}", id)));;
        Document doc = collection.find(Filters.eq("_id", id)).first();
        if (doc == null) {
            throw new ProductNotFoundException(("Product with ID " + id) + " not found.");
        }
        return mapToProduct(doc);
    }

    public List<Product> findAll() {
        String logUser = com.observability.util.UserContext.getUserId();;
        org.slf4j.LoggerFactory.getLogger(com.observability.repository.ProductRepository.class).info(String.format(java.util.Locale.US, "{\"user\": \"%s\", \"action\": \"findAll\", \"type\": \"READ\", \"data\": %s }", logUser, "{}"));;
        List<Product> products = new ArrayList<>();
        for (Document doc : collection.find()) {
            products.add(mapToProduct(doc));
        }
        return products;
    }

    private Product mapToProduct(Document doc) {
        // Fix: _id might be ObjectId (auto-generated) or String (manual). toString()
        // handles both.
        String id = doc.get("_id").toString();
        String name = doc.getString("name");
        // Handle double/int potential mismatch if data was inserted differently
        double price = 0.0;
        if (doc.get("price") != null) {
            price = doc.get("price", Number.class).doubleValue();
        }
        String dateStr = doc.getString("expirationDate");
        LocalDate expirationDate = LocalDate.now();// Default if missing

        if (dateStr != null) {
            expirationDate = LocalDate.parse(dateStr);
        }
        return new Product(id, name, price, expirationDate);
    }

    // Helper to close client on shutdown if needed, though simple CLI might not
    // call it explicitly
    public void close() {
        String logUser = com.observability.util.UserContext.getUserId();;
        org.slf4j.LoggerFactory.getLogger(com.observability.repository.ProductRepository.class).info(String.format(java.util.Locale.US, "{\"user\": \"%s\", \"action\": \"close\", \"type\": \"READ\", \"data\": %s }", logUser, "{}"));;
        mongoClient.close();
    }
}