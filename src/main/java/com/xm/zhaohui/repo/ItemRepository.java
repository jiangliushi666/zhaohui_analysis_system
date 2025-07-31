package com.xm.zhaohui.repo;

import com.xm.zhaohui.DTO.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item,Long> {
    /**
     * 跟据标签查询商品
     * JPA会自动处理集合类型的查询
     * findBy是关键字
     * Tags是item类中字段名
     * in表示查询条件是集合中的一员
     * @param tags
     * @return
     */
    List<Item> findByTagsIn(List<String> tags);

    /**
     * 根据分类名查询商品
     * @param category 要查询的分类名
     * @return 该分类下的所有商品列表
     */
    List<Item> findByCategory(String category);

}
