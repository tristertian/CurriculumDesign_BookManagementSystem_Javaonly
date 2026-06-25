package com.bms.repository;

import com.bms.entity.User;

import java.util.Optional;

/**
 * 用户数据访问接口。
 */
public interface UserRepository {

    /**
     * 根据用户名查询用户。
     *
     * @param username 用户名
     * @return 用户 optional
     */
    Optional<User> findByUsername(String username);

    /**
     * 保存用户。
     *
     * @param user 用户
     * @return 保存后的用户
     */
    User save(User user);
}
