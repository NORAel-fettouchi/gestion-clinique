package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import models.Admin;
import models.Patient;
import models.RendezVous;
import models.dao.AdminDAO;
import models.dao.RendezVousDAO;
import java.util.List;
import java.time.LocalDate;
import javafx.application.Platform;

public class DemanderRendezVousController {

    @FXML private ComboBox<String> comboSpecialites;
    @FXML private ComboBox<Admin> comboDoctors;
    @FXML private DatePicker datePickerRdv;

    private Patient patient;
    private final AdminDAO adminDAO = new AdminDAO();
    private final RendezVousDAO rendezVousDAO = new RendezVousDAO();

    @FXML
    public void initialize() {
        // Ajoute l'icône à la fenêtre principale (Stage)
        Platform.runLater(() -> {
            Stage stage = (Stage) comboSpecialites.getScene().getWindow();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/utils/icons/demande.png")));
        });
        // Initialiser les spécialités
        ObservableList<String> specialites = FXCollections.observableArrayList(
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
                "Gynécologie"
        );
        comboSpecialites.setItems(specialites);

        // Configurer l'affichage des médecins
        comboDoctors.setCellFactory(param -> new ListCell<Admin>() {
            @Override
            protected void updateItem(Admin item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null :
                        item.getNom() + " " + item.getPrenom() + " (" + item.getSpecialite() + ")");
            }
        });

        // Charger les médecins quand une spécialité est sélectionnée
        comboSpecialites.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                chargerMedecinsParSpecialite(newVal);
            }
        });

        // Configurer le DatePicker pour n'accepter que les dates futures
        datePickerRdv.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(date.isBefore(LocalDate.now().plusDays(1)));
            }
        });
        datePickerRdv.setValue(LocalDate.now().plusDays(1));
    }

    private void chargerMedecinsParSpecialite(String specialite) {
        comboDoctors.getItems().clear();
        List<Admin> medecins = adminDAO.getAdminsBySpecialite(specialite);
        comboDoctors.getItems().addAll(medecins);

        if (medecins.isEmpty()) {
            comboDoctors.setPromptText("Aucun médecin disponible");
        } else {
            comboDoctors.getSelectionModel().selectFirst();
        }
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    @FXML
    private void handleEnvoyer() {
        if (patient == null) {
            showAlert("Erreur", "Patient non défini", Alert.AlertType.ERROR);
            return;
        }

        LocalDate date = datePickerRdv.getValue();
        Admin medecin = comboDoctors.getValue();

        if (date == null || medecin == null) {
            showAlert("Erreur", "Veuillez sélectionner une date et un médecin", Alert.AlertType.ERROR);
            return;
        }

        // Création du rendez-vous avec statut "Demande"
        RendezVous rdv = new RendezVous(
                date,
                "Demande", // Statut initial
                patient.getId(),
                medecin.getId()
        );

        if (rendezVousDAO.ajouterRendezVous(rdv)) {
            showAlert("Succès", "Demande de rendez-vous envoyée au Dr. " +
                            medecin.getNom() + ". En attente de confirmation.",
                    Alert.AlertType.INFORMATION);
            closeWindow();
        } else {
            showAlert("Erreur", "Échec de l'envoi de la demande", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleAnnuler() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) comboSpecialites.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(getClass().getResourceAsStream("/utils/icons/warning.png")));
        alert.showAndWait();
    }
}













