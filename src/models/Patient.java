package models;

import javafx.beans.property.*;

public class Patient {
    private IntegerProperty id;
    private StringProperty nom;
    private StringProperty prenom;
    private StringProperty dateNaiss;
    private StringProperty sexe;

    public Patient(int id, String nom, String prenom, String dateNaiss, String sexe) {
        this.id = new SimpleIntegerProperty(id);
        this.nom = new SimpleStringProperty(nom);
        this.prenom = new SimpleStringProperty(prenom);
        this.dateNaiss = new SimpleStringProperty(dateNaiss);
        this.sexe = new SimpleStringProperty(sexe);
    }

    public Patient() {
        this.id = new SimpleIntegerProperty();
        this.nom = new SimpleStringProperty();
        this.prenom = new SimpleStringProperty();
        this.dateNaiss = new SimpleStringProperty();
        this.sexe = new SimpleStringProperty();
    }
    // Getters JavaFX properties
    public IntegerProperty idProperty() { return id; }
    public StringProperty nomProperty() { return nom; }
    public StringProperty prenomProperty() { return prenom; }
    public StringProperty dateNaissProperty() { return dateNaiss; }
    public StringProperty sexeProperty() { return sexe; }

    // Getters classiques
    public int getId() { return id.get(); }
    public String getNom() { return nom.get(); }
    public String getPrenom() { return prenom.get(); }
    public String getDateNaiss() { return dateNaiss.get(); }
    public String getSexe() { return sexe.get(); }

    // Setters classiques
    public void setId(int id) { this.id.set(id); }
    public void setNom(String nom) { this.nom.set(nom); }
    public void setPrenom(String prenom) { this.prenom.set(prenom); }
    public void setDateNaiss(String dateNaiss) { this.dateNaiss.set(dateNaiss); }
    public void setSexe(String sexe) { this.sexe.set(sexe); }
}
