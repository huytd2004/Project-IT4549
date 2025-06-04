package com.example.server.controller;

import com.example.server.dto.MedicalAppointmentRequest;
import com.example.server.model.Appointment;
import com.example.server.service.MedicalAppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MedicalAppointmentController {

    @Autowired
    private MedicalAppointmentService medicalAppointmentService;

    @PostMapping("/medicalappointments")
    public ResponseEntity<Appointment> createMedicalAppointment(@RequestBody MedicalAppointmentRequest request) {
        Appointment createdAppointment = medicalAppointmentService.createMedicalAppointment(request);
        return ResponseEntity.ok(createdAppointment);
    }
    @GetMapping("/medicalappointments")
    public ResponseEntity<List<Appointment>> getAllMedicalAppointments() {
        List<Appointment> appointments = medicalAppointmentService.getAllMedicalAppointments();
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/medicalappointments/{id}")
    public ResponseEntity<Appointment> getMedicalAppointmentById(@PathVariable Integer id) {
        Appointment appointment = medicalAppointmentService.getMedicalAppointmentById(id);
        return ResponseEntity.ok(appointment);
    }

    @PutMapping("/medicalappointments/{id}")
    public ResponseEntity<Appointment> updateMedicalAppointment(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> appointmentData) {

        // Extract updated values from request
        LocalDateTime startTime = appointmentData.containsKey("startTime") ?
                LocalDateTime.parse(appointmentData.get("startTime").toString()) : null;

        String customerNotes = appointmentData.containsKey("customerNotes") ?
                (String) appointmentData.get("customerNotes") : null;

        Appointment.AppointmentStatus status = appointmentData.containsKey("status") ?
                Appointment.AppointmentStatus.valueOf(appointmentData.get("status").toString()) : null;

        Appointment updatedAppointment = medicalAppointmentService.updateMedicalAppointment(
                id, startTime, customerNotes, status);

        return ResponseEntity.ok(updatedAppointment);
    }

    @DeleteMapping("/medicalappointments/{id}")
    public ResponseEntity<Map<String, String>> deleteMedicalAppointment(@PathVariable Integer id) {
        medicalAppointmentService.deleteMedicalAppointment(id);
        return ResponseEntity.ok(Map.of("message", "Appointment deleted successfully"));
    }
}