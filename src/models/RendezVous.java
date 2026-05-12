package models;

import java.time.LocalDate;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RendezVous {
    private final IntegerProperty id;
    private final ObjectProperty<LocalDate> dateRdv;
    private final StringProperty statut;
    private final IntegerProperty patientId;
    private final IntegerProperty adminId;  // <- Nouveau champwa

    // Nouveau constructeur avec adminId
    public RendezVous(int id, LocalDate dateRdv, String statut, int patientId, int adminId) {
        this.id = new SimpleIntegerProperty(id);
        this.dateRdv = new SimpleObjectProperty<>(dateRdv);
        this.statut = new SimpleStringProperty(statut);
        this.patientId = new SimpleIntegerProperty(patientId);
        this.adminId = new SimpleIntegerProperty(adminId);
    }

    // Constructeur sans id (ex: création nouvelle instance)
    public RendezVous(LocalDate dateRdv, String statut, int patientId, int adminId) {
        this(0, dateRdv, statut, patientId, adminId);
    }

    // Ancien constructeur simplifié (si besoin, à adapter)
    public RendezVous(LocalDate dateRdv, String statut) {
        this(0, dateRdv, statut, 0, 0);
    }

    // Getters et setters pour les propriétés

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public LocalDate getDateRdv() {
        return dateRdv.get();
    }

    public ObjectProperty<LocalDate> dateRdvProperty() {
        return dateRdv;
    }

    public void setDateRdv(LocalDate dateRdv) {
        this.dateRdv.set(dateRdv);
    }

    public String getStatut() {
        return statut.get();
    }

    public StringProperty statutProperty() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut.set(statut);
    }

    public int getPatientId() {
        return patientId.get();
    }

    public IntegerProperty patientIdProperty() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId.set(patientId);
    }

    public int getAdminId() {
        return adminId.get();
    }

    public IntegerProperty adminIdProperty() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId.set(adminId);
    }
}
