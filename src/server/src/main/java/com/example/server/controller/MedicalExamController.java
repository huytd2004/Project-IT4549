package com.example.server.controller;

import com.example.server.model.MedicalExam;
import com.example.server.service.MedicalExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MedicalExamController {

    @Autowired
    private MedicalExamService medicalExamService;

    @GetMapping("/medicalexams")
    public ResponseEntity<List<MedicalExam>> getAllMedicalExams() {
        List<MedicalExam> exams = medicalExamService.getAllMedicalExams();
        return ResponseEntity.ok(exams);
    }

    @GetMapping("/medicalexams/{id}")
    public ResponseEntity<MedicalExam> getMedicalExamById(@PathVariable Integer id) {
        MedicalExam exam = medicalExamService.getMedicalExamById(id);
        return ResponseEntity.ok(exam);
    }

    @GetMapping("/appointments/{appointmentId}/medicalexam")
    public ResponseEntity<MedicalExam> getMedicalExamByAppointmentId(@PathVariable Integer appointmentId) {
        MedicalExam exam = medicalExamService.getMedicalExamByAppointmentId(appointmentId);
        return ResponseEntity.ok(exam);
    }

    @PutMapping("/medicalexams/{id}")
    public ResponseEntity<MedicalExam> updateMedicalExam(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> examData) {
        MedicalExam updatedExam = medicalExamService.updateMedicalExam(id, examData);
        return ResponseEntity.ok(updatedExam);
    }

    @DeleteMapping("/medicalexams/{id}")
    public ResponseEntity<Map<String, String>> deleteMedicalExam(@PathVariable Integer id) {
        medicalExamService.deleteMedicalExam(id);
        return ResponseEntity.ok(Map.of("message", "Medical exam record deleted successfully"));
    }
}