package com.dhayal.ticketdesk.exception;

public class TicketNotFoundException extends RuntimeException {
    public TicketNotFoundException(Long id) {
        super("Ticket %d not found".formatted(id));
    }
}
