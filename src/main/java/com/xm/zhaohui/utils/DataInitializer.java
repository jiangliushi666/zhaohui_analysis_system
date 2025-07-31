package com.xm.zhaohui.utils;

import com.xm.zhaohui.DTO.Interaction;
import com.xm.zhaohui.DTO.Item;
import com.xm.zhaohui.DTO.User;
import com.xm.zhaohui.repo.InteractionRepository;
import com.xm.zhaohui.repo.ItemRepository;
import com.xm.zhaohui.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor //自动生成构造函数
@Slf4j
public class DataInitializer implements CommandLineRunner {

    //通过构造函数注入JPA Respository
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final InteractionRepository interactionRepository;


    @Override
    public void run(String... args) throws Exception {
        // 检查数据库是否已有数据 避免重复初始化
        if (userRepository.count() > 0){
            log.info("数据已存在，跳过初始化");
            return;
        }
        log.info("开始初始化数据");

        // 1.创建商品数据
        List<Item> items = createItems();
        itemRepository.saveAll(items);
        log.info("成功初始化商品数据:{}",items.size());

        //2.创建用户数据
        List<User> users = createUsers();
        userRepository.saveAll(users);
        log.info("成功初始化用户数据:{}",users.size());

        //3.模拟用户购买行为(生成 Ground Truth)
        createInteractions(users,items);
        log.info("成功初始化用户购买行为数据");
    }

    /**
    创建一批模拟商品
     */
    private List<Item> createItems() {
        List<String> categorys = Arrays.asList("电子产品","图书","家居用品","服装","食品");
        List<String> allTags = Arrays.asList("新款", "畅销", "经典", "java", "编程", "小说", "历史", "厨房", "卧室", "男装", "女装", "零食");

        return IntStream.rangeClosed(1,200)
                .mapToObj(i ->{
                    Item item = new Item();
                    item.setName("商品"+i);
                    item.setCategory(getRandomElement(categorys));
                    item.setTags(getRandomTags(allTags,3)); // 每个商品随机分配3个标签
                    return item;
                })
                .collect(Collectors.toList());
    }

    /**
     * 创建一批模拟用户
     */
    private List<User> createUsers() {
        List<String> allTags = Arrays.asList("新款","畅销","经典","java","编程","小说","历史","厨房","卧室","男装","女装","零食");

        return IntStream.rangeClosed(1,50)
                .mapToObj(i -> {
                    User user = new User();
                    user.setName("用户 "+i);
                    user.setPreferredTags(getRandomTags(allTags,4));//每个用户随机分配4个标签
                    return user;
                })
                .collect(Collectors.toList());
    }

    /**
     * 模拟用户与商品的交互，特别是购买行为
     */
    private void createInteractions(List<User> users,List<Item> items) {
        List<Interaction> interactions = new ArrayList<>();

        for (User user:users){
            Set<String> userTags = user.getPreferredTags();

            //筛选与用户兴趣标签匹配的商品作为可能购买的候选池
            List<Item> relevantItems = items.stream()
                    .filter(item -> item.getTags().stream().anyMatch(userTags::contains))
                    .collect(Collectors.toList());
            // 打乱候选池，增加随机性
            Collections.shuffle(relevantItems);

            // 每个用户随即购买5到15件相关商品
            int purchaseCount = ThreadLocalRandom.current().nextInt(5,16);
            relevantItems.stream()
                    .limit(purchaseCount)
                    .forEach(item -> {
                        Interaction interaction = new Interaction();
                        interaction.setUserId(user.getId());
                        interaction.setItemId(item.getId());
                        interaction.setType("purchase"); // 定义为购买行为
                        interactions.add(interaction);
                    });
        }
        interactionRepository.saveAll(interactions);
    }

    // 工具方法
    private <T> T getRandomElement(List<T> list) {
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

    private Set<String> getRandomTags(List<String> allTags, int count) {
        Collections.shuffle(allTags);
        return new HashSet<>(allTags.subList(0,count));
    }
}
