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
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Availability {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private Integer practitionerId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public boolean isAvaillable(List<Appointment> appointments) {
        for (Appointment appointment : appointments) {
            if (getStartDate().isEqual(appointment.getStartDate()) || getEndDate().isEqual(appointment.getEndDate())) {
                return false;
            }

        }
        return true;
    }
}
