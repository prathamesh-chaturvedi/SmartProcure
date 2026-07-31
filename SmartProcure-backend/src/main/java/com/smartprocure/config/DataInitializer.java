package com.smartprocure.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.smartprocure.entities.Company;
import com.smartprocure.entities.Designation;
import com.smartprocure.entities.User;
import com.smartprocure.entities.UserRole;
import com.smartprocure.repositories.CompanyRepository;
import com.smartprocure.repositories.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initMasterAdmin(CompanyRepository companyRepository, UserRepository userRepository,
                                      PasswordEncoder passwordEncoder) {

        return args -> {

            if (userRepository.existsByUserRole(UserRole.MASTER_ADMIN)) {
                return;
            }

            // Create Platform Company
            Company company = new Company();
            company.setCompanyName("SmartProcure Platform");
            company.setEmail("support@smartprocure.com");
            company.setAddress("Pune");
            company.setPhone("9999999999");
            company.setActive(true);

            company = companyRepository.save(company);

            // Create Master Admin
            User masterAdmin = new User();

            masterAdmin.setFirstName("Juno");
            masterAdmin.setLastName("Codes");
            masterAdmin.setEmail("masteradmin@gmail.com");
            masterAdmin.setPassword(passwordEncoder.encode("Juno123"));

            masterAdmin.setUserRole(UserRole.MASTER_ADMIN);
            masterAdmin.setDesignation(Designation.SYSTEM_ADMINISTRATOR);
            masterAdmin.setCompany(company);
            masterAdmin.setActive(true);

            userRepository.save(masterAdmin);

            System.out.println("MASTER_ADMIN created successfully.");
        };
    }
}