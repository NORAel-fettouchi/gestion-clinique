package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import models.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import models.Patient;

import java.io.IOException;


public class PatientLoginController {

    @FXML
    private TextField patientFullName;

    @FXML
    private void loginPatient() {
        String fullname = patientFullName.getText().trim();

        if (fullname.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez entrer votre nom complet.");
            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            alertStage.getIcons().add(new Image(getClass().getResourceAsStream("/utils/icons/warning.png")));

            alert.showAndWait();
            return;
        }

        try {
            Connection conn = Database.connect();
            String sql = "SELECT * FROM patients WHERE CONCAT(nom, ' ', prenom) = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, fullname);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Authentification réussie
                /*Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("Connexion réussie.");
                Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                alertStage.getIcons().add(new Image(getClass().getResourceAsStream("/utils/icons/warning.png")));

                alert.showAndWait();*/
                // TODO : rediriger vers l'espace patient (interface à créer)
                Patient patient = new Patient();
                patient.setId(rs.getInt("id"));
                patient.setNom(rs.getString("nom"));
                patient.setPrenom(rs.getString("prenom"));

                // Chargement de l'interface patientDashboard.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/PatientDashboard.fxml"));
                Parent root = loader.load();

                // Transmettre le patient au contrôleur
                PatientDashboardController controller = loader.getController();
                controller.setPatient(patient); // 🔥 🔥 C’est ça qui est essentiel

                // Afficher la nouvelle scène
                Stage stage = (Stage) patientFullName.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Espace Patient");
                stage.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Échec");
                alert.setHeaderText(null);
                alert.setContentText("Nom complet invalide.");
                Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                alertStage.getIcons().add(new Image(getClass().getResourceAsStream("/utils/icons/warning.png")));

                alert.showAndWait();
            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur est survenue.");
            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            alertStage.getIcons().add(new Image(getClass().getResourceAsStream("/utils/icons/warning.png")));

            alert.showAndWait();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(getClass().getResourceAsStream("/utils/icons/warning.png")));

        alert.showAndWait();
    }
    @FXML
    private void handleRetourAccueil(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/main_view.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        }
    }


}
