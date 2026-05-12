package models;

public class Admin {
    private int id;
    private String nom;
    private String prenom;
    private String username;
    private String password;
    private String specialite;

    // Constructeur complet
    public Admin(String nom, String prenom, String username, String password, String specialite) {
        this.nom = nom;
        this.prenom = prenom;
        this.username = username;
        this.password = password;
        this.specialite = specialite;
    }

    // Constructeur optionnel sans spécialité (pour compatibilité)
    public Admin(String nom, String prenom, String username, String password) {
        this(nom, prenom, username, password, null);
    }

    // Getters
    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getSpecialite() { return specialite; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }

    @Override
    public String toString() {
        return String.format("%s %s%s",
                nom,
                prenom,
                (specialite != null && !specialite.isEmpty()) ? " (" + specialite + ")" : "");
    }

    // Méthode utilitaire pour la validation
    public boolean isValid() {
        return nom != null && !nom.isEmpty() &&
                prenom != null && !prenom.isEmpty() &&
                username != null && !username.isEmpty() &&
                password != null && password.length() >= 6;
    }
}