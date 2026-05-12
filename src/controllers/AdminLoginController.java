package controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.Admin;
import models.dao.AdminDAO;
import javafx.scene.Node;
import javafx.util.StringConverter;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminLoginController implements Initializable {

    // Formulaire connexion
    @FXML private AnchorPane formulaire_connexion;
    @FXML private TextField champNomUtilisateurConnexion;
    @FXML private PasswordField champMotDePasseConnexion;
    @FXML private TextField champAffichageMotDePasseConnexion;
    @FXML private CheckBox caseAfficherConnexion;
    @FXML private Label labelMessageConnexion;

    // Formulaire inscription
    @FXML private AnchorPane formulaire_inscription;
    @FXML private TextField champNom;
    @FXML private TextField champPrenom;
    @FXML private TextField champNomUtilisateurInscription;
    @FXML private PasswordField champMotDePasseInscription;
    @FXML private TextField champAffichageMotDePasseInscription;
    @FXML private CheckBox caseAfficherInscription;
    @FXML private Label labelMessageInscription;
    @FXML private ComboBox<String> comboSpecialite; // Nouveau champ pour la spécialité

    private final AdminDAO adminDAO = new AdminDAO();
    private final String[] SPECIALITES = {
            "Généraliste",
            "Cardiologie",
            "Dermatologie",
            "Pneumologie",
            "Neurologie",
            "Pédiatrie",
            "Gastro-entérologie",
            "Médecin interne",
            "Hématologie",
            "Oncologie",
            "Gynécologie",
            "Autre..."
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialisation de la ComboBox des spécialités
        comboSpecialite.getItems().addAll(SPECIALITES);


        Platform.runLater(() -> {
            formulaire_connexion.getScene().getRoot().requestFocus();
        });



    }

    @FXML
    private void handleLogin() {
        String username = champNomUtilisateurConnexion.getText().trim();
        String password = champMotDePasseConnexion.isVisible()
                ? champMotDePasseConnexion.getText().trim()
                : champAffichageMotDePasseConnexion.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            labelMessageConnexion.setText("Veuillez remplir tous les champs.");
            return;
        }

        Admin admin = adminDAO.authentifier(username, password);
        if (admin != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/adminInterface.fxml"));
                Parent root = loader.load();

                // Transmettre l'admin connecté au dashboard
                AdminDashboardController controller = loader.getController();
                controller.setAdmin(admin);

                Stage stage = (Stage) champNomUtilisateurConnexion.getScene().getWindow();
                Scene scene = new Scene(root, 800, 600); // Taille augmentée pour meilleure ergonomie

                stage.setScene(scene);
                stage.setTitle(String.format("Interface %s - Dr. %s %s",
                        admin.getSpecialite(), admin.getPrenom(), admin.getNom()));
                stage.centerOnScreen();
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                labelMessageConnexion.setText("Erreur lors du chargement de l'interface.");
            }
        } else {
            labelMessageConnexion.setText("Identifiants incorrects.");
        }
    }

    @FXML
    private void handleRegister() {
        String nom = champNom.getText().trim();
        String prenom = champPrenom.getText().trim();
        String username = champNomUtilisateurInscription.getText().trim();
        String password = champMotDePasseInscription.isVisible()
                ? champMotDePasseInscription.getText().trim()
                : champAffichageMotDePasseInscription.getText().trim();
        String specialite = comboSpecialite.getValue();

        if (nom.isEmpty() || prenom.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showAlert("Champs manquants", "Veuillez remplir tous les champs.", Alert.AlertType.WARNING);
            return;
        }



        Admin admin = new Admin(nom, prenom, username, password, specialite);

        if (adminDAO.creerCompte(admin)) {
            showAlert("Inscription réussie", "Compte créé avec succès !", Alert.AlertType.INFORMATION);
            clearInscriptionForm();
            formulaire_inscription.setVisible(false);
            formulaire_connexion.setVisible(true);
        } else {
            showAlert("Erreur d'inscription", "Ce nom d'utilisateur existe déjà.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Style personnalisé - only if CSS file exist

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/utils/icons/warning.png")));
        alert.showAndWait();
    }

    private void clearInscriptionForm() {
        champNom.clear();
        champPrenom.clear();
        champNomUtilisateurInscription.clear();
        champMotDePasseInscription.clear();
        champAffichageMotDePasseInscription.clear();
        comboSpecialite.getSelectionModel().selectFirst();
    }

    // Méthodes existantes pour la visibilité des mots de passe
    @FXML
    private void togglePasswordVisibilityConnexion() {
        if (caseAfficherConnexion.isSelected()) {
            champAffichageMotDePasseConnexion.setText(champMotDePasseConnexion.getText());
            champAffichageMotDePasseConnexion.setVisible(true);
            champMotDePasseConnexion.setVisible(false);
        } else {
            champMotDePasseConnexion.setText(champAffichageMotDePasseConnexion.getText());
            champMotDePasseConnexion.setVisible(true);
            champAffichageMotDePasseConnexion.setVisible(false);
        }
    }

    @FXML
    private void togglePasswordVisibilityInscription() {
        if (caseAfficherInscription.isSelected()) {
            champAffichageMotDePasseInscription.setText(champMotDePasseInscription.getText());
            champAffichageMotDePasseInscription.setVisible(true);
            champMotDePasseInscription.setVisible(false);
        } else {
            champMotDePasseInscription.setText(champAffichageMotDePasseInscription.getText());
            champMotDePasseInscription.setVisible(true);
            champAffichageMotDePasseInscription.setVisible(false);
        }
    }

    @FXML
    public void basculerFormulaire() {
        boolean estConnexionVisible = formulaire_connexion.isVisible();
        formulaire_connexion.setVisible(!estConnexionVisible);
        formulaire_inscription.setVisible(estConnexionVisible);


    }

    @FXML
    private void retourAccueil(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/main_view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la page d'accueil.", Alert.AlertType.ERROR);
        }
    }


}