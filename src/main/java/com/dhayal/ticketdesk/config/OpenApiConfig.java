package com.dhayal.ticketdesk.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ticketDeskOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("TicketDesk API")
                .description("A REST API for creating, tracking, and resolving support tickets.")
                .version("v1.0")
                .contact(new Contact().name("Dhayal R").email("dhayal1107@gmail.com")));
    }
}
