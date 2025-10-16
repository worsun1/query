package com.example.query.dubbo.impl;

import com.example.query.domain.Customer;
import com.example.query.dubbo.CustomerDubboService;
import com.example.query.dubbo.dto.CustomerDTO;
import com.example.query.service.CustomerService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

@DubboService
public class CustomerDubboServiceImpl implements CustomerDubboService {

  private final CustomerService customerService;

  public CustomerDubboServiceImpl(CustomerService customerService) {
    this.customerService = customerService;
  }

  @Override
  public CustomerDTO getCustomer(Long id) {
    Customer customer = customerService.getCustomer(id);
    return toDTO(customer);
  }

  @Override
  public List<CustomerDTO> searchCustomers(String name, String email) {
    return customerService.searchCustomers(name, email)
        .stream()
        .map(this::toDTO)
        .collect(Collectors.toList());
  }

  private CustomerDTO toDTO(Customer customer) {
    CustomerDTO dto = new CustomerDTO();
    dto.setId(customer.getId());
    dto.setName(customer.getName());
    dto.setEmail(customer.getEmail());
    dto.setCreatedAt(customer.getCreatedAt());
    return dto;
  }
}