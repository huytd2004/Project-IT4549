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
public class BoardingAppointmentService {

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

    @Autowired
    private RoomRepo roomRepo;

    public Appointment createBoardingAppointment(int userId, int petId, int serviceId,
                                                 int centerId, Appointment.RoomType roomType, LocalDateTime startTime,
                                                 LocalDateTime endTime, String customerNotes) {
        // Validate inputs
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy người dùng"));

        Pet pet = petRepo.findById(petId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thú cưng"));

        ServiceInfo service = serviceInfoRepo.findById(serviceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy dịch vụ"));

        // Kiểm tra loại dịch vụ
        if (service.getServiceType() != ServiceInfo.ServiceType.LƯU_TRÚ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không phải dịch vụ lưu trú");
        }

        Center center = centerRepo.findById(centerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy trung tâm"));

        // Tìm phòng phù hợp với loại phòng và có sẵn trong thời gian yêu cầu
        Room availableRoom = findAvailableRoom(centerId, roomType, startTime, endTime);

        if (availableRoom == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Không có phòng " + roomType + " trống trong khoảng thời gian đã chọn");
        }

        // Tạo lịch hẹn mới
        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setPet(pet);
        appointment.setServiceInfo(service);
        appointment.setCenter(center);
        appointment.setRoom(availableRoom);
        appointment.setAppointmentRoomType(roomType);
        appointment.setStartTime(startTime);
        appointment.setEndTime(endTime);
        appointment.setRequestTime(LocalDateTime.now());
        appointment.setCustomerNotes(customerNotes);
        appointment.setType(Appointment.AppointmentType.LƯU_TRÚ);
        appointment.setStatus(Appointment.AppointmentStatus.CHỜ_XÁC_NHẬN);

        // Tính giá dựa trên thời gian
        Double price = calculateBoardingPrice(service, startTime, endTime);
        appointment.setPrice(price);

        return appointmentRepository.save(appointment);
    }

    private Room findAvailableRoom(int centerId, Appointment.RoomType roomType,
                                   LocalDateTime startTime, LocalDateTime endTime) {
        // Lấy danh sách phòng theo loại phòng và trung tâm
        List<Room> roomsOfType = roomRepo.findByCenterIdAndRoomType(centerId, roomType.toString());

        // Tìm phòng còn chỗ trống trong khoảng thời gian yêu cầu
        for (Room room : roomsOfType) {
            if (room.getIsAvailable()) {
                // Kiểm tra số lượng thú cưng đã đặt lịch lưu trú trong phòng này
                long currentPetsInRoom = appointmentRepository.countByRoomIdAndTimeOverlap(
                        room.getId(), startTime, endTime);

                // Nếu phòng còn chỗ trống, gán cho lịch hẹn này
                if (currentPetsInRoom < room.getCapacity()) {
                    return room;
                }
            }
        }

        return null; // Không tìm thấy phòng phù hợp
    }

    private Double calculateBoardingPrice(ServiceInfo service, LocalDateTime startTime, LocalDateTime endTime) {
        // Tính toán khoảng thời gian
        long totalHours = java.time.Duration.between(startTime, endTime).toHours();

        // Tính số ngày và số giờ dư
        int days = (int) (totalHours / 24);
        int remainingHours = (int) (totalHours % 24);

        Double totalPrice = 0.0;

        // Tính giá theo ngày
        if (days > 0) {
            totalPrice += days * service.getPricePerDay();
        }

        // Tính giá theo giờ cho số giờ dư
        if (remainingHours > 0) {
            // Tối đa chỉ tính một ngày, nếu số giờ dư nhân với giá theo giờ vượt quá giá ngày
            Double hourlyPrice = remainingHours * service.getPricePerHour();
            totalPrice += Math.min(hourlyPrice, service.getPricePerDay());
        }

        // Nếu không có dữ liệu giá theo ngày/giờ, sử dụng giá cơ bản
        if (totalPrice == 0) {
            totalPrice = service.getBasePrice();
        }

        return totalPrice;
    }
    public List<Appointment> getAllBoardingAppointments() {
        return appointmentRepository.findByType(Appointment.AppointmentType.LƯU_TRÚ);
    }

    public Appointment getBoardingAppointmentById(Integer id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lịch hẹn"));

        // Verify it's a boarding appointment
        if (appointment.getType() != Appointment.AppointmentType.LƯU_TRÚ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không phải lịch hẹn lưu trú");
        }

        return appointment;
    }

    public Appointment updateBoardingAppointment(Integer id, Appointment.RoomType roomType,
                                                 LocalDateTime startTime, LocalDateTime endTime,
                                                 String customerNotes, Appointment.AppointmentStatus status) {
        Appointment appointment = getBoardingAppointmentById(id);

        // Update only if supplied values are non-null
        if (roomType != null && roomType != appointment.getAppointmentRoomType()) {
            // Need to find a new available room
            Room newRoom = findAvailableRoom(
                    appointment.getCenter().getId(),
                    roomType,
                    startTime != null ? startTime : appointment.getStartTime(),
                    endTime != null ? endTime : appointment.getEndTime()
            );

            if (newRoom == null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Không có phòng " + roomType + " trống trong khoảng thời gian đã chọn");
            }

            appointment.setRoom(newRoom);
            appointment.setAppointmentRoomType(roomType);
        }

        if (startTime != null || endTime != null) {
            LocalDateTime newStartTime = startTime != null ? startTime : appointment.getStartTime();
            LocalDateTime newEndTime = endTime != null ? endTime : appointment.getEndTime();

            // When changing times, need to verify room is still available
            if (startTime != null || endTime != null) {
                // Check if current room is available for new time period
                Room currentRoom = appointment.getRoom();
                long overlappingAppointments = appointmentRepository.countByRoomIdAndTimeOverlapExcludingId(
                        currentRoom.getId(), newStartTime, newEndTime, id);

                if (overlappingAppointments >= currentRoom.getCapacity()) {
                    // Need to find another room
                    Room newRoom = findAvailableRoom(
                            appointment.getCenter().getId(),
                            appointment.getAppointmentRoomType(),
                            newStartTime, newEndTime
                    );

                    if (newRoom == null) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT,
                                "Không thể cập nhật thời gian vì không có phòng trống");
                    }

                    appointment.setRoom(newRoom);
                }
            }

            appointment.setStartTime(newStartTime);
            appointment.setEndTime(newEndTime);

            // Recalculate price based on new time period
            Double price = calculateBoardingPrice(appointment.getServiceInfo(), newStartTime, newEndTime);
            appointment.setPrice(price);
        }

        if (customerNotes != null) {
            appointment.setCustomerNotes(customerNotes);
        }

        if (status != null) {
            appointment.setStatus(status);
        }

        return appointmentRepository.save(appointment);
    }

    public void deleteBoardingAppointment(Integer id) {
        Appointment appointment = getBoardingAppointmentById(id);
        appointmentRepository.delete(appointment);
    }
}