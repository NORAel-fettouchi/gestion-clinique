package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.Cursor;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;
import models.Patient;
import models.dao.PatientManagDAO;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class PatientManagController {

    @FXML private TableView<Patient> tablePatients;
    @FXML private TableColumn<Patient, Integer> colId;
    @FXML private TableColumn<Patient, String> colNom;
    @FXML private TableColumn<Patient, String> colPrenom;
    @FXML private TableColumn<Patient, String> colDateNaiss;
    @FXML private TableColumn<Patient, String> colSexe;
    @FXML private TableColumn<Patient, Void> colActions;

    @FXML private TextField txtNom;
    @FXML private TextField txtPrenom;
    @FXML private TextField txtDateNaiss;
    @FXML private ComboBox<String> combosexe;

    @FXML private Button btnAjouter;

    private final PatientManagDAO dao = new PatientManagDAO();
    private final ObservableList<Patient> patientsList = FXCollections.observableArrayList();

    private int adminId;

    // IMPORTANT : setter pour passer l'adminId
    public void setAdminId(int adminId) {
        this.adminId = adminId;
        chargerPatients();
    }

    @FXML
    public void initialize() {
        combosexe.getItems().addAll("Homme", "Femme");

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colDateNaiss.setCellValueFactory(new PropertyValueFactory<>("dateNaiss"));
        colSexe.setCellValueFactory(new PropertyValueFactory<>("sexe"));

        ajouterColonnesActions();
    }

    private void chargerPatients() {
        patientsList.clear();
        if (adminId != 0) {
            patientsList.addAll(dao.getAllPatientsByAdmin(adminId));
        }
        tablePatients.setItems(patientsList);
    }

    private void ajouterColonnesActions() {
        colActions.setCellFactory(param -> new TableCell<Patient, Void>() {
            private final Button btnModifier = new Button();
            private final Button btnSupprimer = new Button();
            private final HBox pane = new HBox(10, btnModifier, btnSupprimer);

            {
                ImageView iconModifier = new ImageView(new Image(getClass().getResourceAsStream("/utils/RdvIcons/pencil.png")));
                iconModifier.setFitWidth(18);
                iconModifier.setFitHeight(18);
                btnModifier.setGraphic(iconModifier);
                btnModifier.setStyle("-fx-background-color: transparent;");
                btnModifier.setCursor(Cursor.HAND);
                btnModifier.setTooltip(new Tooltip("Modifier"));

                ImageView iconSupprimer = new ImageView(new Image(getClass().getResourceAsStream("/utils/RdvIcons/delete.png")));
                iconSupprimer.setFitWidth(18);
                iconSupprimer.setFitHeight(18);
                btnSupprimer.setGraphic(iconSupprimer);
                btnSupprimer.setStyle("-fx-background-color: transparent;");
                btnSupprimer.setCursor(Cursor.HAND);
                btnSupprimer.setTooltip(new Tooltip("Supprimer"));

                btnModifier.setOnMouseEntered(e -> btnModifier.setStyle("-fx-background-color: #cce5ff;"));
                btnModifier.setOnMouseExited(e -> btnModifier.setStyle("-fx-background-color: transparent;"));

                btnSupprimer.setOnMouseEntered(e -> btnSupprimer.setStyle("-fx-background-color: #f8d7da;"));
                btnSupprimer.setOnMouseExited(e -> btnSupprimer.setStyle("-fx-background-color: transparent;"));

                btnModifier.setOnAction(event -> {
                    int index = getIndex();
                    if (index >= 0 && index < getTableView().getItems().size()) {
                        Patient patient = getTableView().getItems().get(index);
                        boolean okClicked = showEditDialog(patient);
                        if (okClicked) {
                            if (dao.modifierPatient(patient, adminId)) {
                                chargerPatients();
                                showAlert("Succès", "Patient modifié avec succès !");
                            } else {
                                showAlert("Erreur", "Erreur lors de la modification.");
                            }
                        }
                    }
                });

                btnSupprimer.setOnAction(event -> {
                    int index = getIndex();
                    if (index >= 0 && index < getTableView().getItems().size()) {
                        Patient patient = getTableView().getItems().get(index);

                        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                        confirmation.setTitle("Confirmation de suppression");
                        confirmation.setHeaderText("Êtes-vous sûr de vouloir supprimer ce patient ?");
                        confirmation.setContentText("Nom : " + patient.getNom() + " " + patient.getPrenom());
                        Stage alertStage = (Stage) confirmation.getDialogPane().getScene().getWindow();
                        alertStage.getIcons().add(new Image(getClass().getResourceAsStream("/utils/icons/warning.png")));

                        confirmation.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                if (dao.supprimerPatient(patient.getId(), adminId)) {
                                    chargerPatients();
                                    showAlert("Succès", "Patient supprimé avec succès !");
                                } else {
                                    showAlert("Erreur", "Erreur lors de la suppression.");
                                }
                            }
                        });
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    @FXML
    private void ajouterPatient() {
        if (!validerChamps()) return;

        Patient p = new Patient(0, txtNom.getText(), txtPrenom.getText(),
                txtDateNaiss.getText(), combosexe.getValue());

        if (dao.ajouterPatient(p, adminId)) {
            chargerPatients();
            viderChamps();
            showAlert("Succès", "Patient ajouté avec succès !");
        } else {
            showAlert("Erreur", "Erreur lors de l'ajout du patient.");
        }
    }

    private boolean validerChamps() {
        if (txtNom.getText().isEmpty() || txtPrenom.getText().isEmpty()
                || txtDateNaiss.getText().isEmpty() || combosexe.getValue() == null) {
            showAlert("Erreur", "Tous les champs doivent être remplis.");
            return false;
        }

        try {
            LocalDate.parse(txtDateNaiss.getText());
        } catch (DateTimeParseException e) {
            showAlert("Erreur", "Date invalide. Format attendu : YYYY-MM-DD.");
            return false;
        }

        return true;
    }

    private void viderChamps() {
        txtNom.clear();
        txtPrenom.clear();
        txtDateNaiss.clear();
        combosexe.getSelectionModel().clearSelection();
    }

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(getClass().getResourceAsStream("/utils/icons/warning.png")));
        alert.showAndWait();
    }

    private boolean showEditDialog(Patient patient) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/EditPatientDialog.fxml"));
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Modifier Patient");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(tablePatients.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            controllers.EditPatientDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setPatient(patient);

            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre de modification.");
            return false;
        }
    }
}
