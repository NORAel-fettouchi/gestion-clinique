#  Gestion Clinique JavaFX

Une application de bureau moderne et intuitive pour la gestion quotidienne d'une clinique médicale, développée en **JavaFX**.

##  Fonctionnalités
* **Gestion des Patients** : Ajout, modification et suppression des dossiers patients.
* **Prise de Rendez-vous** : Système de planification pour les consultations.
* **Suivi Médical** : Historique des traitements et statistiques des patients.
* **Interface Admin** : Authentification sécurisée pour le personnel médical.
* **Base de Données** : Persistance des données via JDBC (MySQL/SQLite).

## echnologies Utilisées
* **Langage** : Java (JDK 17+)
* **Interface Graphique** : JavaFX & CSS personnalisé
* **Architecture** : MVC (Modèle-Vue-Contrôleur) / DAO (Data Access Object)
* **Outils** : Scene Builder pour le design, Git pour le versioning.

##  Structure du Projet
```text
src/
├── design/     # Fichiers CSS pour le style de l'interface
├── models/     # Classes d'objets (Patient, Admin, RendezVous...)
├── dao/        # Gestion des accès à la base de données
├── views/      # Contrôleurs et interfaces FXML
└── Main.java   # Point d'entrée de l'application

## 🔗 Lien du projet
 https://github.com/NORAel-fettouchi/gestion-clinique.git
