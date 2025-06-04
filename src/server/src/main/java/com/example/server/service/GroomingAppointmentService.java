package com.example.server.service;

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
public class GroomingAppointmentService {

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

    public Appointment createGroomingAppointment(int userId, int petId, int serviceId,
                                                 int centerId, LocalDateTime startTime, String customerNotes) {
        // Validate inputs
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Pet pet = petRepo.findById(petId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found"));

        ServiceInfo service = serviceInfoRepo.findById(serviceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found"));

        // Validate that this is a grooming service
        if (service.getServiceType() != ServiceInfo.ServiceType.LÀM_ĐẸP) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not a grooming service");
        }

        Center center = centerRepo.findById(centerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Center not found"));

        // Create new appointment
        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setPet(pet);
        appointment.setServiceInfo(service);
        appointment.setCenter(center);
        appointment.setStartTime(startTime);
//        appointment.setEndTime(endTime);
        appointment.setRequestTime(LocalDateTime.now());
        appointment.setCustomerNotes(customerNotes);
        appointment.setType(Appointment.AppointmentType.LÀM_ĐẸP);
        appointment.setStatus(Appointment.AppointmentStatus.CHỜ_XÁC_NHẬN);
        appointment.setPrice(service.getBasePrice());

        return appointmentRepository.save(appointment);
    }
    public List<Appointment> getAllGroomingAppointments() {
        return appointmentRepository.findByType(Appointment.AppointmentType.LÀM_ĐẸP);
    }

    public Appointment getGroomingAppointmentById(Integer id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lịch hẹn"));

        // Verify it's a grooming appointment
        if (appointment.getType() != Appointment.AppointmentType.LÀM_ĐẸP) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không phải lịch hẹn làm đẹp");
        }

        return appointment;
    }

    public Appointment updateGroomingAppointment(Integer id, LocalDateTime startTime,
                                                 String customerNotes, Appointment.AppointmentStatus status) {
        Appointment appointment = getGroomingAppointmentById(id);

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

    public void deleteGroomingAppointment(Integer id) {
        Appointment appointment = getGroomingAppointmentById(id);
        appointmentRepository.delete(appointment);
    }
}