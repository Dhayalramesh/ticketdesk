package com.dhayal.ticketdesk.service;

import com.dhayal.ticketdesk.dto.TicketRequest;
import com.dhayal.ticketdesk.exception.InvalidStatusTransitionException;
import com.dhayal.ticketdesk.exception.TicketNotFoundException;
import com.dhayal.ticketdesk.model.Ticket;
import com.dhayal.ticketdesk.model.TicketStatus;
import com.dhayal.ticketdesk.repository.TicketRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

/**
 * Owns the ticket lifecycle business rule that the JPA layer can't express:
 * which status transitions are legal. A closed ticket is terminal; a
 * resolved ticket can be reopened if it turns out the fix didn't hold.
 */
@Service
public class TicketServiceImpl implements TicketService {

    private static final Map<TicketStatus, Set<TicketStatus>> ALLOWED_TRANSITIONS = Map.of(
            TicketStatus.OPEN, Set.of(TicketStatus.IN_PROGRESS, TicketStatus.CLOSED),
            TicketStatus.IN_PROGRESS, Set.of(TicketStatus.RESOLVED, TicketStatus.OPEN),
            TicketStatus.RESOLVED, Set.of(TicketStatus.CLOSED, TicketStatus.IN_PROGRESS),
            TicketStatus.CLOSED, Set.of()
    );

    private final TicketRepository repository;

    public TicketServiceImpl(TicketRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Ticket createTicket(TicketRequest request) {
        Ticket ticket = new Ticket(request.title(), request.description(), request.priority(), request.assignee());
        return repository.save(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public Ticket getTicketById(Long id) {
        return repository.findById(id).orElseThrow(() -> new TicketNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Ticket> listTickets(TicketStatus status, Pageable pageable) {
        return status == null ? repository.findAll(pageable) : repository.findByStatus(status, pageable);
    }

    @Override
    @Transactional
    public Ticket updateTicket(Long id, TicketRequest request) {
        Ticket ticket = getTicketById(id);
        ticket.setTitle(request.title());
        ticket.setDescription(request.description());
        ticket.setPriority(request.priority());
        ticket.setAssignee(request.assignee());
        return repository.save(ticket);
    }

    @Override
    @Transactional
    public Ticket updateStatus(Long id, TicketStatus newStatus) {
        Ticket ticket = getTicketById(id);
        TicketStatus current = ticket.getStatus();
        if (current == newStatus) {
            return ticket;
        }
        Set<TicketStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(current, Set.of());
        if (!allowed.contains(newStatus)) {
            throw new InvalidStatusTransitionException(current, newStatus);
        }

        ticket.setStatus(newStatus);
        if (newStatus == TicketStatus.RESOLVED) {
            ticket.setResolvedAt(Instant.now());
        } else if (current == TicketStatus.RESOLVED) {
            // reopened after being marked resolved - the earlier resolution no longer holds
            ticket.setResolvedAt(null);
        }
        return repository.save(ticket);
    }

    @Override
    @Transactional
    public void deleteTicket(Long id) {
        Ticket ticket = getTicketById(id);
        repository.delete(ticket);
    }
}
