package models.dao;
import java.util.List;
import java.util.ArrayList;

import models.Admin;
import models.Database;

import java.sql.*;

public class AdminDAO {
    public Admin authentifier(String username, String password) {
        String sql = "SELECT * FROM admin WHERE username = ? AND password = ?";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Admin admin = new Admin(
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("specialite")
                );
                admin.setId(rs.getInt("id"));
                return admin;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean creerCompte(Admin admin) {
        // First check if username exists
        if (usernameExists(admin.getUsername())) {
            return false;
        }

        String sql = "INSERT INTO admin (nom, prenom, username, password, specialite) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, admin.getNom());
            stmt.setString(2, admin.getPrenom());
            stmt.setString(3, admin.getUsername());
            stmt.setString(4, admin.getPassword());
            stmt.setString(5, admin.getSpecialite());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        admin.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM admin WHERE username = ?";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean modifierSpecialite(int adminId, String nouvelleSpecialite) {
        String sql = "UPDATE admin SET specialite = ? WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nouvelleSpecialite);
            stmt.setInt(2, adminId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Admin trouverParId(int id) {
        String sql = "SELECT * FROM admin WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Admin admin = new Admin(
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("specialite")
                );
                admin.setId(id);
                return admin;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<Admin> getAdminsBySpecialite(String specialite) {
        List<Admin> admins = new ArrayList<>();
        String sql = "SELECT * FROM admin WHERE specialite = ?";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, specialite);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Admin admin = new Admin(
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("specialite")
                );
                admin.setId(rs.getInt("id"));
                admins.add(admin);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return admins;
    }
}