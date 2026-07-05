package com.clientflow.backend.domain.business;

import com.clientflow.backend.common.response.ApiResponse;
import com.clientflow.backend.domain.business.dto.BusinessCreateRequest;
import com.clientflow.backend.domain.business.dto.BusinessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import com.clientflow.backend.common.response.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/businesses")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<BusinessResponse> createBusiness(@Valid @RequestBody BusinessCreateRequest request) {
        return ApiResponse.<BusinessResponse>builder()
                .message("Business created successfully")
                .result(businessService.createBusiness(request))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<PageResponse<BusinessResponse>> getMyBusinesses(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ApiResponse.<PageResponse<BusinessResponse>>builder()
                .message("Get businesses successfully")
                .result(businessService.getMyBusinesses(pageable))
                .build();
    }

    @GetMapping("/{businessId}")
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<BusinessResponse> getMyBusiness(@PathVariable Long businessId) {
        return ApiResponse.<BusinessResponse>builder()
                .message("Get business successfully")
                .result(businessService.getMyBusiness(businessId))
                .build();
    }
}
