package controllers;
import java.util.Map; // Import ajouté
import java.util.HashMap; // Import ajouté
import javafx.beans.property.SimpleStringProperty; // Import ajouté
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox; // Import ajouté
import javafx.stage.Stage;
import models.RendezVous;
import models.dao.RendezVousDAO;
import java.time.LocalDate;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import models.Patient;
import models.dao.PatientDAO;
public class DemandeAdminController {

    @FXML private TableView<RendezVous> tableDemandes;
    @FXML private TableColumn<RendezVous, Integer> colId;
    @FXML private TableColumn<RendezVous, String> colPatient;
    @FXML private TableColumn<RendezVous, LocalDate> colDate;
    @FXML private TableColumn<RendezVous, String> colStatut;
    @FXML private TableColumn<RendezVous, Void> colActions;

    private int adminId;
    private final RendezVousDAO rendezVousDAO = new RendezVousDAO();
    private final PatientDAO patientDAO = new PatientDAO(); // Nouvelle instance
    private Map<Integer, String> patientsMap = new HashMap<>(); // Cache pour les noms des patients

    public void setAdminId(int adminId) {
        this.adminId = adminId;
        chargerDemandes();
    }

    @FXML
    public void initialize() {
        configurerTableau();
    }

    private void configurerTableau() {
        colPatient.setCellValueFactory(cellData -> {
            int patientId = cellData.getValue().getPatientId();
            String nomComplet = patientsMap.get(patientId);

            if (nomComplet == null) {
                // Si le patient n'est pas dans le cache, on le récupère
                var patient = patientDAO.getPatientById(patientId);
                nomComplet = (patient != null)
                        ? patient.getNom() + " " + patient.getPrenom()
                        : "Patient #" + patientId;

                patientsMap.put(patientId, nomComplet);
            }

            return new SimpleStringProperty(nomComplet);
        });
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateRdv"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnAccepter = new Button("Accepter");
            private final Button btnRefuser = new Button("Refuser");
            private final HBox hbox = new HBox(btnAccepter, btnRefuser);

            {
                hbox.setSpacing(5);
                btnAccepter.getStyleClass().add("btn-success");
                btnRefuser.getStyleClass().add("btn-danger");

                btnAccepter.setOnAction(event -> {
                    RendezVous rdv = getTableView().getItems().get(getIndex());
                    traiterDemande(rdv, "Planifié");
                });

                btnRefuser.setOnAction(event -> {
                    RendezVous rdv = getTableView().getItems().get(getIndex());
                    traiterDemande(rdv, "Annulé");
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
    }

    private void chargerDemandes() {
        ObservableList<RendezVous> demandes = FXCollections.observableArrayList(
                rendezVousDAO.getRendezVousByStatutAndAdmin("Demande", adminId)
        );
        tableDemandes.setItems(demandes);
    }

    private void traiterDemande(RendezVous rdv, String nouveauStatut) {
        rdv.setStatut(nouveauStatut);
        if (rendezVousDAO.modifierRendezVous(rdv)) {
            chargerDemandes();
            showAlert("Succès", "Demande traitée avec succès", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Erreur", "Échec du traitement de la demande", Alert.AlertType.ERROR);
        }
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