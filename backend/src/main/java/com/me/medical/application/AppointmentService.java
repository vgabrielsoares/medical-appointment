package com.me.medical.application;

import java.util.List;
import java.util.UUID;

import com.me.medical.application.dto.AppointmentDto;

public interface AppointmentService {
    AppointmentDto createAppointment(UUID doctorId, UUID slotId, UUID patientId);
    List<AppointmentDto> listByPatient(UUID patientId);
}
