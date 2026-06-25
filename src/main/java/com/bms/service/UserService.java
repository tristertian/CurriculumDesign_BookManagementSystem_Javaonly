package com.bms.service;

import com.bms.entity.User;
import com.bms.repository.UserRepository;

import java.util.Optional;

/**
 * 用户业务逻辑层。
 */
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 用户登录验证。
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录成功返回用户，失败返回 empty
     */
    public Optional<User> login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return Optional.empty();
        }
        Optional<User> userOpt = userRepository.findByUsername(username.trim());
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            return userOpt;
        }
        return Optional.empty();
    }

    /**
     * 初始化默认账号。
     */
    public void initializeDefaultUsers() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            userRepository.save(new User("admin", "admin", User.Role.ADMIN));
        }
        if (userRepository.findByUsername("clerk").isEmpty()) {
            userRepository.save(new User("clerk", "clerk", User.Role.CLERK));
        }
    }
}
