package com.clientflow.backend.domain.customer;

import com.clientflow.backend.common.response.ApiResponse;
import com.clientflow.backend.common.response.PageResponse;
import com.clientflow.backend.domain.customer.dto.CustomerCreateRequest;
import com.clientflow.backend.domain.customer.dto.CustomerResponse;
import com.clientflow.backend.domain.customer.dto.CustomerStatusUpdateRequest;
import com.clientflow.backend.domain.customer.dto.CustomerUpdateRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/businesses/{businessId}/customers")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerController {

    CustomerService customerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<CustomerResponse> createCustomer(
            @PathVariable Long businessId,
            @Valid @RequestBody CustomerCreateRequest request
    ) {
        return ApiResponse.<CustomerResponse>builder()
                .message("Customer created successfully")
                .result(customerService.createCustomer(businessId, request))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<PageResponse<CustomerResponse>> getCustomers(
            @PathVariable Long businessId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ApiResponse.<PageResponse<CustomerResponse>>builder()
                .message("Get customers successfully")
                .result(customerService.getCustomers(businessId, pageable))
                .build();
    }

    @PutMapping("/{customerId}")
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<CustomerResponse> updateCustomer(
            @PathVariable Long businessId,
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerUpdateRequest request
    ) {
        return ApiResponse.<CustomerResponse>builder()
                .message("Customer updated successfully")
                .result(customerService.updateCustomer(businessId, customerId, request))
                .build();
    }

    @PatchMapping("/{customerId}/status")
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<CustomerResponse> updateCustomerStatus(
            @PathVariable Long businessId,
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerStatusUpdateRequest request
    ) {
        return ApiResponse.<CustomerResponse>builder()
                .message("Customer status updated successfully")
                .result(customerService.updateCustomerStatus(businessId, customerId, request))
                .build();
    }
}
