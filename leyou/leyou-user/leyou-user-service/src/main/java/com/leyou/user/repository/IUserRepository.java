package com.leyou.user.repository;

import com.leyou.user.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IUserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
}
