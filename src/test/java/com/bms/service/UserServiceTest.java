package com.bms.service;

import com.bms.entity.User;
import com.bms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserService 单元测试。
 */
class UserServiceTest {

    private UserService service;

    @BeforeEach
    void setUp() {
        service = new UserService(new InMemoryUserRepository());
    }

    @Test
    void login_withValidAdmin_shouldReturnUser() {
        service.initializeDefaultUsers();
        Optional<User> user = service.login("admin", "admin");
        assertTrue(user.isPresent());
        assertTrue(user.get().isAdmin());
    }

    @Test
    void login_withValidClerk_shouldReturnUser() {
        service.initializeDefaultUsers();
        Optional<User> user = service.login("clerk", "clerk");
        assertTrue(user.isPresent());
        assertFalse(user.get().isAdmin());
    }

    @Test
    void login_withWrongPassword_shouldReturnEmpty() {
        service.initializeDefaultUsers();
        Optional<User> user = service.login("admin", "wrong");
        assertTrue(user.isEmpty());
    }

    @Test
    void login_withEmptyUsername_shouldReturnEmpty() {
        Optional<User> user = service.login("", "admin");
        assertTrue(user.isEmpty());
    }

    private static class InMemoryUserRepository implements UserRepository {

        private final Map<String, User> users = new HashMap<>();

        @Override
        public Optional<User> findByUsername(String username) {
            return Optional.ofNullable(users.get(username));
        }

        @Override
        public User save(User user) {
            users.put(user.getUsername(), user);
            return user;
        }
    }
}
