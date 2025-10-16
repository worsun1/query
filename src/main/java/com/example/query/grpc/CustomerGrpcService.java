package com.example.query.grpc;

import com.example.query.domain.Customer;
import com.example.query.service.CustomerService;
import com.example.query.service.CustomerService.CustomerNotFoundException;
import io.grpc.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

@GrpcService
public class CustomerGrpcService extends CustomerServiceGrpc.CustomerServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(CustomerGrpcService.class);
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private final CustomerService customerService;

    public CustomerGrpcService(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public void getCustomer(GetCustomerRequest request, StreamObserver<GetCustomerResponse> responseObserver) {
        logger.info("Fetching customer with id: {}", request.getId());
        try {
            Customer customer = customerService.getCustomer(request.getId());
            responseObserver.onNext(GetCustomerResponse.newBuilder()
                    .setCustomer(toDto(customer))
                    .build());
            responseObserver.onCompleted();
        } catch (CustomerNotFoundException ex) {
            logger.warn("Customer not found with id: {}", request.getId());
            responseObserver.onError(Status.NOT_FOUND.withDescription(ex.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void searchCustomers(SearchCustomersRequest request, StreamObserver<SearchCustomersResponse> responseObserver) {
        logger.info("Searching customers with name: {} and email: {}", request.getName(), request.getEmail());
        var customers = customerService.searchCustomers(request.getName(), request.getEmail());
        SearchCustomersResponse.Builder builder = SearchCustomersResponse.newBuilder();
        customers.stream().map(this::toDto).forEach(builder::addCustomers);
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    private CustomerDto toDto(Customer customer) {
        String createdAt = customer.getCreatedAt() != null
                ? ISO_FORMATTER.format(customer.getCreatedAt().atOffset(ZoneOffset.UTC))
                : "";
        return CustomerDto.newBuilder()
                .setId(customer.getId())
                .setName(customer.getName())
                .setEmail(customer.getEmail())
                .setCreatedAt(createdAt)
                .build();
    }
}