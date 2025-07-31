package com.xm.zhaohui.repo;

import com.xm.zhaohui.DTO.Interaction;
import com.xm.zhaohui.DTO.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InteractionRepository extends JpaRepository<Interaction,Long> {


    /**
     * 根据用户ID查询所有互动记录。
     * JPA会根据方法名实现这个查询
     * findBy是查询的关键字
     * UserId是interaction类中的一个字段名
     * @param userId
     * @return
     */
    List<Interaction> findByUserId(Long userId);



}
