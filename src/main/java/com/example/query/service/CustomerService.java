package com.example.query.service;

import com.example.query.domain.Customer;
import com.example.query.mapper.CustomerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);
    
    private final CustomerMapper customerMapper;

    public CustomerService(CustomerMapper customerMapper) {
        this.customerMapper = customerMapper;
    }

    public Customer getCustomer(Long id) {
        log.info("通过ID查询客户信息，客户ID: {}", id);
        Customer customer = customerMapper.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        log.info("查询到客户信息: {}", customer);
        return customer;
    }

    public List<Customer> searchCustomers(String name, String email) {
        log.info("搜索客户信息，姓名: {}, 邮箱: {}", name, email);
        String normalizedName = StringUtils.hasText(name) ? name.trim() : null;
        String normalizedEmail = StringUtils.hasText(email) ? email.trim() : null;
        List<Customer> customers = customerMapper.searchCustomers(normalizedName, normalizedEmail);
        log.info("搜索到{}个客户", customers.size());
        return customers;
    }

    public static class CustomerNotFoundException extends RuntimeException {
        public CustomerNotFoundException(Long id) {
            super("Customer not found for id=" + id);
        }
    }
}