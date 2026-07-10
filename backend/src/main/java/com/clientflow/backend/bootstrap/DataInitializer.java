package com.clientflow.backend.bootstrap;

import com.clientflow.backend.common.enums.RoleName;
import com.clientflow.backend.domain.role.Role;
import com.clientflow.backend.domain.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.Order;

@Component
@Order(1)
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        seedRoles();
    }

    private void seedRoles() {
        createRoleIfMissing(RoleName.OWNER, "Business owner");
        createRoleIfMissing(RoleName.STAFF, "Business staff");
        createRoleIfMissing(RoleName.CUSTOMER, "Customer");
        createRoleIfMissing(RoleName.ADMIN, "System admin");
    }

    private void createRoleIfMissing(RoleName name, String description) {
        if(roleRepository.findByName(name).isPresent()) {
            return;
        }
        Role role = Role.builder()
                .name(name)
                .description(description)
                .build();

        roleRepository.save(role);
    }
}
