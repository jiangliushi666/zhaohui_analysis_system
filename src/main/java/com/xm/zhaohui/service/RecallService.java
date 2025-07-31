package com.xm.zhaohui.service;

import com.xm.zhaohui.DTO.Interaction;
import com.xm.zhaohui.DTO.Item;
import com.xm.zhaohui.repo.InteractionRepository;
import com.xm.zhaohui.repo.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RecallService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private InteractionRepository interactionRepository;

    /**
     * 召回策略一：热门商品召回(Hot Item Recall)
     * 召回被互动次数最多的Top N个商品
     * @param topN 需要召回的商品数量
     * @return 热门商品列表
     */
    public List<Item> hotRecall(int topN){

        List<Interaction> allInteractions = interactionRepository.findAll();
        // 统计每个itemId出现次数
        // 将列表转换为流，方便链式操作
        // 按指定条件分组，统计每个分组的元素数量 ，最后收集成Map
        Map<Long, Long> itemCountMap = allInteractions.stream()
                .collect(Collectors.groupingBy(
                        Interaction::getItemId,
                        Collectors.counting()
                ));
        // 对map按value降序排序 并选出前topN个
        List<Long> topNItemIds = itemCountMap.entrySet().stream() // 将Map转换为Entry的流
                .sorted(Map.Entry.<Long,Long>comparingByValue().reversed())// 按照value降序排序
                .limit(topN) //提取前topN个
                .map(Map.Entry::getKey) //提取出key
                .collect(Collectors.toList()); // 收集成List
        // 根据这些itemId找出对应的Item对象
        return itemRepository.findAllById(topNItemIds);
    }


    /**
     * 召回策略二：基于用户兴趣标签的召回
     * 1、分析历史(Analyze History):先搞清楚这个用户都看过什么
     * 2、提炼兴趣(Extract Interests) 从他看过的，总结核心兴趣 topN个标签
     * 3、推荐新品(Recommend New Items) 根据兴趣，找一些没看过、可能喜欢的新品
     * @param userId 目标用户的ID
     * @param topNTags 需要分析的用户兴趣标签数量
     * @param recallSize 每个标签需要召回的商品数量
     * @return 基于用户兴趣标签召回的商品列表
     */
    public List<Item> tagRecall(Long userId,int topNTags, int recallSize){
        // 需要查询interaction里的userId，需要手动添加接口的方法，JPA会自动实现

        // 阶段一：找出该用户互动过的所有商品
        // 从interactionRepository中找出该userId的所有互动记录
        List<Interaction> userInteractions = interactionRepository.findByUserId(userId);

        // 提取所有互动的itemId
        // 去重，得到一个不重复的itemId集合
        Set<Long> interactedItemIds = userInteractions.stream()
                .map(Interaction::getItemId) // 将每个interaction对象转换为它的itemId，只需要itemId
                .collect(Collectors.toSet());// Set是自动去重的

        // 检查一下 如果没有互动历史就返回空列表
        if (interactedItemIds.isEmpty()){
            return Collections.emptyList();
        }


        // 阶段二：
        // 找出所有item的tags
        List<Item> interactedItems = itemRepository.findAllById(interactedItemIds);
        Map<String, Long> tagCounts = interactedItems.stream()
                .flatMap(item -> item.getTags().stream())
                .collect(Collectors.groupingBy(
                        x -> x,// 按标签字符串本身进行分组 Function.identify()
                        Collectors.counting()
                ));

        // 找到出现次数最多的前topNTags个标签
        List<String> topTags = tagCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(topNTags)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (topTags.isEmpty()){
            return Collections.emptyList();
        }

        // 阶段三：基于兴趣标签进行召回

        List<Item> candidateItems = itemRepository.findByTagsIn(topTags);

        List<Item> recommendedItems = candidateItems.stream()
                .filter(item -> !interactedItemIds.contains(item.getId())) // 过滤
                .distinct() // 去重
                .collect(Collectors.toList());
        Collections.shuffle(recommendedItems);

        return recommendedItems.stream()
                .limit(recallSize)
                .collect(Collectors.toList());
    }

    /**
     * 召回策略三：基于分类的召回
     * @param userId 目标用户ID
     * @param recallSize 召回多少商品
     * @return 基于分类召回的商品列表
     */
    public List<Item> categoryRecall(Long userId,int recallSize){
        // 找出用户互动过的所有商品id
        List<Interaction> userInteractions = interactionRepository.findByUserId(userId);
        Set<Long> interactedItemIds = userInteractions.stream()
                .map(Interaction::getItemId)
                .collect(Collectors.toSet());
        if (interactedItemIds.isEmpty()){
            return Collections.emptyList();
        }

        // 统计用户最感兴趣的分类
        List<Item> interactedItems = itemRepository.findAllById(interactedItemIds);
        Map<String, Long> categoryCounts = interactedItems.stream()
                .collect(Collectors.groupingBy(
                        Item::getCategory,
                        Collectors.counting()
                ));

        // 找到最受欢迎的一个分类
        String topCategory = categoryCounts.entrySet().stream()
                .max(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .orElse(null);
        if (categoryCounts.isEmpty()){
            return Collections.emptyList();
        }

        // 基于最受欢迎的分类进行召回
        List<Item> candidateItems = itemRepository.findByCategory(topCategory);

        return candidateItems.stream()
                .filter(item -> !interactedItemIds.contains(item.getId()))//过滤
                .limit(recallSize)
                .collect(Collectors.toList());
    }

    /**
     * 召回策略四：随机召回
     * @param recallSize
     * @return 随机召回的商品列表
     */
    public List<Item> randomRecall(int recallSize){
        // 获取商品总数
        long count = itemRepository.count();
        if (count ==0 ){
            return Collections.emptyList();
        }

        // 计算分页 每个recallSize个 因为商品可能太多了 JPA提供分页查询
        long totalPages = count / recallSize;
        if (totalPages == 0){
            // 总数小于recallSize返回所有
            return (List<Item>) itemRepository.findAll();
        }

        //随机选择一页
        int randomPageNumber = ThreadLocalRandom.current().nextInt((int) totalPages);

        // 分页查询
        Page<Item> page = itemRepository.findAll(PageRequest.of(randomPageNumber,recallSize));

        return page.getContent();
    }
}
