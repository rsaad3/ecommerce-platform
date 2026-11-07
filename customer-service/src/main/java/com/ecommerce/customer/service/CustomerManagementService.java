
package com.ecommerce.customer.service;

import com.ecommerce.customer.dto.*;
import com.ecommerce.customer.entity.Customer;
import com.ecommerce.customer.exception.*;
import com.ecommerce.customer.repository.CustomerRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomerManagementService {

    private static final Logger log =
            LoggerFactory.getLogger(CustomerManagementService.class);

    private final CustomerRepository customerRepository;

    public CustomerManagementService(
            CustomerRepository customerRepository) {

        this.customerRepository = customerRepository;
    }

    // =========================
    // Create Customer
    // =========================

    public CustomerResponse createCustomer(
            CreateCustomerRequest request) {

        log.info("Creating customer with email={}", request.email());

        if (customerRepository.existsByEmail(request.email())) {

            throw new DuplicateEmailException(
                    "Email already registered"
            );
        }

        Customer customer = new Customer();

        customer.setFullName(request.fullName());
        customer.setEmail(request.email());
        customer.setPhone(request.phone());

        Customer savedCustomer =
                customerRepository.save(customer);

        log.info(
                "Customer created successfully. customerId={}",
                savedCustomer.getId()
        );

        return toResponse(savedCustomer);
    }

    // =========================
    // Get Customer By ID
    // =========================

    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long id) {

        log.info("Fetching customer. customerId={}", id);

        Customer customer = findCustomer(id);

        return toResponse(customer);
    }

    // =========================
    // Get All Customers
    // =========================

    @Transactional(readOnly = true)
    public Page<CustomerResponse> getAllCustomers(
            Pageable pageable) {

        log.info(
                "Fetching customers. page={} size={}",
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        return customerRepository
                .findAll(pageable)
                .map(this::toResponse);
    }

    // =========================
    // Update Customer
    // =========================

    public CustomerResponse updateCustomer(
            Long id,
            UpdateCustomerRequest request) {

        log.info("Updating customer. customerId={}", id);

        Customer customer = findCustomer(id);

        boolean emailUsedByAnotherCustomer =
                customerRepository.existsByEmailAndIdNot(
                        request.email(),
                        id
                );

        if (emailUsedByAnotherCustomer) {

            throw new DuplicateEmailException(
                    "Email already registered"
            );
        }

        customer.setFullName(request.fullName());
        customer.setEmail(request.email());
        customer.setPhone(request.phone());

        Customer updatedCustomer =
                customerRepository.save(customer);

        log.info(
                "Customer updated successfully. customerId={}",
                id
        );

        return toResponse(updatedCustomer);
    }

    // =========================
    // Delete Customer
    // =========================

    public void deleteCustomer(Long id) {

        log.info("Deleting customer. customerId={}", id);

        Customer customer = findCustomer(id);

        customerRepository.delete(customer);

        log.info(
                "Customer deleted successfully. customerId={}",
                id
        );
    }

    // =========================
    // Private Helpers
    // =========================

    private Customer findCustomer(Long id) {

        return customerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found with id: " + id
                        )
                );
    }

    private CustomerResponse toResponse(Customer customer) {

        return new CustomerResponse(
                customer.getId(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }
}
