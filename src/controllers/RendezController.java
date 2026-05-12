package controllers;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import models.Patient;
import models.RendezVous;
import models.dao.PatientManagDAO;
import models.dao.RendezVousDAO;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Cursor;

public class RendezController implements Initializable {

    @FXML private TableView<RendezVous> tableRdv;
    @FXML private TableColumn<RendezVous, String> colDate;
    @FXML private TableColumn<RendezVous, Integer> Id;
    @FXML private TableColumn<RendezVous, String> colPatient;
    @FXML private TableColumn<RendezVous, String> colStatut;
    @FXML private TableColumn<RendezVous, Void> colActions;
    @FXML private DatePicker datePickerRdv;
    @FXML private ComboBox<String> comboStatut;
    @FXML private TextField txtPatientId;
    @FXML private Button btnAjouter;

    private RendezVousDAO rendezVousDAO = new RendezVousDAO();
    private ObservableList<RendezVous> rdvList = FXCollections.observableArrayList();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private RendezVous rdv;

    private int adminId; // ✅ Nouveau champ pour l'ID de l'admin connecté

    public void setAdminId(int adminId) {
        this.adminId = adminId;
        chargerRendezVous(); // Charger les rendez-vous pour cet admin
    }

    public void setRendezVous(RendezVous rdv) {
        this.rdv = rdv;
        //afficherDetails();
        chargerRendezVous();

        Platform.runLater(() -> {
            for (RendezVous item : rdvList) {
                if (item.getId() == rdv.getId()) {
                    tableRdv.getSelectionModel().select(item);
                    tableRdv.scrollTo(item);

                    // Mettre le RowFactory qui colore la ligne sélectionnée
                    tableRdv.setRowFactory(tv -> new TableRow<>() {
                        @Override
                        protected void updateItem(RendezVous rowItem, boolean empty) {
                            super.updateItem(rowItem, empty);
                            if (empty || rowItem == null) {
                                setStyle("");
                            } else if (rowItem.equals(tableRdv.getSelectionModel().getSelectedItem())) {
                                setStyle("-fx-background-color: lightgreen;");
                            } else {
                                setStyle("");
                            }
                        }
                    });

                    break;
                }
            }
        });

    }

    private void afficherDetails() {
        if (rdv != null) {
            colDate.setText("Date : " + rdv.getDateRdv().toString());
            colStatut.setText("Statut : " + rdv.getStatut());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Id.setVisible(false);
        Id.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colDate.setCellValueFactory(data -> javafx.beans.binding.Bindings.createStringBinding(() ->
                data.getValue().getDateRdv().format(formatter)));

        colStatut.setCellValueFactory(data -> data.getValue().statutProperty());

        colPatient.setCellValueFactory(data -> {
            int patientId = data.getValue().getPatientId();
            Patient patient = new PatientManagDAO().getPatientById(patientId);
            String nomComplet = "Inconnu";
            if (patient != null) {
                nomComplet = patient.getNom() + " " + patient.getPrenom();
            }
            return new javafx.beans.property.SimpleStringProperty(nomComplet);
        });


        ajouterColonnesActions();

        comboStatut.setItems(FXCollections.observableArrayList("Planifié", "Confirmé", "Annulé"));

        datePickerRdv.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? formatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return (string != null && !string.isEmpty()) ? LocalDate.parse(string, formatter) : null;
            }
        });

        // Ajoute cette partie pour gérer la coloration de la ligne sélectionnée
        tableRdv.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(RendezVous item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else if (item.equals(tableRdv.getSelectionModel().getSelectedItem())) {
                    setStyle("-fx-background-color: lightgreen;");
                } else {
                    setStyle("");
                }
            }
        });
    }

    private void chargerRendezVous() {

        rdvList.setAll(rendezVousDAO.getAllRendezVousParAdmin(adminId));
        tableRdv.setItems(rdvList);
    }

    @FXML
    private void ajouterRendezVous() {
        LocalDate date = datePickerRdv.getValue();
        String statut = comboStatut.getValue();
        String patientIdText = txtPatientId.getText();

        if (date == null || statut == null || statut.isEmpty() || patientIdText == null || patientIdText.isEmpty()) {
            showAlert("Erreur", "Date, statut et ID patient sont requis !");
            return;
        }

        int patientId;
        try {
            patientId = Integer.parseInt(patientIdText);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "L'ID patient doit être un nombre entier !");
            return;
        }

        PatientManagDAO patientDAO = new PatientManagDAO();
        if (!patientDAO.existePatient(patientId)) {
            showAlert("Erreur", "Le patient avec l'ID " + patientId + " n'existe pas !");
            return;
        }

        RendezVous rdv = new RendezVous(0, date, statut, patientId, adminId); // ✅ adminId ajouté
        if (rendezVousDAO.ajouterRendezVous(rdv)) {
            showAlert("Succès", "Rendez-vous ajouté.");
            chargerRendezVous();
            viderChamps();
        } else {
            showAlert("Erreur", "Erreur lors de l'ajout.");
        }
    }

    private void ajouterColonnesActions() {
        colActions.setCellFactory(param -> new TableCell<>() {
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
                        RendezVous rdv = getTableView().getItems().get(index);
                        modifierRendezVous(rdv);
                    }
                });

                btnSupprimer.setOnAction(event -> {
                    int index = getIndex();
                    if (index >= 0 && index < getTableView().getItems().size()) {
                        RendezVous rdv = getTableView().getItems().get(index);
                        supprimerRendezVous(rdv);
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

    private void modifierRendezVous(RendezVous rdv) {
        DatePicker dp = new DatePicker(rdv.getDateRdv());
        ComboBox<String> cb = new ComboBox<>(FXCollections.observableArrayList("Planifié", "Confirmé", "Annulé"));
        cb.setValue(rdv.getStatut());

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Modifier Rendez-vous");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        HBox content = new HBox(10, dp, cb);
        dialog.getDialogPane().setContent(content);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                LocalDate nouvelleDate = dp.getValue();
                String nouveauStatut = cb.getValue();

                if (nouvelleDate == null || nouveauStatut == null || nouveauStatut.isEmpty()) {
                    showAlert("Erreur", "Date et statut sont requis pour la modification !");
                    return;
                }

                rdv.setDateRdv(nouvelleDate);
                rdv.setStatut(nouveauStatut);

                if (rendezVousDAO.modifierRendezVous(rdv)) {
                    showAlert("Succès", "Rendez-vous modifié.");
                    chargerRendezVous();
                } else {
                    showAlert("Erreur", "Erreur lors de la modification.");
                }
            }
        });
    }

    private void supprimerRendezVous(RendezVous rdv) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmer la suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Voulez-vous vraiment supprimer ce rendez-vous ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Correction ici : on passe id et adminId
                if (rendezVousDAO.supprimerRendezVous(rdv.getId(), rdv.getAdminId())) {
                    rdvList.remove(rdv);
                    showAlert("Succès", "Rendez-vous supprimé.");
                } else {
                    showAlert("Erreur", "Erreur lors de la suppression.");
                }
            }
        });
    }


    private void viderChamps() {
        datePickerRdv.setValue(null);
        comboStatut.setValue(null);
        txtPatientId.clear();
    }

    private void showAlert(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(getClass().getResourceAsStream("/utils/icons/warning.png")));
        alert.showAndWait();
    }
}
