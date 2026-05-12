package models.dao;

import models.Database;
import models.RendezVous;
import controllers.AdminDashboardController;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RendezVousDAO {

    private Connection getConnection() throws SQLException {
        return Database.connect();
    }

    // Récupérer tous les RDV d’un admin spécifique
    public List<RendezVous> getAllRendezVousParAdmin(int adminId) {
        List<RendezVous> list = new ArrayList<>();
        String sql = "SELECT id, date_rdv, statut, patient_id, admin_id FROM rendezvous WHERE admin_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, adminId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                RendezVous rdv = new RendezVous(
                        rs.getInt("id"),
                        rs.getDate("date_rdv").toLocalDate(),
                        rs.getString("statut"),
                        rs.getInt("patient_id"),
                        rs.getInt("admin_id")   // <-- Important ici aussi
                );
                list.add(rdv);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Ajouter un RDV en l’associant à l’admin courant
    public boolean ajouterRendezVous(RendezVous rdv) {
        String sql = "INSERT INTO rendezvous (date_rdv, statut, patient_id, admin_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDate(1, Date.valueOf(rdv.getDateRdv()));
            stmt.setString(2, rdv.getStatut());
            stmt.setInt(3, rdv.getPatientId());
            stmt.setInt(4, rdv.getAdminId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        rdv.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    // Modifier un RDV uniquement si ce RDV appartient bien à l’admin connecté
    public boolean modifierRendezVous(RendezVous rdv) {
        String sql = "UPDATE rendezvous SET date_rdv = ?, statut = ?, patient_id = ? WHERE id = ? AND admin_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setDate(1, Date.valueOf(rdv.getDateRdv()));
            pst.setString(2, rdv.getStatut());
            pst.setInt(3, rdv.getPatientId());
            pst.setInt(4, rdv.getId());
            pst.setInt(5, rdv.getAdminId());



            int affectedRows = pst.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Supprimer un RDV uniquement s’il appartient à l’admin connecté
    public boolean supprimerRendezVous(int id, int adminId) {
        String sql = "DELETE FROM rendezvous WHERE id = ? AND admin_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, id);
            pst.setInt(2, adminId);


            int affectedRows = pst.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Compter les RDV de cet admin uniquement
    public int getTotalRendezVous(int adminId) {
        int total = 0;
        String query = "SELECT COUNT(*) FROM rendezvous WHERE admin_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, adminId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                total = rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return total;
    }

    public List<RendezVous> getDerniersRendezVousPourPatient(int patientId, int limite) {
        List<RendezVous> rdvList = new ArrayList<>();
        String sql = "SELECT id, date_rdv, statut, patient_id, admin_id " +
                "FROM rendezvous " +
                "WHERE patient_id = ? AND date_rdv >= ? " +
                "ORDER BY date_rdv ASC " +
                "LIMIT ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, patientId);
            stmt.setDate(2, Date.valueOf(LocalDate.now())); // date du jour
            stmt.setInt(3, limite);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                RendezVous rdv = new RendezVous(
                        rs.getInt("id"),
                        rs.getDate("date_rdv").toLocalDate(),
                        rs.getString("statut"),
                        rs.getInt("patient_id"),
                        rs.getInt("admin_id")
                );
                rdvList.add(rdv);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rdvList;
    }
    // Obtenir les derniers RDV d’un admin, limités en nombre
    public List<RendezVous> getDerniersRendezVous(int limite, int adminId) {
        List<RendezVous> rdvList = new ArrayList<>();
        String sql = "SELECT id, date_rdv, statut, patient_id, admin_id " +
                "FROM rendezvous " +
                "WHERE admin_id = ? AND date_rdv >= ? " +
                "AND LOWER(statut) != 'demande' " +
        "ORDER BY date_rdv ASC " +
                "LIMIT ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, adminId);
            stmt.setDate(2, Date.valueOf(LocalDate.now())); // date du jour
            stmt.setInt(3, limite);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                RendezVous rdv = new RendezVous(
                        rs.getInt("id"),
                        rs.getDate("date_rdv").toLocalDate(),
                        rs.getString("statut"),
                        rs.getInt("patient_id"),
                        rs.getInt("admin_id")
                );
                rdvList.add(rdv);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rdvList;
    }
    public List<RendezVous> getRendezVousParPatient(int patientId) {
        List<RendezVous> rdvList = new ArrayList<>();
        String sql = "SELECT * FROM rendezvous WHERE patient_id = ?  ORDER BY date_rdv ASC";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                rdvList.add(mapResultSetToRendezVous(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rdvList;
    }
    private RendezVous mapResultSetToRendezVous(ResultSet rs) throws SQLException {
        RendezVous rdv = new RendezVous(
                rs.getInt("id"),
                rs.getDate("date_rdv").toLocalDate(),
                rs.getString("statut"),
                rs.getInt("patient_id"),
                rs.getInt("admin_id")
        );

        return rdv;
    }
    public int getAllRendezVousParPatient(int patientId) {
        int total = 0;
        String query = "SELECT COUNT(*) FROM rendezvous WHERE patient_id = ? ";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return total;
    }


    public List<RendezVous> getRendezVousByStatutAndAdmin(String statut, int adminId) {
        List<RendezVous> rendezVousList = new ArrayList<>();
        String sql = "SELECT * FROM rendezvous WHERE statut = ? AND admin_id = ? " ;

        try (Connection conn =Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, statut);
            stmt.setInt(2, adminId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                RendezVous rdv = new RendezVous(
                        rs.getInt("id"),
                        rs.getDate("date_rdv").toLocalDate(),
                        rs.getString("statut"),
                        rs.getInt("patient_id"),
                        rs.getInt("admin_id")
                );
                rendezVousList.add(rdv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rendezVousList;
    }





}
