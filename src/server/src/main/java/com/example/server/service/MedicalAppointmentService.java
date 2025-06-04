package com.example.server.service;

import com.example.server.dto.MedicalAppointmentRequest;
import com.example.server.model.*;
import com.example.server.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class MedicalAppointmentService {

    @Autowired
    private AppointmentRepo appointmentRepository;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PetRepo petRepo;

    @Autowired
    private ServiceInfoRepo serviceInfoRepo;

    @Autowired
    private CenterRepo centerRepo;

    public Appointment createMedicalAppointment(MedicalAppointmentRequest request) {
        // Validate inputs
        User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Pet pet = petRepo.findById(request.getPetId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found"));

        ServiceInfo service = serviceInfoRepo.findById(request.getServiceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found"));

        // Validate that this is a medical service
        if (service.getServiceType() != ServiceInfo.ServiceType.KHÁM_BỆNH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not a medical service");
        }

        Center center = centerRepo.findById(request.getCenterId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Center not found"));

//        User veterinarian = null;
//        if (request.getVeterinarianId() != null) {
//            veterinarian = userRepo.findById(request.getVeterinarianId())
//                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veterinarian not found"));
//        }
//
//        // Check if the veterinarian is available at the requested time
//        if (veterinarian != null) {
//            List<Appointment> existingAppointments = appointmentRepository.findByVeterinarianIdAndStartTimeBetween(
//                    veterinarian.getId(),
//                    request.getStartTime(),
//                    request.getEndTime()
//            );
//
//            if (!existingAppointments.isEmpty()) {
//                throw new ResponseStatusException(HttpStatus.CONFLICT,
//                        "Veterinarian already has an appointment during this time");
//            }
//        }

        // Create new appointment
        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setPet(pet);
        appointment.setServiceInfo(service);
        appointment.setCenter(center);
//        appointment.setVeterinarian(veterinarian);
        appointment.setStartTime(request.getStartTime());
//        appointment.setEndTime(request.getEndTime());
        appointment.setRequestTime(LocalDateTime.now());
        appointment.setCustomerNotes(request.getCustomerNotes());
        appointment.setType(Appointment.AppointmentType.KHÁM_BỆNH);
        appointment.setStatus(Appointment.AppointmentStatus.CHỜ_XÁC_NHẬN);
        appointment.setPrice(service.getBasePrice());

        // Initialize medical exam record
        MedicalExam exam = new MedicalExam();
        exam.setAppointment(appointment);
        appointment.setMedicalExam(exam);

        return appointmentRepository.save(appointment);
    }

    // Additional methods for managing medical appointments could go here
    public List<Appointment> getAllMedicalAppointments() {
        return appointmentRepository.findByType(Appointment.AppointmentType.KHÁM_BỆNH);
    }

    public Appointment getMedicalAppointmentById(Integer id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lịch hẹn"));

        // Verify it's a medical appointment
        if (appointment.getType() != Appointment.AppointmentType.KHÁM_BỆNH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không phải lịch hẹn khám bệnh");
        }

        return appointment;
    }

    public Appointment updateMedicalAppointment(Integer id, LocalDateTime startTime,
                                                String customerNotes, Appointment.AppointmentStatus status) {
        Appointment appointment = getMedicalAppointmentById(id);

        // Update only if supplied values are non-null
        if (startTime != null) {
            appointment.setStartTime(startTime);
        }

        if (customerNotes != null) {
            appointment.setCustomerNotes(customerNotes);
        }

        if (status != null) {
            appointment.setStatus(status);
        }

        return appointmentRepository.save(appointment);
    }

    public void deleteMedicalAppointment(Integer id) {
        Appointment appointment = getMedicalAppointmentById(id);
        appointmentRepository.delete(appointment);
    }
}