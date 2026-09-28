package com.dhayal.ticketdesk.dto;

import com.dhayal.ticketdesk.model.Ticket;
import com.dhayal.ticketdesk.model.TicketPriority;
import com.dhayal.ticketdesk.model.TicketStatus;
import java.time.Instant;

public record TicketResponse(
        Long id,
        String title,
        String description,
        TicketStatus status,
        TicketPriority priority,
        String assignee,
        Instant createdAt,
        Instant updatedAt,
        Instant resolvedAt
) {
    public static TicketResponse from(Ticket t) {
        return new TicketResponse(
                t.getId(), t.getTitle(), t.getDescription(), t.getStatus(), t.getPriority(),
                t.getAssignee(), t.getCreatedAt(), t.getUpdatedAt(), t.getResolvedAt());
    }
}
