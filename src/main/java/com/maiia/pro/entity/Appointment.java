package com.maiia.pro.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private Integer patientId;
    private Integer practitionerId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public boolean inSlot(TimeSlot slot) {
        // START
        if (startDate.isAfter(slot.getStartDate()) && startDate.isBefore(slot.getEndDate())) {
            return true;
        }
        // END
        if (endDate.isAfter(slot.getStartDate()) && endDate.isBefore(slot.getEndDate())) {
            return true;
        }
        // MORE
        if (endDate.isBefore(slot.getStartDate()) && endDate.isAfter(slot.getEndDate())) {
            return true;
        }

        return false;
    }
}
