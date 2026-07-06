package com.clientflow.backend.domain.staffservice;


import com.clientflow.backend.common.enums.ErrorCode;
import com.clientflow.backend.common.exception.AppException;
import com.clientflow.backend.common.response.PageResponse;
import com.clientflow.backend.domain.business.Business;
import com.clientflow.backend.domain.business.BusinessRepository;
import com.clientflow.backend.domain.service.ServiceOffering;
import com.clientflow.backend.domain.service.ServiceOfferingRepository;
import com.clientflow.backend.domain.staff.StaffProfile;
import com.clientflow.backend.domain.staff.StaffProfileRepository;
import com.clientflow.backend.domain.staffservice.dto.StaffServiceResponse;
import com.clientflow.backend.domain.staffservice.mapper.StaffServiceAssignmentMapper;
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
public class StaffServiceAssignmentService {
    BusinessRepository businessRepository;
    StaffServiceAssignmentRepository staffServiceAssignmentRepository;
    StaffServiceAssignmentMapper  staffServiceAssignmentMapper;
    StaffProfileRepository  staffProfileRepository;
    ServiceOfferingRepository serviceOfferingRepository;
    SecurityUtil securityUtil;


    @Transactional
    public StaffServiceResponse assignService(Long businessId, Long staffId, Long serviceId){
        Business business = getCurrentOwnerBusiness(businessId);
        StaffProfile staffProfile = getStaffProfile(business.getId(), staffId);
        ServiceOffering serviceOffering = getServiceOffering(business.getId(), serviceId);

        if (staffServiceAssignmentRepository.existsByStaffProfileIdAndServiceOfferingId(staffId, serviceId)) {
            throw new AppException(ErrorCode.STAFF_SERVICE_ALREADY_ASSIGNED);
        }

        StaffServiceAssignment assignment = StaffServiceAssignment.builder()
                .staffProfile(staffProfile)
                .serviceOffering(serviceOffering)
                .build();

        return staffServiceAssignmentMapper.toResponse(staffServiceAssignmentRepository.save(assignment));
    }

    @Transactional(readOnly = true)
    public PageResponse<StaffServiceResponse> getStaffServices(Long businessId, Long staffId, Pageable pageable) {
        Business business = getCurrentOwnerBusiness(businessId);
        StaffProfile staffProfile = getStaffProfile(business.getId(), staffId);

        return PageResponse.from(
                staffServiceAssignmentRepository.findByStaffProfileId(staffProfile.getId(), pageable)
                        .map(staffServiceAssignmentMapper::toResponse)
        );
    }

    private Business getCurrentOwnerBusiness(Long businessId) {
        Long ownerId = securityUtil.getCurrentUserId();

        return businessRepository.findByIdAndOwnerId(businessId, ownerId)
                .orElseThrow(() -> new AppException(ErrorCode.BUSINESS_NOT_FOUND));
    }

    private StaffProfile getStaffProfile(Long businessId, Long staffId) {
        return staffProfileRepository.findByIdAndBusinessId(staffId, businessId)
                .orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));
    }

    private ServiceOffering getServiceOffering(Long businessId, Long serviceId) {
        return serviceOfferingRepository.findByIdAndBusinessId(serviceId, businessId)
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));
    }
}
