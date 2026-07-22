package com.customers.oriontek.api.controller;

import com.customers.oriontek.application.command.CreateCustomerCommand;
import com.customers.oriontek.application.command.CustomerCommandHandler;
import com.customers.oriontek.application.dto.CustomerResponse;
import com.customers.oriontek.application.query.CustomerQueryHandler;
import com.customers.oriontek.application.query.GetCustomerByIdQuery;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerCommandHandler commandHandler;

    @MockBean
    private CustomerQueryHandler queryHandler;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateCustomer() throws Exception {
        UUID generatedId = UUID.randomUUID();
        given(commandHandler.handle(any(CreateCustomerCommand.class))).willReturn(generatedId);

        CreateCustomerCommand command = new CreateCustomerCommand(
                "Acme Corp",
                "John Doe",
                "john@acme.com",
                "8095550199",
                "130123456"
        );

        mockMvc.perform(post("/api/v1/customers")
                        .header("X-API-Key", "admin-secret-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testListCustomers() throws Exception {
        CustomerResponse customer = new CustomerResponse(
                UUID.randomUUID(),
                "Acme Corp",
                "John Doe",
                "john@acme.com",
                "8095550199",
                "130123456",
                LocalDateTime.now(),
                LocalDateTime.now(),
                List.of()
        );

        given(queryHandler.findAll(any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(customer)));

        mockMvc.perform(get("/api/v1/customers")
                        .header("X-API-Key", "user-secret-key"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
