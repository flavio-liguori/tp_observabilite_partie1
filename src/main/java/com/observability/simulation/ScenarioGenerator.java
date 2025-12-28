package com.observability.simulation;

import com.observability.repository.ProductRepository;
import com.observability.model.Product;
import com.observability.util.UserContext;
import com.observability.exception.ProductNotFoundException;
import com.observability.exception.ProductAlreadyExistsException;

import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;

public class ScenarioGenerator {
    private static final ProductRepository repository = new ProductRepository();
    private static final Random random = new Random();

    public static void main(String[] args) {
        System.out.println("Démarrage de la simulation d'utilisateurs...");

        // On crée 10 utilisateurs fictifs
        for (int i = 1; i <= 10; i++) {
            String userId = "user" + i + "@simulation.com";

            String profile = "READER";
            if (i >= 5 && i <= 7)
                profile = "WRITER";
            if (i >= 8)
                profile = "LUXURY";

            System.out.println("Simulation pour " + userId + " [" + profile + "]");
            simulateUser(userId, profile);
        }

        System.out.println("Fin de la génération des logs.");
        repository.close();
    }

    private static void simulateUser(String userId, String profile) {
        // On set l'utilisateur courant pour que les logs Spoon le récupèrent
        UserContext.setUserId(userId);

        // Chaque utilisteur fait 20 actions
        for (int j = 0; j < 20; j++) {
            try {
                // Logique de simulation selon le profil
                if (profile.equals("READER")) {
                    if (random.nextDouble() < 0.8) {
                        doRead();
                    } else {
                        doWrite(false);
                    }
                } else if (profile.equals("WRITER")) {
                    if (random.nextDouble() < 0.70) {
                        doWrite(false);
                    } else {
                        doRead();
                    }
                } else if (profile.equals("LUXURY")) {
                    if (random.nextDouble() < 0.5) {
                        doWrite(true); // Prix élevé
                    } else {
                        doRead(); // Read (maybe specific expensive ones, simplified here)
                    }
                }
            } catch (Exception e) {
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
    }

    private static void doRead() {
        if (random.nextBoolean()) {
            repository.findAll();
        } else {
            try {
                repository.findById("sim_" + random.nextInt(100));
            } catch (ProductNotFoundException e) {
            }
        }
    }

    private static void doWrite(boolean expensive) throws Exception {
        String id = "sim_" + random.nextInt(100);
        double price = 10.0 + random.nextInt(50);
        if (expensive) {
            price = 1000.0 + random.nextInt(5000);
        }

        Product p = new Product(id, "SimProduct-" + id, price, LocalDate.now().plusDays(365));

        int action = random.nextInt(3);
        // 0=save, 1=update, 2=delete

        switch (action) {
            case 0:
                try {
                    repository.save(p);
                } catch (ProductAlreadyExistsException e) {
                    repository.update(p);
                }
                break;
            case 1:
                try {
                    repository.update(p);
                } catch (ProductNotFoundException e) {
                }
                break;
            case 2:
                try {
                    repository.delete(id);
                } catch (ProductNotFoundException e) {
                }
                break;
        }
    }
}
