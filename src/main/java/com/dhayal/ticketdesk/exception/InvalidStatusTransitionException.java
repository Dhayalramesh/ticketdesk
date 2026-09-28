package com.dhayal.ticketdesk.exception;

import com.dhayal.ticketdesk.model.TicketStatus;

public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(TicketStatus from, TicketStatus to) {
        super("Cannot transition ticket from %s to %s".formatted(from, to));
    }
}
