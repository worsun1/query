package com.example.query.web;

import com.example.query.domain.Customer;
import com.example.query.dto.CustomerResponse;
import com.example.query.dto.CustomerSearchRequest;
import com.example.query.service.CustomerService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);
    
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable("id") Long id) {
        log.info("接收到获取客户信息的HTTP请求，客户ID: {}", id);
        Customer customer = customerService.getCustomer(id);
        CustomerResponse response = toResponse(customer);
        log.info("返回客户信息: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> searchCustomers(@Valid CustomerSearchRequest request) {
        log.info("接收到搜索客户的HTTP请求，搜索条件: {}", request);
        List<CustomerResponse> responses = customerService.searchCustomers(request.getName(), request.getEmail())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        log.info("返回搜索结果，共{}个客户", responses.size());
        return ResponseEntity.ok(responses);
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(customer.getId(), customer.getName(), customer.getEmail(), customer.getCreatedAt());
    }
}