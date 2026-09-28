package com.dhayal.ticketdesk.controller;

import com.dhayal.ticketdesk.dto.StatusUpdateRequest;
import com.dhayal.ticketdesk.dto.TicketRequest;
import com.dhayal.ticketdesk.dto.TicketResponse;
import com.dhayal.ticketdesk.model.TicketStatus;
import com.dhayal.ticketdesk.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@Tag(name = "Tickets", description = "Ticket management endpoints")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new ticket")
    public TicketResponse create(@Valid @RequestBody TicketRequest request) {
        return TicketResponse.from(ticketService.createTicket(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a ticket by id")
    public TicketResponse getById(@PathVariable Long id) {
        return TicketResponse.from(ticketService.getTicketById(id));
    }

    @GetMapping
    @Operation(summary = "List tickets, optionally filtered by status")
    public Page<TicketResponse> list(
            @RequestParam(required = false) TicketStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ticketService.listTickets(status, pageable).map(TicketResponse::from);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a ticket's details")
    public TicketResponse update(@PathVariable Long id, @Valid @RequestBody TicketRequest request) {
        return TicketResponse.from(ticketService.updateTicket(id, request));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Transition a ticket's status")
    public TicketResponse updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        return TicketResponse.from(ticketService.updateStatus(id, request.status()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a ticket")
    public void delete(@PathVariable Long id) {
        ticketService.deleteTicket(id);
    }
}
