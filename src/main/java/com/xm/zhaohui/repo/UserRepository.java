package com.xm.zhaohui.repo;

import com.xm.zhaohui.DTO.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {

}
