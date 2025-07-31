package com.xm.zhaohui.controller;

import com.xm.zhaohui.DTO.RecallResult;
import com.xm.zhaohui.DTO.User;
import com.xm.zhaohui.repo.UserRepository;
import com.xm.zhaohui.service.AnalysisService;
import com.xm.zhaohui.service.RecallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class AnalysisController {

    @Autowired
    private RecallService recallService;

    @Autowired
    private AnalysisService analysisService;
    @Autowired
    private UserRepository userRepository;

    /**
     * API端点：获取所有用户列表
     * 返回一个包含所有用户的JSON数组
     */
    @GetMapping("/api/users")
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    /**
     * 获取指定用户的召回分析结果
     * @param userID
     * @return 返回一个包含召回结果的JSON数组
     */
    @GetMapping("/api/analyze/{userId}")
    public List<RecallResult> analyze(@PathVariable long userID){
        return analysisService.getRecallResultsForUser(userID);
    }
}
