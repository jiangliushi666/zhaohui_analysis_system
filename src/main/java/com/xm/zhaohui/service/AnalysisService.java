package com.xm.zhaohui.service;

import com.xm.zhaohui.DTO.Item;
import com.xm.zhaohui.DTO.RecallResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnalysisService {

    @Autowired
    private RecallService recallService;

    public List<RecallResult> getRecallResultsForUser(Long userId) {

        List<RecallResult> allResults = new ArrayList<>();
        final int RECALL_SIZE = 10; // 每个策略召回10个商品

        List<Item> hotRecallItems = recallService.hotRecall(RECALL_SIZE);
        allResults.add(new RecallResult("热门商品召回",hotRecallItems));

        List<Item> tagRecallItems = recallService.tagRecall(userId, 5, RECALL_SIZE);
        allResults.add(new RecallResult("用户兴趣标签召回",tagRecallItems));

        List<Item> categoryRecallItems = recallService.categoryRecall(userId, RECALL_SIZE);
        allResults.add(new RecallResult("基于分类召回",categoryRecallItems));

        List<Item> randomRecallItems = recallService.randomRecall(RECALL_SIZE);
        allResults.add(new RecallResult("随机召回",randomRecallItems));

        return allResults;
    }

}
