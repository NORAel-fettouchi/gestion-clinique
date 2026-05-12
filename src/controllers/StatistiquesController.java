package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import models.Statistiques;
import models.dao.StatistiquesDAO;

public class StatistiquesController {
    @FXML
    private Label lblTotalPatients;
    @FXML
    private Label lblTotalRendezVous;
    @FXML
    private BarChart<String, Number> barChartTraitements;
    @FXML
    private PieChart pieChartRendezVous;

    private final StatistiquesDAO statistiquesDAO = new StatistiquesDAO();
    private int adminId;

    public void setAdminId(int adminId) {
        this.adminId = adminId;
        loadStatistics(); // Charger les stats dès qu'on a l'adminId
    }

    // Ne pas utiliser initialize() car adminId n’est pas encore défini à ce moment-là
    private void loadStatistics() {
        lblTotalPatients.setText(String.valueOf(statistiquesDAO.getTotalPatients(adminId)));
        lblTotalRendezVous.setText(String.valueOf(statistiquesDAO.getTotalRendezVous(adminId)));

        // BarChart
        XYChart.Series<String, Number> seriesTraitements = new XYChart.Series<>();
        seriesTraitements.setName("Traitements");
        for (Statistiques.StatTypeTraitement stat : statistiquesDAO.getStatsByTypeTraitement(adminId)) {
            seriesTraitements.getData().add(new XYChart.Data<>(stat.getTypeTraitement(), stat.getNombre()));
        }
        barChartTraitements.getData().clear();
        barChartTraitements.getData().add(seriesTraitements);

        // PieChart
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Statistiques.StatStatutRdv stat : statistiquesDAO.getStatsByStatutRendezVous(adminId)) {
            pieChartData.add(new PieChart.Data(stat.getStatut(), stat.getNombre()));
        }
        pieChartRendezVous.setData(pieChartData);
    }
}
