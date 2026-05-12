package models.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Database;
import models.Traitement;

public class TraitementDAO {

    // Récupérer uniquement les traitements liés à un admin donné
    public List<Traitement> getTraitementsByAdminId(int adminId) {
        List<Traitement> traitements = new ArrayList<>();
        String sql = "SELECT * FROM Traitement WHERE admin_id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, adminId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Date dateDebutSql = rs.getDate("date_debut");
                Date dateFinSql = rs.getDate("date_fin");

                Traitement traitement = new Traitement(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("type"),
                        rs.getString("description"),
                        dateDebutSql != null ? dateDebutSql.toLocalDate() : null,
                        dateFinSql != null ? dateFinSql.toLocalDate() : null,
                        rs.getInt("id_patient"),  // Vérifie bien le nom de la colonne dans ta base
                        rs.getInt("admin_id")
                );
                traitements.add(traitement);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return traitements;
    }

    // Ajouter un traitement avec adminId
    public boolean ajouterTraitement(Traitement t) {
        if (t.getDateDebut() != null && t.getDateFin() != null && !t.getDateDebut().isBefore(t.getDateFin())) {
            System.err.println("Erreur: la date de début doit être antérieure à la date de fin.");
            return false;
        } else {
            String sql = "INSERT INTO Traitement(nom, type, description, date_debut, date_fin, id_patient, admin_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = Database.connect();
                 PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

                stmt.setString(1, t.getNom());
                stmt.setString(2, t.getType());
                stmt.setString(3, t.getDescription());

                if (t.getDateDebut() != null) {
                    stmt.setDate(4, Date.valueOf(t.getDateDebut()));
                } else {
                    stmt.setNull(4, java.sql.Types.DATE);
                }

                if (t.getDateFin() != null) {
                    stmt.setDate(5, Date.valueOf(t.getDateFin()));
                } else {
                    stmt.setNull(5, java.sql.Types.DATE);
                }

                stmt.setInt(6, t.getIdPatient());
                stmt.setInt(7, t.getAdminId());  // IMPORTANT : insérer l'id de l'admin

                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Échec de l'ajout du traitement, aucune ligne affectée.");
                }

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        t.setId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Échec de l'ajout du traitement, aucun ID obtenu.");
                    }
                }

                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    // Modifier un traitement seulement si adminId correspond
    public boolean modifierTraitement(Traitement traitement) {
        String sql = "UPDATE Traitement SET nom = ?, type = ?, description = ?, date_debut = ?, date_fin = ?, id_patient = ? WHERE id = ? AND admin_id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, traitement.getNom());
            stmt.setString(2, traitement.getType());
            stmt.setString(3, traitement.getDescription());

            if (traitement.getDateDebut() != null) {
                stmt.setDate(4, Date.valueOf(traitement.getDateDebut()));
            } else {
                stmt.setNull(4, java.sql.Types.DATE);
            }

            if (traitement.getDateFin() != null) {
                stmt.setDate(5, Date.valueOf(traitement.getDateFin()));
            } else {
                stmt.setNull(5, java.sql.Types.DATE);
            }

            stmt.setInt(6, traitement.getIdPatient());
            stmt.setInt(7, traitement.getId());
            stmt.setInt(8, traitement.getAdminId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Supprimer un traitement seulement si adminId correspond
    public boolean supprimerTraitement(int id, int adminId) {
        String sql = "DELETE FROM Traitement WHERE id = ? AND admin_id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.setInt(2, adminId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public ObservableList<Traitement> getTraitementsByPatientId(int patientId) {
        ObservableList<Traitement> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM traitement WHERE id_patient = ?";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Traitement t = new Traitement(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("type"),
                        rs.getString("description"),
                        rs.getDate("date_debut").toLocalDate(),
                        rs.getDate("date_fin").toLocalDate(),
                        rs.getInt("id_patient"),
                        rs.getInt("admin_id")
                );
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public int getTraitementsTotal(int id_patient) {
        String sql = "SELECT COUNT(*) FROM traitement WHERE id_patient = ?";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id_patient);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
    // Récupérer les traitements par patient
    public List<Traitement> getTraitementsParPatient(int patientId) {
        List<Traitement> traitements = new ArrayList<>();
        String query = "SELECT * FROM traitement WHERE id_patient = ? ORDER BY date_debut DESC";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Traitement traitement = new Traitement(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("type"),
                        rs.getString("description"),
                        rs.getDate("date_debut") != null ? rs.getDate("date_debut").toLocalDate() : null,
                        rs.getDate("date_fin") != null ? rs.getDate("date_fin").toLocalDate() : null,
                        rs.getInt("id_patient"),
                        rs.getInt("admin_id")
                );
                traitements.add(traitement);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return traitements;
    }
}
