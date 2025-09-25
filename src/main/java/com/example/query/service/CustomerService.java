package com.example.query.service;

import com.example.query.domain.Customer;
import com.example.query.mapper.CustomerMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerMapper customerMapper;

    public CustomerService(CustomerMapper customerMapper) {
        this.customerMapper = customerMapper;
    }

    public Customer getCustomer(Long id) {
        return customerMapper.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    public List<Customer> searchCustomers(String name, String email) {
        String normalizedName = StringUtils.hasText(name) ? name.trim() : null;
        String normalizedEmail = StringUtils.hasText(email) ? email.trim() : null;
        return customerMapper.searchCustomers(normalizedName, normalizedEmail);
    }

    public static class CustomerNotFoundException extends RuntimeException {
        public CustomerNotFoundException(Long id) {
            super("Customer not found for id=" + id);
        }
    }
}
