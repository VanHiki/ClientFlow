package com.clientflow.backend.domain.customer;

import com.clientflow.backend.common.enums.ErrorCode;
import com.clientflow.backend.common.exception.AppException;
import com.clientflow.backend.common.response.PageResponse;
import com.clientflow.backend.common.enums.AppointmentStatus;
import com.clientflow.backend.domain.appointment.AppointmentRepository;
import com.clientflow.backend.domain.appointment.dto.AppointmentResponse;
import com.clientflow.backend.domain.appointment.mapper.AppointmentMapper;
import com.clientflow.backend.domain.business.Business;
import com.clientflow.backend.domain.business.BusinessRepository;
import com.clientflow.backend.domain.customer.dto.CustomerCreateRequest;
import com.clientflow.backend.domain.customer.dto.CustomerResponse;
import com.clientflow.backend.domain.customer.dto.CustomerStatusUpdateRequest;
import com.clientflow.backend.domain.customer.dto.CustomerUpdateRequest;
import com.clientflow.backend.domain.customer.mapper.CustomerMapper;
import com.clientflow.backend.security.SecurityUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerService {

    CustomerRepository customerRepository;
    AppointmentRepository appointmentRepository;
    BusinessRepository businessRepository;
    CustomerMapper customerMapper;
    AppointmentMapper appointmentMapper;
    SecurityUtil securityUtil;

    @Transactional
    public CustomerResponse createCustomer(Long businessId, CustomerCreateRequest request) {
        Business business = getCurrentOwnerBusiness(businessId);
        String phone = request.phone().trim();
        String email = normalizeEmail(request.email());

        if (customerRepository.existsByBusinessIdAndPhone(business.getId(), phone)) {
            throw new AppException(ErrorCode.CUSTOMER_PHONE_ALREADY_EXISTS);
        }

        if (email != null && customerRepository.existsByBusinessIdAndEmailIgnoreCase(business.getId(), email)) {
            throw new AppException(ErrorCode.CUSTOMER_EMAIL_ALREADY_EXISTS);
        }

        Customer customer = customerMapper.toEntity(request);
        customer.setBusiness(business);
        customer.setFullName(request.fullName().trim());
        customer.setPhone(phone);
        customer.setEmail(email);
        customer.setNotes(normalizeNullable(request.notes()));
        customer.setActive(true);

        return customerMapper.toResponse(customerRepository.save(customer));
    }

    @Transactional(readOnly = true)
    public PageResponse<CustomerResponse> getCustomers(
            Long businessId,
            String keyword,
            Boolean active,
            Pageable pageable
    ) {
        Business business = getCurrentOwnerBusiness(businessId);

        return PageResponse.from(
                customerRepository.search(
                                business.getId(),
                                normalizeNullable(keyword),
                                active,
                                pageable
                        )
                        .map(customerMapper::toResponse)
        );
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomer(Long businessId, Long customerId) {
        Business business = getCurrentOwnerBusiness(businessId);

        return customerMapper.toResponse(findCustomer(business.getId(), customerId));
    }

    @Transactional(readOnly = true)
    public PageResponse<AppointmentResponse> getCustomerAppointments(
            Long businessId,
            Long customerId,
            AppointmentStatus status,
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable
    ) {
        Business business = getCurrentOwnerBusiness(businessId);
        Customer customer = findCustomer(business.getId(), customerId);

        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new AppException(ErrorCode.INVALID_DATE_RANGE);
        }

        return PageResponse.from(
                appointmentRepository.search(
                                business.getId(),
                                status,
                                null,
                                customer.getId(),
                                fromDate,
                                toDate,
                                pageable
                        )
                        .map(appointmentMapper::toResponse)
        );
    }

    @Transactional
    public CustomerResponse updateCustomer(Long businessId, Long customerId, CustomerUpdateRequest request) {
        Business business = getCurrentOwnerBusiness(businessId);
        Customer customer = findCustomer(business.getId(), customerId);
        String phone = request.phone().trim();
        String email = normalizeEmail(request.email());

        if (customerRepository.existsByBusinessIdAndPhoneAndIdNot(business.getId(), phone, customer.getId())) {
            throw new AppException(ErrorCode.CUSTOMER_PHONE_ALREADY_EXISTS);
        }

        if (email != null && customerRepository.existsByBusinessIdAndEmailIgnoreCaseAndIdNot(
                business.getId(),
                email,
                customer.getId()
        )) {
            throw new AppException(ErrorCode.CUSTOMER_EMAIL_ALREADY_EXISTS);
        }

        customer.setFullName(request.fullName().trim());
        customer.setPhone(phone);
        customer.setEmail(email);
        customer.setNotes(normalizeNullable(request.notes()));

        return customerMapper.toResponse(customer);
    }

    @Transactional
    public CustomerResponse updateCustomerStatus(
            Long businessId,
            Long customerId,
            CustomerStatusUpdateRequest request
    ) {
        Business business = getCurrentOwnerBusiness(businessId);
        Customer customer = findCustomer(business.getId(), customerId);

        customer.setActive(request.active());

        return customerMapper.toResponse(customer);
    }

    private Business getCurrentOwnerBusiness(Long businessId) {
        Long ownerId = securityUtil.getCurrentUserId();

        return businessRepository.findByIdAndOwnerId(businessId, ownerId)
                .orElseThrow(() -> new AppException(ErrorCode.BUSINESS_NOT_FOUND));
    }

    private Customer findCustomer(Long businessId, Long customerId) {
        return customerRepository.findByIdAndBusinessId(customerId, businessId)
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }

        return email.trim().toLowerCase();
    }

    private String normalizeNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}
