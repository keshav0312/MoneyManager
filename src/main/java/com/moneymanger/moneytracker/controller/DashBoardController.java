package com.moneymanger.moneytracker.controller;


import com.moneymanger.moneytracker.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashBoardController {
    private  final DashboardService  dashboardService;
    @GetMapping("/")
    public ResponseEntity<Map<String,Object>>dashboard(){
     Map<String,Object> map  =   dashboardService.getDashboard();
     return ResponseEntity.ok(map);
    }
}
