package com.example.query.grpc;

import com.example.query.domain.Customer;
import com.example.query.service.CustomerService;
import com.example.query.service.CustomerService.CustomerNotFoundException;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@GrpcService
public class CustomerGrpcService extends CustomerServiceGrpc.CustomerServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(CustomerGrpcService.class);
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private final CustomerService customerService;

    public CustomerGrpcService(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public void getCustomer(GetCustomerRequest request, StreamObserver<GetCustomerResponse> responseObserver) {
        log.info("接收到获取客户信息的gRPC请求，客户ID: {}", request.getId());
        try {
            Customer customer = customerService.getCustomer(request.getId());
            GetCustomerResponse response = GetCustomerResponse.newBuilder()
                    .setCustomer(toDto(customer))
                    .build();
            log.info("返回客户信息: {}", response.getCustomer());
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (CustomerNotFoundException ex) {
            log.error("未找到客户，客户ID: {}", request.getId(), ex);
            responseObserver.onError(Status.NOT_FOUND.withDescription(ex.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void searchCustomers(SearchCustomersRequest request, StreamObserver<SearchCustomersResponse> responseObserver) {
        log.info("接收到搜索客户的gRPC请求，搜索条件: {}", request);
        var customers = customerService.searchCustomers(request.getName(), request.getEmail());
        SearchCustomersResponse.Builder builder = SearchCustomersResponse.newBuilder();
        customers.stream().map(this::toDto).forEach(builder::addCustomers);
        SearchCustomersResponse response = builder.build();
        log.info("返回搜索结果，共{}个客户", response.getCustomersCount());
        responseObserver.onNext(response);
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