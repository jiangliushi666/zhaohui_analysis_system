package com.xm.zhaohui.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecallResult {
    private String strategyName;
    private List<Item> items;
}
