package com.epsi.tp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Logger;

public class UserService {

    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());

    // Le mot de passe n'est plus en dur : on le lit depuis une variable d'environnement
    private static final String DB_USER = "root";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mydb";

    public boolean login(String username, String password) {
        LOGGER.info("Tentative de connexion de l'utilisateur : " + username);

        boolean isLoggedIn = "admin".equals(username) && "admin".equals(password);
        if (isLoggedIn) {
            LOGGER.info("Administrateur connecté avec succès.");
        } else {
            LOGGER.warning("Identifiants invalides.");
        }
        return isLoggedIn;
    }

    public void getUserDetails(String username) {
        String dbPassword = System.getenv("DB_PASSWORD");
        // Requête paramétrée pour éviter l'injection SQL
        String query = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LOGGER.info("Utilisateur trouvé : " + rs.getString("username"));
                }
            }
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération de l'utilisateur : " + e.getMessage());
        }
    }
}
