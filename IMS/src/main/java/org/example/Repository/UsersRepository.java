package org.example.Repository;

import org.example.Entities.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Users findByEmail(String email);
    Page<Users> findByRole(Users.Role role, Pageable pageable);
    List<Users> findAllByRole(Users.Role role);
}

