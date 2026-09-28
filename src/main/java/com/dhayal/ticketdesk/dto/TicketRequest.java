package com.dhayal.ticketdesk.dto;

import com.dhayal.ticketdesk.model.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Payload for creating a ticket. A record rather than the {@code Ticket} entity
 * itself, so the API's public contract can evolve independently of the schema,
 * and so a caller can never set fields (id, status, timestamps) that the server owns.
 */
public record TicketRequest(
        @NotBlank(message = "title must not be blank")
        @Size(max = 200, message = "title must be at most 200 characters")
        String title,

        @Size(max = 2000, message = "description must be at most 2000 characters")
        String description,

        @NotNull(message = "priority is required")
        TicketPriority priority,

        @Size(max = 100, message = "assignee must be at most 100 characters")
        String assignee
) {
}
