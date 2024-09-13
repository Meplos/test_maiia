package com.maiia.pro.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maiia.pro.entity.Appointment;
import com.maiia.pro.entity.Availability;
import com.maiia.pro.entity.TimeSlot;
import com.maiia.pro.repository.AppointmentRepository;
import com.maiia.pro.repository.AvailabilityRepository;
import com.maiia.pro.repository.TimeSlotRepository;

@Service
public class ProAvailabilityService {
    static public int APPOINTMENT_DURATION = 15;
    static public int MINIMUM_DURATION = 10;
    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    public List<Availability> findByPractitionerId(Integer practitionerId) {
        return availabilityRepository.findByPractitionerId(practitionerId);
    }

    public List<Availability> generateAvailabilities(Integer practitionerId) {
        List<TimeSlot> slots = timeSlotRepository.findByPractitionerId(practitionerId);
        List<Appointment> appointments = appointmentRepository.findByPractitionerId(practitionerId);

        List<Availability> cache = availabilityRepository.findByPractitionerId(practitionerId);
        if (!cache.isEmpty()) {
            return cache;
        }
        var result = new ArrayList<Availability>();
        for (TimeSlot timeSlot : slots) {
            List<Availability> availabilities = createAvailabilities(timeSlot, practitionerId,
                    appointments.stream().filter((var a) -> a.inSlot(timeSlot)).collect(Collectors.toList()));
            result.addAll(availabilities);
        }
        List<Availability> list = new ArrayList<>();
        availabilityRepository.saveAll(result).iterator().forEachRemaining(list::add);
        return list;
    }

    List<Availability> createAvailabilities(TimeSlot slot, int practitionerId, List<Appointment> appointments) {
        List<Availability> availabilities = new ArrayList<>();
        List<TimeSlot> freeSlots = getFreeSlots(slot, appointments);
        for (TimeSlot timeSlot : freeSlots) {
            var i = 0;
            var start = timeSlot.getStartDate();
            while (true) {
                start = timeSlot.getStartDate().plusMinutes(i * APPOINTMENT_DURATION);
                if (start.isAfter(timeSlot.getEndDate()) || start.isEqual(timeSlot.getEndDate())) {
                    break;
                }
                var endDate = start.plusMinutes(APPOINTMENT_DURATION);
                if (endDate.isAfter(timeSlot.getEndDate())) {
                    endDate = timeSlot.getEndDate();
                    if (Duration.between(start, endDate).toMinutes() < MINIMUM_DURATION) {
                        break;
                    }
                }
                var availlability = Availability.builder()
                        .startDate(start)
                        .endDate(endDate)
                        .practitionerId(practitionerId)
                        .build();
                i++;
                availabilities.add(availlability);
            }
        }
        return availabilities;
    }

    List<TimeSlot> getFreeSlots(TimeSlot slot, List<Appointment> appointments) {
        var freeSlot = new ArrayList<TimeSlot>();
        appointments.sort((var a1, var a2) -> a1.getStartDate().compareTo(a2.getStartDate()));
        if (appointments.isEmpty()) {
            freeSlot.add(slot);
            return freeSlot;
        }

        var start = slot.getStartDate();
        for (Appointment appointment : appointments) {
            if (appointment.getStartDate().isAfter(slot.getEndDate())) {
                break;
            }

            if (appointment.getStartDate().isAfter(start)) {
                freeSlot.add(
                        TimeSlot.builder().startDate(start).endDate(appointment.getStartDate()).build());
            }

            if (appointment.getEndDate().isAfter(start)) {
                start = appointment.getEndDate();
            }
        }

        if (start.isBefore(slot.getEndDate())) {
            freeSlot.add(
                    TimeSlot.builder().startDate(start).endDate(slot.getEndDate()).build());
        }

        return freeSlot;

    }

}
