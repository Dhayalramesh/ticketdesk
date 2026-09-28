package com.dhayal.ticketdesk.service;

import com.dhayal.ticketdesk.dto.TicketRequest;
import com.dhayal.ticketdesk.exception.InvalidStatusTransitionException;
import com.dhayal.ticketdesk.exception.TicketNotFoundException;
import com.dhayal.ticketdesk.model.Ticket;
import com.dhayal.ticketdesk.model.TicketPriority;
import com.dhayal.ticketdesk.model.TicketStatus;
import com.dhayal.ticketdesk.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    @Mock
    TicketRepository repository;

    @InjectMocks
    TicketServiceImpl service;

    @Test
    void createTicket_savesWithOpenStatus() {
        TicketRequest request = new TicketRequest("Printer broken", "Won't power on", TicketPriority.MEDIUM, "alice");
        when(repository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        Ticket result = service.createTicket(request);

        assertThat(result.getStatus()).isEqualTo(TicketStatus.OPEN);
        assertThat(result.getTitle()).isEqualTo("Printer broken");
        verify(repository).save(any(Ticket.class));
    }

    @Test
    void getTicketById_missing_throwsNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getTicketById(99L))
                .isInstanceOf(TicketNotFoundException.class);
    }

    @Test
    void updateStatus_validTransition_succeeds() {
        Ticket ticket = new Ticket("Bug", "desc", TicketPriority.HIGH, "bob"); // starts OPEN
        when(repository.findById(1L)).thenReturn(Optional.of(ticket));
        when(repository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        Ticket result = service.updateStatus(1L, TicketStatus.IN_PROGRESS);

        assertThat(result.getStatus()).isEqualTo(TicketStatus.IN_PROGRESS);
    }

    @Test
    void updateStatus_skippingStraightToResolved_throwsInvalidTransition() {
        Ticket ticket = new Ticket("Bug", "desc", TicketPriority.HIGH, "bob"); // starts OPEN
        when(repository.findById(1L)).thenReturn(Optional.of(ticket));

        assertThatThrownBy(() -> service.updateStatus(1L, TicketStatus.RESOLVED))
                .isInstanceOf(InvalidStatusTransitionException.class);
    }

    @Test
    void updateStatus_resolving_stampsResolvedAt() {
        Ticket ticket = new Ticket("Bug", "desc", TicketPriority.HIGH, "bob");
        ticket.setStatus(TicketStatus.IN_PROGRESS);
        when(repository.findById(1L)).thenReturn(Optional.of(ticket));
        when(repository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        Ticket result = service.updateStatus(1L, TicketStatus.RESOLVED);

        assertThat(result.getResolvedAt()).isNotNull();
    }

    @Test
    void updateStatus_reopeningResolvedTicket_clearsResolvedAt() {
        Ticket ticket = new Ticket("Bug", "desc", TicketPriority.HIGH, "bob");
        ticket.setStatus(TicketStatus.RESOLVED);
        ticket.setResolvedAt(java.time.Instant.now());
        when(repository.findById(1L)).thenReturn(Optional.of(ticket));
        when(repository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        Ticket result = service.updateStatus(1L, TicketStatus.IN_PROGRESS);

        assertThat(result.getResolvedAt()).isNull();
    }

    @Test
    void updateStatus_transitioningOutOfClosed_throwsInvalidTransition() {
        Ticket ticket = new Ticket("Bug", "desc", TicketPriority.HIGH, "bob");
        ticket.setStatus(TicketStatus.CLOSED);
        when(repository.findById(1L)).thenReturn(Optional.of(ticket));

        assertThatThrownBy(() -> service.updateStatus(1L, TicketStatus.OPEN))
                .isInstanceOf(InvalidStatusTransitionException.class);
    }
}
