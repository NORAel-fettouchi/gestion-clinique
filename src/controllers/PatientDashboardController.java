package controllers;

import com.sun.javafx.charts.Legend;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Patient;
import models.RendezVous;
import models.Traitement;
import models.dao.PatientDAO;
import models.dao.RendezVousDAO;
import models.dao.TraitementDAO;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class PatientDashboardController implements Initializable {

    @FXML
    private TableColumn<RendezVous, Void> colView;

    @FXML
    private Label lblTotalTraitement;
    @FXML
    private Label lblTotalRdv;
    @FXML
    private TableColumn<RendezVous, LocalDate> colDateRdv;
    @FXML
    private TableColumn<RendezVous, String> colStatutRdv;
    @FXML
    private TableView<RendezVous> tableRendezVous;
    @FXML
    private VBox mainContent;
    @FXML
    private TableView<Traitement> tableTraitements;
    @FXML
    private TableColumn<Traitement, LocalDate> colDatedebutTraitement;
    @FXML
    private TableColumn<Traitement, LocalDate> colDatefinTraitement;
    @FXML
    private TableColumn<Traitement, String> colNomTraitement;
    @FXML
    private Label labelNomComplet;
    @FXML
    private Label labelId;
    @FXML
    private Text textInitiale;
    @FXML private Button btnDemanderRdv;

    private final RendezVousDAO rendezVousDAO = new RendezVousDAO();
    private final TraitementDAO traitementDAO = new TraitementDAO();
    private Patient patient;
    @FXML
    private Text textTopbar;
    @FXML
    private VBox sidebar;

    public void setPatient(Patient patient) {
        if (patient == null) {
            showError("Patient invalide.");
            return;
        }
        this.patient = patient;
        mettreAJourInfosPatient();
        chargerRendezVous();
        chargerTraitements();
        mettreAJourCompteurs();
        afficherTableRdvANDtraitementDansMainContent();
    }

    private void mettreAJourInfosPatient() {
        if (patient != null) {
            labelNomComplet.setText(capitalize(patient.getNom()) + " " + capitalize(patient.getPrenom()));
            labelId.setText("ID: " + patient.getId());

            if (patient.getNom() != null && !patient.getNom().isEmpty()) {
                String text = patient.getNom().substring(0, 1).toUpperCase();
                textInitiale.setText(text);
                textTopbar.setText(text);
            }
        }
    }
    private void mettreAJourCompteurs() {
        if (patient == null) return;
        int totalRdv=rendezVousDAO.getAllRendezVousParPatient(patient.getId());
        int totalTraitements=traitementDAO.getTraitementsTotal(patient.getId());
        lblTotalTraitement.setText(String.valueOf(totalTraitements));
        lblTotalRdv.setText(String.valueOf(totalRdv));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Colonnes rendez-vous généraux
        colDateRdv.setCellValueFactory(new PropertyValueFactory<>("dateRdv"));
        colStatutRdv.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Colonnes traitements généraux
        colDatedebutTraitement.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        colDatefinTraitement.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        colNomTraitement.setCellValueFactory(new PropertyValueFactory<>("nom"));

        this.chargerRendezVous();
        this.chargerTraitements();
        //ajouterBoutonsActions();

    }

    @FXML
    private void handleRDVButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/RendezVousPatient.fxml"));
            Parent pane = loader.load();

            RendezVousPatientController controller = loader.getController();
            if (patient != null) {
                controller.setPatient(patient);
            }

            mainContent.getChildren().setAll(pane);
            AnchorPane.setTopAnchor(pane, 0.0);
            AnchorPane.setBottomAnchor(pane, 0.0);
            AnchorPane.setLeftAnchor(pane, 0.0);
            AnchorPane.setRightAnchor(pane, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors du chargement de la vue RDV.");
        }
    }
    @FXML
    private void handleTraitementsButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/TraitementPatient.fxml"));
            Parent pane = loader.load();

            TraitementsPatientController controller = loader.getController();
            if (patient != null) {
                controller.setPatient(patient);
            }

            mainContent.getChildren().setAll(pane);
            AnchorPane.setTopAnchor(pane, 0.0);
            AnchorPane.setBottomAnchor(pane, 0.0);
            AnchorPane.setLeftAnchor(pane, 0.0);
            AnchorPane.setRightAnchor(pane, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors du chargement de la vue de Traitements.");
        }
    }


    /*private void ajouterBoutonsActions() {
        colView.setCellFactory(param -> new TableCell<>() {
            private final Button btnVoir = new Button("");

            {
                btnVoir.getStyleClass().add("table-button");
                btnVoir.setStyle("-fx-background-color: transparent;");
                ImageView iconVoir = new ImageView(new Image(getClass().getResourceAsStream("/utils/icons/view_icon.png")));
                iconVoir.setFitWidth(20);
                iconVoir.setFitHeight(20);
                iconVoir.setPreserveRatio(true);
                btnVoir.setGraphic(iconVoir);
                btnVoir.setOnAction(event -> {
                    RendezVous rdv = getTableView().getItems().get(getIndex());
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/RendezVous.fxml"));
                        Parent pane = loader.load();
                        RendezController controller = loader.getController();
                        controller.setRendezVous(rdv);
                        mainContent.getChildren().setAll(pane);
                        AnchorPane.setTopAnchor(pane, 0.0);
                        AnchorPane.setBottomAnchor(pane, 0.0);
                        AnchorPane.setLeftAnchor(pane, 0.0);
                        AnchorPane.setRightAnchor(pane, 0.0);
                    } catch (IOException e) {
                        e.printStackTrace();
                        showError("Erreur lors du chargement des détails du rendez-vous.");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnVoir);
            }
        });
    }*/
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/patient_login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) sidebar.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur de déconnexion");
        }
    }

    private void afficherTableRdvANDtraitementDansMainContent() {
        mainContent.getChildren().clear();
        mainContent.getChildren().addAll(
                new Label("Tableau de bord") {{
                    getStyleClass().add("page-title");
                }},
                new HBox(20) {{
                    getStyleClass().add("dashboard-stats");
                    getChildren().addAll(
                            new VBox() {{
                                getStyleClass().add("stat-card");
                                getChildren().addAll(new Label("Traitements") {{
                                    getStyleClass().add("stat-title");
                                }}, lblTotalTraitement);
                            }},
                            new VBox() {{
                                getStyleClass().add("stat-card");
                                getChildren().addAll(new Label("Rendez-vous") {{
                                    getStyleClass().add("stat-title");
                                }}, lblTotalRdv);
                            }}
                    );
                }}
                ,
                tableRendezVous,
                new Label("Traitement") {{
                    getStyleClass().add("section-title");
                }},
                tableTraitements

        );

        AnchorPane.setTopAnchor(tableRendezVous, 0.0);
        AnchorPane.setBottomAnchor(tableRendezVous, 0.0);
        AnchorPane.setLeftAnchor(tableRendezVous, 0.0);
        AnchorPane.setRightAnchor(tableRendezVous, 0.0);
    }

    private void chargerRendezVous() {
        if (patient != null) {
            List<RendezVous> derniersRdv = rendezVousDAO.getDerniersRendezVousPourPatient(patient.getId(),4);
            ObservableList<RendezVous> listeRdv = FXCollections.observableArrayList(derniersRdv);
            tableRendezVous.setItems(listeRdv);
        }
    }
    private void chargerTraitements() {
        if (patient == null) return;
        List<Traitement> liste = traitementDAO.getTraitementsParPatient(patient.getId());
        tableTraitements.setItems(FXCollections.observableArrayList(liste));
    }

    @FXML
    private void handleDashboard() {
        mettreAJourCompteurs();
        afficherTableRdvDansMainContent();
        afficherTableTraitementDansMainContent();
        afficherTableRdvANDtraitementDansMainContent();
    }
    private void afficherTableRdvDansMainContent() {
        mainContent.getChildren().clear();
        mainContent.getChildren().addAll(
                new Label("Tableau de bord") {{
                    getStyleClass().add("page-title");
                }}
        );
    }


    private void afficherTableTraitementDansMainContent() {
        mainContent.getChildren().clear();
        mainContent.getChildren().addAll(
                new Label("Tableau de bord") {{
                    getStyleClass().add("page-title");
                }}
        );
    }




    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return "";
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
    @FXML
    private void handleDemanderRdv() {
        if (patient == null) {
            showError("Patient non défini. Veuillez vous reconnecter.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/DemanderRendezVous.fxml"));
            Parent root = loader.load();

            DemanderRendezVousController controller = loader.getController();
            controller.setPatient(patient);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(btnDemanderRdv.getScene().getWindow());
            stage.setTitle("Demander un rendez-vous");
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            stage.setOnHidden(e -> {
                chargerRendezVous(); // Rafraîchir la liste après fermeture
                mettreAJourCompteurs();
            });

            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors du chargement du formulaire de demande.");
        }
    }
}
