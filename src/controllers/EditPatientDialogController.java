package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import models.Patient;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class EditPatientDialogController {

    @FXML
    private TextField txtNom;
    @FXML
    private TextField txtPrenom;
    @FXML
    private TextField txtDateNaiss;
    @FXML
    private ComboBox<String> comboSexe;

    private Stage dialogStage;
    private Patient patient;
    private boolean okClicked = false;

    @FXML
    private void initialize() {
        comboSexe.getItems().addAll("Homme", "Femme");
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;

        txtNom.setText(patient.getNom());
        txtPrenom.setText(patient.getPrenom());
        txtDateNaiss.setText(patient.getDateNaiss());
        comboSexe.setValue(patient.getSexe());
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        if (validerChamps()) {
            patient.setNom(txtNom.getText());
            patient.setPrenom(txtPrenom.getText());
            patient.setDateNaiss(txtDateNaiss.getText());
            patient.setSexe(comboSexe.getValue());

            okClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean validerChamps() {
        String errorMessage = "";

        if (txtNom.getText() == null || txtNom.getText().trim().isEmpty()) {
            errorMessage += "Nom invalide!\n";
        }
        if (txtPrenom.getText() == null || txtPrenom.getText().trim().isEmpty()) {
            errorMessage += "Prénom invalide!\n";
        }
        if (txtDateNaiss.getText() == null || txtDateNaiss.getText().trim().isEmpty()) {
            errorMessage += "Date de naissance invalide!\n";
        } else {
            try {
                LocalDate.parse(txtDateNaiss.getText());
            } catch (DateTimeParseException e) {
                errorMessage += "Format date invalide (YYYY-MM-DD)!\n";
            }
        }
        if (comboSexe.getValue() == null) {
            errorMessage += "Sexe non sélectionné!\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Champs invalides");
            alert.setHeaderText("Veuillez corriger les erreurs");
            alert.setContentText(errorMessage);
            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            alertStage.getIcons().add(new Image(getClass().getResourceAsStream("/utils/icons/warning.png")));

            alert.showAndWait();

            return false;
        }
    }
}
