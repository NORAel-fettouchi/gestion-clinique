package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Patient;
import models.Traitement;
import models.dao.PatientDAO;
import models.dao.TraitementDAO;

import java.io.IOException;
import java.util.List;

public class TraitementsPatientController {

    @FXML
    private TableView<Traitement> tableTraitements;

    @FXML
    private TableColumn<Traitement, String> colDateTraitement;

    @FXML
    private TableColumn<Traitement, String> colNomTraitement;

    @FXML
    private TableColumn<Traitement, String> colFinTraitement;

    private final TraitementDAO traitementDAO = new TraitementDAO();

    private Patient patient;

    /**
     * Initialise la table et ses colonnes.
     */
    @FXML
    public void initialize() {
        // Affiche la date de début sous forme de chaîne
        colDateTraitement.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDateDebut() != null) {
                return new SimpleStringProperty(cellData.getValue().getDateDebut().toString());
            } else {
                return new SimpleStringProperty("");
            }
        });

        // Affiche la description du traitement
        colNomTraitement.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Affiche le statut du traitement
        colFinTraitement.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDateDebut() != null) {
                return new SimpleStringProperty(cellData.getValue().getDateFin().toString());
            } else {
                return new SimpleStringProperty("");
            }
        });
    }

    /**
     * Charge la liste des traitements pour un patient donné.
     *
     * @param patientId l’ID du patient
     */
    public void chargerTraitementsDuPatient(int patientId) {
        List<Traitement> traitements = traitementDAO.getTraitementsByPatientId(patientId);
        tableTraitements.getItems().setAll(traitements);
    }

    /**
     * Définit le patient et charge ses traitements.
     *
     * @param patient l’objet Patient
     */
    public void setPatient(Patient patient) {
        if (patient != null) {
            this.patient = patient;
            chargerTraitementsDuPatient(patient.getId());
        }
    }

    /**
     * Définit le patient via son ID, récupère l’objet Patient et charge les traitements.
     *
     * @param id ID du patient
     */
    public void setPatientId(int id) {
        PatientDAO patientDAO = new PatientDAO();
        Patient patient = patientDAO.getPatientById(id);
        if (patient != null) {
            this.patient = patient;
            chargerTraitementsDuPatient(id);
        } else {
            System.err.println("Patient avec ID " + id + " non trouvé.");
        }
    }



}
