package com.example.server.service;

import com.example.server.model.Appointment;
import com.example.server.model.MedicalExam;
import com.example.server.repository.AppointmentRepo;
import com.example.server.repository.MedicalExamRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class MedicalExamService {

    @Autowired
    private MedicalExamRepo medicalExamRepo;

    @Autowired
    private AppointmentRepo appointmentRepo;

    public List<MedicalExam> getAllMedicalExams() {
        return medicalExamRepo.findAll();
    }

    public MedicalExam getMedicalExamById(Integer id) {
        return medicalExamRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy hồ sơ khám bệnh"));
    }

    public MedicalExam getMedicalExamByAppointmentId(Integer appointmentId) {
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy lịch hẹn khám bệnh"));

        if (appointment.getType() != Appointment.AppointmentType.KHÁM_BỆNH) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Lịch hẹn này không phải lịch khám bệnh");
        }

        MedicalExam medicalExam = appointment.getMedicalExam();
        if (medicalExam == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Không tìm thấy hồ sơ khám bệnh cho lịch hẹn này");
        }

        return medicalExam;
    }

    public MedicalExam updateMedicalExam(Integer id, Map<String, Object> examData) {
        MedicalExam exam = getMedicalExamById(id);

        if (examData.containsKey("diagnosis")) {
            exam.setDiagnosis((String) examData.get("diagnosis"));
        }

        if (examData.containsKey("treatment")) {
            exam.setTreatment((String) examData.get("treatment"));
        }

        if (examData.containsKey("notes")) {
            exam.setNotes((String) examData.get("notes"));
        }

        return medicalExamRepo.save(exam);
    }

    public void deleteMedicalExam(Integer id) {
        MedicalExam exam = getMedicalExamById(id);
        medicalExamRepo.delete(exam);
    }
}