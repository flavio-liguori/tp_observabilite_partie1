# TP Observabilité - Partie 1 : Logging & Profilage Utilisateur

Projet backend Java illustrant l'instrumentation de code via Spoon, le logging structuré JSON et le profilage comportemental.

## Stack Technique
* **Langage** : Java 17
* **Build** : Maven
* **Base de données** : MongoDB (Pilote Sync)
* **Instrumentation** : Spoon

## Installation & Exécution

Assurez-vous d'avoir Java 17 et Maven installés.

1.  **Instrumentation du code** (Injection des logs JSON via Spoon) :
    ```bash
    ./run_instrumented.sh
    ```

2.  **Génération de trafic** (Simulation de 10 utilisateurs/scénarios) :
    ```bash
    ./run_simulation.sh
    ```
    *Génère le fichier `logs/simulation.log`.*

3.  **Analyse des profils** (Parsing des logs et catégorisation) :
    ```bash
    ./run_analysis.sh
    ```
