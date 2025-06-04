package com.example.server.controller;

import com.example.server.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticController {

    @Autowired
    private StatisticService statisticService;

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getUserStatistics() {
        Map<String, Object> statistics = statisticService.getUserStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/appointments")
    public ResponseEntity<Map<String, Object>> getAppointmentStatistics() {
        Map<String, Object> statistics = statisticService.getAppointmentStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/revenue")
    public ResponseEntity<Map<String, Object>> getRevenueStatistics() {
        Map<String, Object> statistics = statisticService.getRevenueStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/pets")
    public ResponseEntity<Map<String, Object>> getPetStatistics() {
        Map<String, Object> statistics = statisticService.getPetStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardSummary() {
        Map<String, Object> summary = statisticService.getDashboardSummary();
        return ResponseEntity.ok(summary);
    }
}