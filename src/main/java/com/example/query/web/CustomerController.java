package com.example.query.web;

import com.example.query.domain.Customer;
import com.example.query.dto.CustomerResponse;
import com.example.query.dto.CustomerSearchRequest;
import com.example.query.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/customers")
@Validated
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable("id") Long id) {
        Customer customer = customerService.getCustomer(id);
        return ResponseEntity.ok(toResponse(customer));
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> searchCustomers(@Valid CustomerSearchRequest request) {
        List<CustomerResponse> responses = customerService.searchCustomers(request.getName(), request.getEmail())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(customer.getId(), customer.getName(), customer.getEmail(), customer.getCreatedAt());
    }
}
