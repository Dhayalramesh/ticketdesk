package com.dhayal.ticketdesk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * TicketDesk: a small ticket-management REST API.
 * <p>
 * Layering: {@code controller -> service -> repository -> database}, the standard
 * Spring Boot separation of concerns. Business rules (valid status transitions,
 * timestamps) live in the service layer so they are testable without a web server
 * or a database.
 */
@SpringBootApplication
public class TicketDeskApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketDeskApplication.class, args);
    }
}
