package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.stage.Stage;
import models.Patient;
import models.dao.PatientDAO;
import models.dao.RendezVousDAO;
import models.RendezVous;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class RendezVousPatientController implements Initializable {

    private Patient patient;

    @FXML
    private Label lblNomPatient;

    @FXML
    private TableView<RendezVous> tableRendezVous;

    @FXML
    private TableColumn<RendezVous, LocalDate> colDateRdv;

    @FXML
    private TableColumn<RendezVous, String> colStatutRdv;

    private final RendezVousDAO rendezVousDAO = new RendezVousDAO();
    private final ObservableList<RendezVous> rendezVousList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colDateRdv.setCellValueFactory(new PropertyValueFactory<>("dateRdv"));
        colStatutRdv.setCellValueFactory(new PropertyValueFactory<>("statut"));



        tableRendezVous.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableRendezVous.setItems(rendezVousList);
    }

    public void setPatient(Patient patient) {
        if (patient == null) {
            showError("Patient invalide.");
            return;
        }

        this.patient = patient;
        afficherInfosPatient();
        chargerRendezVousDuPatient();
    }

    public void setPatientId(int id) {
        try {
            PatientDAO patientDAO = new PatientDAO();
            Patient patientTrouve = patientDAO.getPatientById(id);

            if (patientTrouve != null) {
                setPatient(patientTrouve);
            } else {
                showError("Patient non trouvé avec l'ID: " + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors de la récupération du patient.");
        }
    }

    private void afficherInfosPatient() {
        if (patient != null && lblNomPatient != null) {
            String nomComplet = capitalize(patient.getNom()) + " " + capitalize(patient.getPrenom());
            lblNomPatient.setText(nomComplet);
        }
    }

    private void chargerRendezVousDuPatient() {
        if (patient == null) return;

        try {
            List<RendezVous> liste = rendezVousDAO.getRendezVousParPatient(patient.getId());
            rendezVousList.setAll(liste);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors du chargement des rendez-vous.");
            rendezVousList.clear();
        }
    }





    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public Patient getPatient() {
        return patient;
    }


}
