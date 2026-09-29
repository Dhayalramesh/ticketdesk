package com.dhayal.ticketdesk.controller;

import com.dhayal.ticketdesk.dto.TicketRequest;
import com.dhayal.ticketdesk.exception.InvalidStatusTransitionException;
import com.dhayal.ticketdesk.exception.TicketNotFoundException;
import com.dhayal.ticketdesk.model.Ticket;
import com.dhayal.ticketdesk.model.TicketPriority;
import com.dhayal.ticketdesk.model.TicketStatus;
import com.dhayal.ticketdesk.service.TicketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TicketController.class)
class TicketControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    TicketService ticketService;

    @Test
    void createTicket_returns201WithBody() throws Exception {
        Ticket saved = new Ticket("Printer broken", "Won't power on", TicketPriority.MEDIUM, "alice");
        when(ticketService.createTicket(any())).thenReturn(saved);

        TicketRequest request = new TicketRequest("Printer broken", "Won't power on", TicketPriority.MEDIUM, "alice");

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Printer broken"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void createTicket_blankTitle_returns400() throws Exception {
        TicketRequest request = new TicketRequest("", "desc", TicketPriority.MEDIUM, "alice");

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_missing_returns404() throws Exception {
        when(ticketService.getTicketById(42L)).thenThrow(new TicketNotFoundException(42L));

        mockMvc.perform(get("/api/tickets/42"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listTickets_returnsPageOfTickets() throws Exception {
        Ticket ticket = new Ticket("Bug", "desc", TicketPriority.LOW, "carol");
        Page<Ticket> page = new PageImpl<>(java.util.List.of(ticket));
        when(ticketService.listTickets(eq(null), any())).thenReturn(page);

        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Bug"));
    }

    @Test
    void updateStatus_invalidTransition_returns409() throws Exception {
        when(ticketService.updateStatus(1L, TicketStatus.RESOLVED))
                .thenThrow(new InvalidStatusTransitionException(TicketStatus.OPEN, TicketStatus.RESOLVED));

        mockMvc.perform(patch("/api/tickets/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"RESOLVED\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteTicket_returns204() throws Exception {
        mockMvc.perform(delete("/api/tickets/1"))
                .andExpect(status().isNoContent());
    }
}
