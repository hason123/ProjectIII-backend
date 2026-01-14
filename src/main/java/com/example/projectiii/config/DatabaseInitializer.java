package com.example.projectiii.config;

import com.example.projectiii.constant.RoleType;
import com.example.projectiii.entity.Role;
import com.example.projectiii.entity.User;
import com.example.projectiii.repository.RoleRepository;
import com.example.projectiii.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatabaseInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public DatabaseInitializer(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            Role admin = new Role(); admin.setRoleName(RoleType.ADMIN);
            Role user = new Role(); user.setRoleName(RoleType.USER);
            Role librarian = new Role(); librarian.setRoleName(RoleType.LIBRARIAN);
            roleRepository.saveAll(List.of(admin, user, librarian));
        }
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUserName("admin");
            admin.setFullName("ADMIN");
            admin.setPassword(new BCryptPasswordEncoder().encode("123456"));
            admin.setPhoneNumber("0123456789");
            admin.setVerified(true);
            admin.setRole(roleRepository.findByRoleName(RoleType.ADMIN));
            User teacher = new User();
            teacher.setUserName("librarian");
            teacher.setFullName("Đỗ Thế Quân");
            teacher.setPassword(new BCryptPasswordEncoder().encode("123456"));
            teacher.setRole(roleRepository.findByRoleName(RoleType.LIBRARIAN));
            teacher.setVerified(true);
            User student = new User();
            student.setUserName("student");
            student.setFullName("Hà Sơn");
            student.setPassword(new BCryptPasswordEncoder().encode("123456"));
            student.setRole(roleRepository.findByRoleName(RoleType.USER));
            student.setVerified(true);
            userRepository.saveAll(List.of(admin, teacher, student));
        }
    }
}
