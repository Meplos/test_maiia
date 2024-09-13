package com.maiia.pro.controller.data;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Builder
@Data
public class AppointmentDTO {
    int practitionerId;
    int patientId;
    LocalDateTime start;
    LocalDateTime end;
}
