package controllers;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import models.Patient;
import models.Traitement;
import models.dao.PatientManagDAO;
import models.dao.TraitementDAO;

public class TraitementController {

    @FXML private TableView<Traitement> tableTraitement;
    @FXML private TableColumn<Traitement, Integer> colID;
    @FXML private TableColumn<Traitement, String> colNom;
    @FXML private TableColumn<Traitement, String> colType;
    @FXML private TableColumn<Traitement, String> colDescription;
    @FXML private TableColumn<Traitement, String> colDebut;
    @FXML private TableColumn<Traitement, String> colFin;
    @FXML private TableColumn<Traitement, String> coluPatient;
    @FXML private TableColumn<Traitement, Void> colActions;

    @FXML private TextField txtNom;
    @FXML private TextField txtType;
    @FXML private TextField txtDescription;
    @FXML private DatePicker dateDebut;
    @FXML private DatePicker dateFin;
    @FXML private TextField txtPatientId;
    @FXML private Button btnAjouterTraitement;

    private ObservableList<Traitement> traitementList = FXCollections.observableArrayList();
    private final TraitementDAO traitementDAO = new TraitementDAO();
    private Traitement traitementSelectionne = null;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private int currentAdminId;

    public void setAdminId(int adminId) {
        this.currentAdminId = adminId;
        chargerTraitements();
    }

    @FXML
    public void initialize() {
        colID.setVisible(false);

        tableTraitement.setItems(traitementList);

        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        colDebut.setCellValueFactory(data ->
                Bindings.createStringBinding(() -> {
                    LocalDate d = data.getValue().getDateDebut();
                    return d != null ? formatter.format(d) : "";
                })
        );

        colFin.setCellValueFactory(data ->
                Bindings.createStringBinding(() -> {
                    LocalDate d = data.getValue().getDateFin();
                    return d != null ? formatter.format(d) : "";
                })
        );

        coluPatient.setCellValueFactory(data -> {
            int patientId = data.getValue().getPatientId();
            Patient patient = new PatientManagDAO().getPatientById(patientId);
            String nomComplet = patient != null ? patient.getPrenom() + " " + patient.getNom() : "Inconnu";
            return new javafx.beans.property.SimpleStringProperty(nomComplet);
        });

        dateDebut.setConverter(new StringConverter<LocalDate>() {
            public String toString(LocalDate date) {
                return date != null ? formatter.format(date) : "";
            }
            public LocalDate fromString(String string) {
                return (string != null && !string.isEmpty()) ? LocalDate.parse(string, formatter) : null;
            }
        });

        dateFin.setConverter(dateDebut.getConverter());

        chargerTraitements();
        ajouterColonneActions();
    }

    private void chargerTraitements() {
        traitementList.setAll(traitementDAO.getTraitementsByAdminId(currentAdminId));
    }

    private void ajouterColonneActions() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnModifier = new Button();
            private final Button btnSupprimer = new Button();
            private final HBox pane = new HBox(10, btnModifier, btnSupprimer);

