package com.epsi.tp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    @Test
    public void testLoginAdmin() {
        UserService userService = new UserService();
        // Bons identifiants -> connexion réussie
        assertTrue(userService.login("admin", "admin"));
    }

    @Test
    public void testLoginInvalide() {
        UserService userService = new UserService();
        // Mauvais identifiants -> connexion refusée
        assertFalse(userService.login("admin", "wrong"));
    }
}
