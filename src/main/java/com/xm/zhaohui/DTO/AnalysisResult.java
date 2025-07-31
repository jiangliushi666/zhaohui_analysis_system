package com.xm.zhaohui.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 这是哪个策略？
 * 这个策略推荐了多少商品？
 * 用户真实购买了多少商品？
 * 推荐的商品中，命中了多少个用户真实购买的商品？
 * 最终的准确率和召回率分数是多少？
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResult {

    /**
     * 召回策略的名称
     */
    private String strategyName;

    /**
     * 召回策略推荐的商品总数
     */
    private int recommendedCount;

    /**
     * 用户真实购买的商品总数(Ground Truth)
     */
    private int groundTruthCount;

    /**
     * 推荐结果命中用户真实购买的商品数量
     */
    private int hitCount;

    /**
     * 准确率 (Precision) = hitCount / recommendedCount
     */
    private double precision;

    /**
     * 召回率 (Recall) = hitCount / groundTruthCount
     */
    private double recall;

}
