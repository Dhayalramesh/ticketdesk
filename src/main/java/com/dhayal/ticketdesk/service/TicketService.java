package com.dhayal.ticketdesk.service;

import com.dhayal.ticketdesk.dto.TicketRequest;
import com.dhayal.ticketdesk.model.Ticket;
import com.dhayal.ticketdesk.model.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TicketService {

    Ticket createTicket(TicketRequest request);

    Ticket getTicketById(Long id);

    Page<Ticket> listTickets(TicketStatus status, Pageable pageable);

    Ticket updateTicket(Long id, TicketRequest request);

    Ticket updateStatus(Long id, TicketStatus newStatus);

    void deleteTicket(Long id);
}
