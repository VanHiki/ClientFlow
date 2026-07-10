package com.clientflow.backend.domain.businessexception;

import com.clientflow.backend.common.response.ApiResponse;
import com.clientflow.backend.common.response.PageResponse;
import com.clientflow.backend.domain.businessexception.dto.BusinessExceptionCreateRequest;
import com.clientflow.backend.domain.businessexception.dto.BusinessExceptionResponse;
import com.clientflow.backend.domain.businessexception.dto.BusinessExceptionUpdateRequest;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/businesses/{businessId}/exceptions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BusinessExceptionDayController {

    BusinessExceptionDayService businessExceptionDayService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<BusinessExceptionResponse> createException(
            @PathVariable Long businessId,
            @Valid @RequestBody BusinessExceptionCreateRequest request
    ) {
        return ApiResponse.<BusinessExceptionResponse>builder()
                .message("Business exception created successfully")
                .result(businessExceptionDayService.createException(businessId, request))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<PageResponse<BusinessExceptionResponse>> getExceptions(
            @PathVariable Long businessId,
            @PageableDefault(size = 10, sort = "date", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ApiResponse.<PageResponse<BusinessExceptionResponse>>builder()
                .message("Get business exceptions successfully")
                .result(businessExceptionDayService.getExceptions(businessId, pageable))
                .build();
    }

    @PutMapping("/{exceptionId}")
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<BusinessExceptionResponse> updateException(
            @PathVariable Long businessId,
            @PathVariable Long exceptionId,
            @Valid @RequestBody BusinessExceptionUpdateRequest request
    ) {
        return ApiResponse.<BusinessExceptionResponse>builder()
                .message("Business exception updated successfully")
                .result(businessExceptionDayService.updateException(businessId, exceptionId, request))
                .build();
    }

    @DeleteMapping("/{exceptionId}")
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<Void> deleteException(
            @PathVariable Long businessId,
            @PathVariable Long exceptionId
    ) {
        businessExceptionDayService.deleteException(businessId, exceptionId);

        return ApiResponse.<Void>builder()
                .message("Business exception deleted successfully")
                .build();
    }
}
