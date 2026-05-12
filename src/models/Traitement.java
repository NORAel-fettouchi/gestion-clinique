package models;

import java.time.LocalDate;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Traitement {
    private int id;
    private String nom;
    private String type;
    private String description;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private int idPatient;
    private final IntegerProperty patientId; // si tu veux utiliser JavaFX Property pour patientId
    private int adminId; // nouvel attribut pour lier le traitement à un admin

    // Constructeur
    public Traitement(int id, String nom, String type, String description, LocalDate dateDebut, LocalDate dateFin, int patientId, int adminId) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.idPatient = patientId;
        this.patientId = new SimpleIntegerProperty(patientId);
        this.adminId = adminId;
    }

    // Getters et Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public int getIdPatient() {
        return idPatient;
    }

    public void setIdPatient(int idPatient) {
        this.idPatient = idPatient;
        this.patientId.set(idPatient);
    }

    public int getPatientId() {
        return patientId.get();
    }

    public IntegerProperty patientIdProperty() {
        return patientId;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }
}
