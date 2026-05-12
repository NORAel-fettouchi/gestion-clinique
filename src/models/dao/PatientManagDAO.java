package models.dao;

import models.Patient;
import models.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientManagDAO {

    private Connection connection;

    public PatientManagDAO() {
        try {
            connection = Database.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Récupérer tous les patients d'un admin spécifique
    public List<Patient> getAllPatientsByAdmin(int adminId) {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE admin_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, adminId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Patient p = new Patient(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("date_naissance"),
                        rs.getString("sexe")
                );
                patients.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }

    // Ajouter patient en liant à l'admin et récupérer l'ID généré
    public boolean ajouterPatient(Patient p, int adminId) {
        String sql = "INSERT INTO patients (nom, prenom, date_naissance, sexe, admin_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, p.getNom());
            stmt.setString(2, p.getPrenom());
            stmt.setString(3, p.getDateNaiss());
            stmt.setString(4, p.getSexe());
            stmt.setInt(5, adminId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    p.setId(generatedId);  // Met à jour l'objet patient avec l'ID
                } else {
                    return false;
                }
            }
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Modifier patient (en supposant admin_id ne change pas)
    public boolean modifierPatient(Patient p, int adminId) {
        String sql = "UPDATE patients SET nom = ?, prenom = ?, date_naissance = ?, sexe = ? WHERE id = ? AND admin_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, p.getNom());
            stmt.setString(2, p.getPrenom());
            stmt.setString(3, p.getDateNaiss());
            stmt.setString(4, p.getSexe());
            stmt.setInt(5, p.getId());
            stmt.setInt(6, adminId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Supprimer patient (uniquement s'il appartient à l'admin)
    public boolean supprimerPatient(int patientId, int adminId) {
        String sql = "DELETE FROM patients WHERE id = ? AND admin_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            stmt.setInt(2, adminId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Nombre total patients par admin
    public int getTotalPatientsParAdmin(int adminId) {
        String sql = "SELECT COUNT(*) FROM patients WHERE admin_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, adminId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Vérifie si un patient existe avec un id donné
    public boolean existePatient(int patientId) {
        String sql = "SELECT id FROM patients WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Récupérer un patient par ID
    public Patient getPatientById(int patientId) {
        String sql = "SELECT * FROM patients WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Patient(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("date_naissance"),
                        rs.getString("sexe")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
