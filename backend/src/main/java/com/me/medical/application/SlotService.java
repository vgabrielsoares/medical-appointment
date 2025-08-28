package com.me.medical.application;

import java.util.List;
import java.util.UUID;

import com.me.medical.application.dto.SlotDto;

public interface SlotService {
    SlotDto createSlot(UUID doctorId, SlotDto dto);
    List<SlotDto> listSlots(UUID doctorId);
    SlotDto updateSlot(UUID doctorId, UUID slotId, SlotDto dto);
    void deleteSlot(UUID doctorId, UUID slotId);
}
