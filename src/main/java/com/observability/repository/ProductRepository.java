package com.observability.repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.observability.model.Product;
import com.observability.exception.ProductNotFoundException;
import com.observability.exception.ProductAlreadyExistsException;
import org.bson.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
    private final MongoCollection<Document> collection;
    private final MongoClient mongoClient;

    public ProductRepository() {
        // Connexion à la base de données Atlas
        // Attention: le mot de passe est en clair ici, idéalement il faudrait une
        // variable d'env
        String uri = "mongodb+srv://flavioliguoripro:niNhTiaLJZTXx4Am@cluster0.09ehpdm.mongodb.net/?appName=Cluster0";
        this.mongoClient = MongoClients.create(uri);
        MongoDatabase database = mongoClient.getDatabase("projetJava");
        this.collection = database.getCollection("products");
    }

    public void save(Product product) throws ProductAlreadyExistsException {
        Document existing = collection.find(Filters.eq("_id", product.getId())).first();
        if (existing != null) {
            throw new ProductAlreadyExistsException("L'ID " + product.getId() + " existe déjà !");
        }
        // Création du document pour MongoDB
        Document doc = new Document("_id", product.getId())
                .append("name", product.getName())
                .append("price", product.getPrice())
                .append("expirationDate", product.getExpirationDate().toString());
        collection.insertOne(doc);
    }

    public void delete(String id) throws ProductNotFoundException {
        Document result = collection.findOneAndDelete(Filters.eq("_id", id));
        if (result == null) {
            throw new ProductNotFoundException("Impossible de supprimer : ID " + id + " introuvable.");
        }
    }

    public void update(Product product) throws ProductNotFoundException {
        // On recrée le document pour remplacer l'ancien
        Document doc = new Document("_id", product.getId())
                .append("name", product.getName())
                .append("price", product.getPrice())
                .append("expirationDate", product.getExpirationDate().toString());

        Document result = collection.findOneAndReplace(Filters.eq("_id", product.getId()), doc);
        if (result == null) {
            throw new ProductNotFoundException("Mise à jour impossible : produit non trouvé.");
        }
    }

    public Product findById(String id) throws ProductNotFoundException {
        Document doc = collection.find(Filters.eq("_id", id)).first();
        if (doc == null) {
            throw new ProductNotFoundException("Produit " + id + " inconnu au bataillon.");
        }
        return mapToProduct(doc);
    }

    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        // On parcourt tous les documents de la collection
        for (Document doc : collection.find()) {
            products.add(mapToProduct(doc));

        }
        return products;
    }

    // Méthode utilitaire pour transformer un Document Mongo en objet Java
    private Product mapToProduct(Document doc) {
        // Parfois l'id est un ObjectId (généré) ou un String (manuel), on assure le
        // coup avec toString()
        String id = doc.get("_id").toString();
        String name = doc.getString("name");
        // Gestion des types numériques (parfois Double, parfois Integer selon
        // l'insertion)
        double price = 0.0;
        if (doc.get("price") != null) {
            price = doc.get("price", Number.class).doubleValue();
        }

        String dateStr = doc.getString("expirationDate");
        LocalDate expirationDate = LocalDate.now(); // Date par défaut si vide
        if (dateStr != null) {
            expirationDate = LocalDate.parse(dateStr);
        }

        return new Product(id, name, price, expirationDate);
    }

    public void close() {
        mongoClient.close();
    }
}
