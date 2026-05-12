package models.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.Database;
import models.Statistiques;

public class StatistiquesDAO {

    public int getTotalPatients(int adminId) {
        String sql = "SELECT COUNT(*) FROM patients WHERE admin_id = ?";
        try (
                Connection conn = Database.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, adminId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalRendezVous(int adminId) {
        String sql = "SELECT COUNT(*) FROM rendezvous WHERE admin_id = ?";
        try (
                Connection conn = Database.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, adminId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Statistiques.StatTypeTraitement> getStatsByTypeTraitement(int adminId) {
        List<Statistiques.StatTypeTraitement> stats = new ArrayList<>();
        String sql = "SELECT type AS typeTraitement, COUNT(*) AS nombre " +
                "FROM traitement WHERE admin_id = ? GROUP BY type";

        try (
                Connection conn = Database.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, adminId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                stats.add(new Statistiques.StatTypeTraitement(
                        rs.getString("typeTraitement"),
                        rs.getInt("nombre")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    public List<Statistiques.StatStatutRdv> getStatsByStatutRendezVous(int adminId) {
        List<Statistiques.StatStatutRdv> stats = new ArrayList<>();
        String sql = "SELECT statut, COUNT(*) AS nombre " +
                "FROM rendezvous WHERE admin_id = ? GROUP BY statut";

        try (
                Connection conn = Database.connect();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, adminId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                stats.add(new Statistiques.StatStatutRdv(
                        rs.getString("statut"),
                        rs.getInt("nombre")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
}
