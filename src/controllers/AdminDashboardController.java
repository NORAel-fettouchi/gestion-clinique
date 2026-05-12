package controllers;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.Admin;
import models.RendezVous;
import models.dao.PatientManagDAO;
import models.dao.RendezVousDAO;
import models.dao.StatistiquesDAO;
import java.util.List;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {

    @FXML private Label lblTotalPatients;
    @FXML private Label lblTotalRdv;
    @FXML private TableView<RendezVous> tableRdv;
    @FXML private TableColumn<RendezVous, LocalDate> colDate;
    @FXML private TableColumn<RendezVous, String> colStatut;
    @FXML private TableColumn<RendezVous, Void> colView;
    @FXML private Button btnDemandes;
    @FXML private Button btnDashboard;
    @FXML private Button btnPatients;
    @FXML private Button btnTraitements;
    @FXML private Button btnLogout;
    @FXML private Button btnStats;
    @FXML private VBox mainContent;
    @FXML private VBox sidebar;
    @FXML private Label labelNomComplet;
    @FXML private Label labelId;
    @FXML private Text textInitiale;
    @FXML private Text textTopbar;

    private final PatientManagDAO patientManagDAO = new PatientManagDAO();
    private final RendezVousDAO rendezVousDAO = new RendezVousDAO();
    private final StatistiquesDAO statistiquesDAO = new StatistiquesDAO();
    private void mettreAJourCompteurs() {
        if (admin != null) {
            int totalPatients = statistiquesDAO.getTotalPatients(admin.getId());
            int totalRdv = statistiquesDAO.getTotalRendezVous(admin.getId());

            lblTotalPatients.setText(String.valueOf(totalPatients));
            lblTotalRdv.setText(String.valueOf(totalRdv));
        }
    }
    private Admin admin;

    public void setAdmin(Admin admin) {
        this.admin = admin;
        mettreAJourInfosAdmin();
        chargerRendezVous();
        chargerStatistiques(); // garde cette ligne si nécessaire pour les graphiques
        mettreAJourCompteurs(); // ajoute ceci pour les labels
        afficherTableRdvDansMainContent(); // 👈 ajoute ça ici pour afficher les données


    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private void mettreAJourInfosAdmin() {
        if (admin != null) {
            labelNomComplet.setText(capitalize(admin.getNom()) + " " + capitalize(admin.getPrenom()));
            labelId.setText("ID: " + admin.getId());
            if (admin.getNom() != null && !admin.getNom().isEmpty()) {
                String text = admin.getNom().substring(0, 1).toUpperCase();
                textInitiale.setText(text);
                textTopbar.setText(text);
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateRdv"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        this.chargerRendezVous();
        ajouterBoutonsActions();
        this.chargerStatistiques();
        afficherTableRdvDansMainContent();



    }


    private void afficherTableRdvDansMainContent() {
        mainContent.getChildren().clear();

        Label titre = new Label("Tableau de bord");
        titre.getStyleClass().add("page-title");

        HBox statsBox = new HBox(20);
        statsBox.getStyleClass().add("dashboard-stats");

        VBox patientCard = new VBox();
        patientCard.getStyleClass().add("stat-card");
        Label patientLabel = new Label("Patients");
        patientLabel.getStyleClass().add("stat-title");
        patientCard.getChildren().addAll(patientLabel, lblTotalPatients);

        VBox rdvCard = new VBox();
        rdvCard.getStyleClass().add("stat-card");
        Label rdvLabel = new Label("Rendez-vous");
        rdvLabel.getStyleClass().add("stat-title");
        rdvCard.getChildren().addAll(rdvLabel, lblTotalRdv);

        statsBox.getChildren().addAll(patientCard, rdvCard);

        Button btnRefresh = new Button("🔄");
        btnRefresh.setOnAction(e -> rafraichirTableauRendezVous());

        mainContent.getChildren().addAll(titre, statsBox, tableRdv, btnRefresh);

        AnchorPane.setTopAnchor(tableRdv, 0.0);
        AnchorPane.setBottomAnchor(tableRdv, 0.0);
        AnchorPane.setLeftAnchor(tableRdv, 0.0);
        AnchorPane.setRightAnchor(tableRdv, 0.0);
    }


    public void chargerRendezVous() {
        if (admin != null) {
            List<RendezVous> derniersRdv = rendezVousDAO.getDerniersRendezVous(7,admin.getId());
            ObservableList<RendezVous> listeRdv = FXCollections.observableArrayList(derniersRdv);
            tableRdv.setItems(listeRdv);
        }
    }


    private void chargerStatistiques() {
        if (admin != null) {
            int totalPatients = patientManagDAO.getTotalPatientsParAdmin(admin.getId());
            int totalRdv = rendezVousDAO.getTotalRendezVous(admin.getId());

            lblTotalPatients.setText(String.valueOf(totalPatients));
            lblTotalRdv.setText(String.valueOf(totalRdv));
        }
    }


    private void ajouterBoutonsActions() {
        colView.setCellFactory(param -> new TableCell<>()
        {
            private final Button btnVoir = new Button("");

            {

                // Configuration du bouton
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

                        // Important : adminId d'abord, pour charger les données
                        controller.setAdminId(rdv.getAdminId());
                        controller.setRendezVous(rdv); // Puis sélection du RDV dans la table

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
    }

    @FXML
    private void handleDashboard() {
        mettreAJourCompteurs();afficherTableRdvDansMainContent();
    }

    @FXML
    private void handlePatientsButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/patientManag.fxml"));
            Parent pane = loader.load();

            PatientManagController controller = loader.getController();
            if (admin != null) {
                controller.setAdminId(admin.getId());
            }

            mainContent.getChildren().setAll(pane);
            AnchorPane.setTopAnchor(pane, 0.0);
            AnchorPane.setBottomAnchor(pane, 0.0);
            AnchorPane.setLeftAnchor(pane, 0.0);
            AnchorPane.setRightAnchor(pane, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors du chargement de la vue Patients.");
        }
    }

    @FXML
    private void handleRDVButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/RendezVous.fxml"));
            Parent pane = loader.load();

            RendezController controller = loader.getController();
            if (admin != null) {
                controller.setAdminId(admin.getId());
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
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/admin_login.fxml"));
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

    @FXML
    private void handleTraitements() {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/views/Traitements.fxml"));
            Parent pane = loader.load();

            TraitementController controller = loader.getController();
            if (admin != null) {
                controller.setAdminId(admin.getId());
            }

            this.mainContent.getChildren().setAll(pane);
            AnchorPane.setTopAnchor(pane, 0.0);
            AnchorPane.setBottomAnchor(pane, 0.0);
            AnchorPane.setLeftAnchor(pane, 0.0);
            AnchorPane.setRightAnchor(pane, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors du chargement de la vue Traitements.");
        }
    }

    @FXML
    private void handleStatistiques() {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/views/Statistiques.fxml"));
            Parent pane = loader.load();

            // Transmission de l'adminId au contrôleur Statistiques
            StatistiquesController statistiquesController = loader.getController();
            if (admin != null) {
                statistiquesController.setAdminId(admin.getId());
            }

            this.mainContent.getChildren().setAll(pane);
            AnchorPane.setTopAnchor(pane, 0.0);
            AnchorPane.setBottomAnchor(pane, 0.0);
            AnchorPane.setLeftAnchor(pane, 0.0);
            AnchorPane.setRightAnchor(pane, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors du chargement de la vue Statistiques.");
        }
    }

    @FXML
    public void rafraichirTableauRendezVous() {
        List<RendezVous> rdvFuturs = rendezVousDAO.getDerniersRendezVous(1000, admin.getId());
        tableRdv.getItems().setAll(rdvFuturs);
    }


    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    // Ajoutez cette méthode de gestion
    @FXML
    private void handleDemandesButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/DemandeAdmin.fxml"));
            Parent pane = loader.load();

            DemandeAdminController controller = loader.getController();
            if (admin != null) {
                controller.setAdminId(admin.getId());
            }

            mainContent.getChildren().setAll(pane);
            AnchorPane.setTopAnchor(pane, 0.0);
            AnchorPane.setBottomAnchor(pane, 0.0);
            AnchorPane.setLeftAnchor(pane, 0.0);
            AnchorPane.setRightAnchor(pane, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors du chargement de la vue Demandes.");
        }
    }
}
