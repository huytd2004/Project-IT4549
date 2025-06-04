package com.example.server.service;

import com.example.server.model.Appointment;
import com.example.server.model.User;
import com.example.server.model.Pet;
import com.example.server.repository.AppointmentRepo;
import com.example.server.repository.UserRepo;
import com.example.server.repository.PetRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticService {

    @Autowired
    private AppointmentRepo appointmentRepository;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private PetRepo petRepository;

    public Map<String, Object> getUserStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // Total users
        long totalUsers = userRepository.count();
        statistics.put("totalUsers", totalUsers);

        // New users in the last 30 days
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        long newUsers = userRepository.countByCreatedAtAfter(thirtyDaysAgo);
        statistics.put("newUsersLast30Days", newUsers);

        // User growth over the last 6 months
        Map<String, Long> monthlyGrowth = new HashMap<>();
        LocalDate now = LocalDate.now();

        for (int i = 5; i >= 0; i--) {
            YearMonth month = YearMonth.from(now.minusMonths(i));
            LocalDateTime monthStart = month.atDay(1).atStartOfDay();
            LocalDateTime monthEnd = month.atEndOfMonth().atTime(23, 59, 59);

            long usersInMonth = userRepository.countByCreatedAtBetween(monthStart, monthEnd);
            monthlyGrowth.put(month.toString(), usersInMonth);
        }
        statistics.put("userGrowthLast6Months", monthlyGrowth);

        return statistics;
    }

    public Map<String, Object> getAppointmentStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // Total appointments
        long totalAppointments = appointmentRepository.count();
        statistics.put("totalAppointments", totalAppointments);

        // Appointments by type
        Map<Appointment.AppointmentType, Long> appointmentsByType = appointmentRepository.findAll().stream()
                .collect(Collectors.groupingBy(Appointment::getType, Collectors.counting()));
        statistics.put("appointmentsByType", appointmentsByType);

        // Appointments by status
        Map<Appointment.AppointmentStatus, Long> appointmentsByStatus = appointmentRepository.findAll().stream()
                .collect(Collectors.groupingBy(Appointment::getStatus, Collectors.counting()));
        statistics.put("appointmentsByStatus", appointmentsByStatus);

        // Appointments over the last 6 months
        Map<String, Long> monthlyAppointments = new HashMap<>();
        LocalDate now = LocalDate.now();

        for (int i = 5; i >= 0; i--) {
            YearMonth month = YearMonth.from(now.minusMonths(i));
            LocalDateTime monthStart = month.atDay(1).atStartOfDay();
            LocalDateTime monthEnd = month.atEndOfMonth().atTime(23, 59, 59);

            long appointmentsInMonth = appointmentRepository.countByRequestTimeBetween(monthStart, monthEnd);
            monthlyAppointments.put(month.toString(), appointmentsInMonth);
        }
        statistics.put("appointmentsLast6Months", monthlyAppointments);

        return statistics;
    }

    public Map<String, Object> getRevenueStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // Total revenue from all completed appointments
        Double totalRevenue = appointmentRepository.findByStatus(Appointment.AppointmentStatus.HOÀN_THÀNH).stream()
                .mapToDouble(Appointment::getPrice)
                .sum();
        statistics.put("totalRevenue", totalRevenue);

        // Revenue by appointment type
        Map<Appointment.AppointmentType, Double> revenueByType = appointmentRepository
                .findByStatus(Appointment.AppointmentStatus.HOÀN_THÀNH).stream()
                .collect(Collectors.groupingBy(
                        Appointment::getType,
                        Collectors.summingDouble(Appointment::getPrice)));
        statistics.put("revenueByType", revenueByType);

        // Monthly revenue for the last 6 months
        Map<String, Double> monthlyRevenue = new HashMap<>();
        LocalDate now = LocalDate.now();

        for (int i = 5; i >= 0; i--) {
            YearMonth month = YearMonth.from(now.minusMonths(i));
            LocalDateTime monthStart = month.atDay(1).atStartOfDay();
            LocalDateTime monthEnd = month.atEndOfMonth().atTime(23, 59, 59);

            Double revenueInMonth = appointmentRepository
                    .findByStatusAndStartTimeBetween(
                            Appointment.AppointmentStatus.HOÀN_THÀNH,
                            monthStart,
                            monthEnd)
                    .stream()
                    .mapToDouble(Appointment::getPrice)
                    .sum();

            monthlyRevenue.put(month.toString(), revenueInMonth);
        }
        statistics.put("revenueLast6Months", monthlyRevenue);

        return statistics;
    }

    public Map<String, Object> getPetStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // Total pets
        long totalPets = petRepository.count();
        statistics.put("totalPets", totalPets);

        // Pets by species
        Map<String, Long> petsBySpecies = petRepository.findAll().stream()
                .collect(Collectors.groupingBy(Pet::getSpecies, Collectors.counting()));
        statistics.put("petsBySpecies", petsBySpecies);

        // Average pet age
        Double avgAge = petRepository.findAll().stream()
                .mapToDouble(Pet::getAge)
                .average()
                .orElse(0);
        statistics.put("averagePetAge", avgAge);

        return statistics;
    }

    public Map<String, Object> getDashboardSummary() {
        Map<String, Object> summary = new HashMap<>();

        // User counts
        long totalUsers = userRepository.count();
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        long newUsers = userRepository.countByCreatedAtAfter(thirtyDaysAgo);

        // Pet counts
        long totalPets = petRepository.count();

        // Appointment counts
        long totalAppointments = appointmentRepository.count();
        long pendingAppointments = appointmentRepository.countByStatus(Appointment.AppointmentStatus.CHỜ_XÁC_NHẬN);
        long todayAppointments = appointmentRepository.countByStartTimeBetween(
                LocalDateTime.now().truncatedTo(ChronoUnit.DAYS),
                LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).plusDays(1));

        // Revenue
        Double totalRevenue = appointmentRepository.findByStatus(Appointment.AppointmentStatus.HOÀN_THÀNH).stream()
                .mapToDouble(Appointment::getPrice)
                .sum();

        // Build summary
        summary.put("totalUsers", totalUsers);
        summary.put("newUsers", newUsers);
        summary.put("totalPets", totalPets);
        summary.put("totalAppointments", totalAppointments);
        summary.put("pendingAppointments", pendingAppointments);
        summary.put("todayAppointments", todayAppointments);
        summary.put("totalRevenue", totalRevenue);

        return summary;
    }
}