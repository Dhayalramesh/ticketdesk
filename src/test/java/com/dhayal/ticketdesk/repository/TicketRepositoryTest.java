package com.dhayal.ticketdesk.repository;

import com.dhayal.ticketdesk.model.Ticket;
import com.dhayal.ticketdesk.model.TicketPriority;
import com.dhayal.ticketdesk.model.TicketStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TicketRepositoryTest {

    @Autowired
    TicketRepository repository;

    @Test
    void findByStatus_returnsOnlyMatchingTickets() {
        Ticket open = new Ticket("Open issue", "desc", TicketPriority.LOW, "carol");
        Ticket resolved = new Ticket("Resolved issue", "desc", TicketPriority.LOW, "carol");
        resolved.setStatus(TicketStatus.RESOLVED);
        repository.save(open);
        repository.save(resolved);

        Page<Ticket> result = repository.findByStatus(TicketStatus.OPEN, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Open issue");
    }

    @Test
    void save_persistsAndAssignsId() {
        Ticket ticket = new Ticket("New ticket", "desc", TicketPriority.CRITICAL, "dave");

        Ticket saved = repository.save(ticket);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
    }
}
