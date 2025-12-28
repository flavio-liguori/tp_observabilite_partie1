package com.observability;

import com.observability.exception.ProductAlreadyExistsException;
import com.observability.exception.ProductNotFoundException;
import com.observability.model.Product;
import com.observability.repository.ProductRepository;
import com.observability.util.UserContext;

import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    private static final ProductRepository repository = new ProductRepository();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Application de Gestion de Produits ===");
        if (login()) {
            runMenu();
        } else {
            System.out.println("Échec de la connexion. Fin du programme.");
        }
    }

    private static boolean login() {
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        // Simulation simple de connexion
        if (!email.isEmpty() && !password.isEmpty()) {
            System.out.println("Connexion réussie !");
            // Set User ID for logging context (using email as ID for simplicity here)
            UserContext.setUserId(email);
            return true;
        }
        return false;
    }

    private static void runMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- MENU ---");
            System.out.println("1. Ajouter un produit");
            System.out.println("2. Mettre à jour un produit");
            System.out.println("3. Supprimer un produit");
            System.out.println("4. Afficher un produit");
            System.out.println("5. Lister tous les produits");
            System.out.println("6. Quitter");
            System.out.print("Choix: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    addProduct();
                    break;
                case "2":
                    updateProduct();
                    break;
                case "3":
                    deleteProduct();
                    break;
                case "4":
                    viewProduct();
                    break;
                case "5":
                    listProducts();
                    break;
                case "6":
                    running = false;
                    System.out.println("Au revoir !");
                    break;
                default:
                    System.out.println("Option invalide.");
            }
        }
    }

    private static void addProduct() {
        try {
            System.out.print("ID: ");
            String id = scanner.nextLine();
            System.out.print("Nom: ");
            String name = scanner.nextLine();
            System.out.print("Prix: ");
            double price = Double.parseDouble(scanner.nextLine());

            // For simplicity, we'll set expiration date to 30 days from now or parse
            // simulated input
            // Keeping it simple as per "scolaire"
            LocalDate expirationDate = LocalDate.now().plusDays(30);

            Product product = new Product(id, name, price, expirationDate);
            repository.save(product);
            System.out.println("Produit ajouté avec succès.");
        } catch (NumberFormatException e) {
            System.out.println("Erreur: Format de nombre invalide.");
        } catch (ProductAlreadyExistsException e) {
            System.out.println("Erreur: " + e.getMessage());
        }
    }

    private static void updateProduct() {
        try {
            System.out.print("ID du produit à mettre à jour: ");
            String id = scanner.nextLine();

            // Just verifying it exists first to be user friendly, logic is in repository
            // though
            Product existing = repository.findById(id);

            System.out.print("Nouveau Nom (" + existing.getName() + "): ");
            String name = scanner.nextLine();
            if (name.isEmpty())
                name = existing.getName();

            System.out.print("Nouveau Prix (" + existing.getPrice() + "): ");
            String priceStr = scanner.nextLine();
            double price = priceStr.isEmpty() ? existing.getPrice() : Double.parseDouble(priceStr);

            Product product = new Product(id, name, price, existing.getExpirationDate());
            repository.update(product);
            System.out.println("Produit mis à jour.");
        } catch (ProductNotFoundException e) {
            System.out.println("Erreur: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Erreur: Format de nombre invalide.");
        }
    }

    private static void deleteProduct() {
        try {
            System.out.print("ID du produit à supprimer: ");
            String id = scanner.nextLine();
            repository.delete(id);
            System.out.println("Produit supprimé.");
        } catch (ProductNotFoundException e) {
            System.out.println("Erreur: " + e.getMessage());
        }
    }

    private static void viewProduct() {
        try {
            System.out.print("ID du produit: ");
            String id = scanner.nextLine();
            Product product = repository.findById(id);
            System.out.println("Détails: " + product);
        } catch (ProductNotFoundException e) {
            System.out.println("Erreur: " + e.getMessage());
        }
    }

    private static void listProducts() {
        repository.findAll().forEach(System.out::println);
    }
}
