package com.clientflow.backend.domain.customer;

import com.clientflow.backend.common.enums.ErrorCode;
import com.clientflow.backend.common.exception.AppException;
import com.clientflow.backend.common.response.PageResponse;
import com.clientflow.backend.domain.business.Business;
import com.clientflow.backend.domain.business.BusinessRepository;
import com.clientflow.backend.domain.customer.dto.CustomerCreateRequest;
import com.clientflow.backend.domain.customer.dto.CustomerResponse;
import com.clientflow.backend.domain.customer.mapper.CustomerMapper;
import com.clientflow.backend.security.SecurityUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerService {

    CustomerRepository customerRepository;
    BusinessRepository businessRepository;
    CustomerMapper customerMapper;
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
    public PageResponse<CustomerResponse> getCustomers(Long businessId, Pageable pageable) {
        Business business = getCurrentOwnerBusiness(businessId);

        return PageResponse.from(
                customerRepository.findByBusinessId(business.getId(), pageable)
                        .map(customerMapper::toResponse)
        );
    }

    private Business getCurrentOwnerBusiness(Long businessId) {
        Long ownerId = securityUtil.getCurrentUserId();

        return businessRepository.findByIdAndOwnerId(businessId, ownerId)
                .orElseThrow(() -> new AppException(ErrorCode.BUSINESS_NOT_FOUND));
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
