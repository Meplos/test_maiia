package com.maiia.pro.service;

import com.maiia.pro.controller.data.AppointmentDTO;
import com.maiia.pro.entity.Appointment;
import com.maiia.pro.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProAppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    public Appointment find(String appointmentId) {
        return appointmentRepository.findById(appointmentId).orElseThrow();
    }

    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    public List<Appointment> findByPractitionerId(Integer practitionerId) {
        return appointmentRepository.findByPractitionerId(practitionerId);
    }

    public Appointment create(AppointmentDTO appointmentDTO) {
        var appointment = Appointment.builder()
                .patientId(appointmentDTO.getPatientId())
                .practitionerId(appointmentDTO.getPractitionerId())
                .startDate(appointmentDTO.getStart())
                .endDate(appointmentDTO.getEnd())
                .build();
        appointmentRepository.save(appointment);
        return appointment;

    }
}