            {
                // Icone Modifier
                InputStream modStream = getClass().getResourceAsStream("/utils/RdvIcons/pencil.png");
                if (modStream != null) {
                    ImageView iconModifier = new ImageView(new Image(modStream));
                    iconModifier.setFitWidth(16);
                    iconModifier.setFitHeight(16);
                    btnModifier.setGraphic(iconModifier);
                }
                btnModifier.setStyle("-fx-background-color: transparent;");
                btnModifier.setCursor(Cursor.HAND);
                btnModifier.setTooltip(new Tooltip("Modifier"));
                btnModifier.setOnAction(e -> {
                    traitementSelectionne = getTableView().getItems().get(getIndex());
                    remplirChampsAvecTraitement(traitementSelectionne);
                    btnAjouterTraitement.setText("Modifier");
                });

                // Icone Supprimer
                InputStream supStream = getClass().getResourceAsStream("/utils/RdvIcons/delete.png");
                if (supStream != null) {
                    ImageView iconSupprimer = new ImageView(new Image(supStream));
                    iconSupprimer.setFitWidth(16);
                    iconSupprimer.setFitHeight(16);
                    btnSupprimer.setGraphic(iconSupprimer);
                }
                btnSupprimer.setStyle("-fx-background-color: transparent;");
                btnSupprimer.setCursor(Cursor.HAND);
                btnSupprimer.setTooltip(new Tooltip("Supprimer"));
                btnSupprimer.setOnAction(e -> {
                    Traitement t = getTableView().getItems().get(getIndex());
                    if (t.getAdminId() != currentAdminId) {
                        showAlert(AlertType.ERROR, "Erreur", "Vous ne pouvez pas supprimer ce traitement.");
                        return;
                    }

                    Alert confirm = new Alert(AlertType.CONFIRMATION);
                    confirm.setTitle("Confirmation");
                    confirm.setContentText("Voulez-vous vraiment supprimer ce traitement ?");
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            if (traitementDAO.supprimerTraitement(t.getId(), currentAdminId)) {
                                showAlert(AlertType.INFORMATION, "Succès", "Traitement supprimé.");
                                chargerTraitements();
                            } else {
                                showAlert(AlertType.ERROR, "Erreur", "Échec de la suppression.");
                            }
                        }
                    });
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
    private void ajouterTraitement() {
        String nom = txtNom.getText().trim();
        String type = txtType.getText().trim();
        String description = txtDescription.getText().trim();
        LocalDate debut = dateDebut.getValue();
        LocalDate fin = dateFin.getValue();
        String idPatientStr = txtPatientId.getText().trim();

        if (nom.isEmpty() || type.isEmpty() || description.isEmpty() || debut == null || fin == null || idPatientStr.isEmpty()) {
            showAlert(AlertType.ERROR, "Erreur", "Tous les champs sont obligatoires.");
            return;
        }

        if (!debut.isBefore(fin)) {
            showAlert(AlertType.ERROR, "Erreur", "La date de début doit être avant la date de fin.");
            return;
        }

        int idPatient;
        try {
            idPatient = Integer.parseInt(idPatientStr);
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Erreur", "ID Patient invalide.");
            return;
        }

        if (traitementSelectionne == null) {
            Traitement nouveau = new Traitement(0, nom, type, description, debut, fin, idPatient, currentAdminId);
            if (traitementDAO.ajouterTraitement(nouveau)) {
                showAlert(AlertType.INFORMATION, "Succès", "Traitement ajouté.");
            } else {
                showAlert(AlertType.ERROR, "Erreur", "Erreur lors de l'ajout.");
            }
        } else {
            if (traitementSelectionne.getAdminId() != currentAdminId) {
                showAlert(AlertType.ERROR, "Erreur", "Vous ne pouvez pas modifier ce traitement.");
                clearFields();
                traitementSelectionne = null;
                btnAjouterTraitement.setText("Ajouter");
                return;
            }

            traitementSelectionne.setNom(nom);
            traitementSelectionne.setType(type);
            traitementSelectionne.setDescription(description);
            traitementSelectionne.setDateDebut(debut);
            traitementSelectionne.setDateFin(fin);
            traitementSelectionne.setIdPatient(idPatient);

            if (traitementDAO.modifierTraitement(traitementSelectionne)) {
                showAlert(AlertType.INFORMATION, "Succès", "Traitement modifié.");
            } else {
                showAlert(AlertType.ERROR, "Erreur", "Erreur lors de la modification.");
            }
            traitementSelectionne = null;
            btnAjouterTraitement.setText("Ajouter");
        }

        chargerTraitements();
        clearFields();
    }

    private void remplirChampsAvecTraitement(Traitement t) {
        txtNom.setText(t.getNom());
        txtType.setText(t.getType());
        txtDescription.setText(t.getDescription());
        dateDebut.setValue(t.getDateDebut());
        dateFin.setValue(t.getDateFin());
        txtPatientId.setText(String.valueOf(t.getPatientId()));
    }

    private void clearFields() {
        txtNom.clear();
        txtType.clear();
        txtDescription.clear();
        txtPatientId.clear();
        dateDebut.setValue(null);
        dateFin.setValue(null);
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
