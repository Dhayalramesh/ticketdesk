package com.dhayal.ticketdesk.dto;

import com.dhayal.ticketdesk.model.TicketStatus;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(
        @NotNull(message = "status is required")
        TicketStatus status
) {
}
