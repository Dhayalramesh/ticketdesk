package com.dhayal.ticketdesk.repository;

import com.dhayal.ticketdesk.model.Ticket;
import com.dhayal.ticketdesk.model.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Page<Ticket> findByStatus(TicketStatus status, Pageable pageable);
}
