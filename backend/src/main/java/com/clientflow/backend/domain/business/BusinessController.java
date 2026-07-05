package com.clientflow.backend.domain.business;

import com.clientflow.backend.common.response.ApiResponse;
import com.clientflow.backend.domain.business.dto.BusinessCreateRequest;
import com.clientflow.backend.domain.business.dto.BusinessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<BusinessResponse> getMyBusiness() {
        return ApiResponse.<BusinessResponse>builder()
                .message("Get business successfully")
                .result(businessService.getMyBusiness())
                .build();
    }
}
