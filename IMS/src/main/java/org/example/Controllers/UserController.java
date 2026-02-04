package org.example.Controllers;

import org.example.DTO.Request.SupplierRequest;
import org.example.DTO.Request.UserRegisterRequest;
import org.example.DTO.Response.SupplierResponse;
import org.example.DTO.Response.UserResponse;
import org.example.Services.UserService;
import org.example.Entities.Users;
import org.example.Repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
public class UserController {



    @Autowired
    UsersRepository usersRepository;
    private final UserService userService;



    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getUsers(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "5") int size,
                                                       @RequestParam(defaultValue = "userId") String sortBy,
                                                       @RequestParam(defaultValue = "true") boolean descending,
                                                       @RequestParam(required = false) String role) {
        Sort sort = descending ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        if (role != null && !role.isEmpty()) {
            String finalRole = role.toUpperCase();
            return ResponseEntity.ok(userService.getUsersByRole(finalRole, pageable));
        } else {
            return ResponseEntity.ok(userService.getUsers(pageable));
        }


    }

    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>> getAllUsers(@RequestParam(required = false) String role) {

        if (role != null && !role.isEmpty()) {
            String finalRole = role.toUpperCase();
            return ResponseEntity.ok(userService.getAllUsersByRole(finalRole));
        } else {
            return ResponseEntity.ok(userService.getAllUsers());
        }

    }

    @Cacheable("users")
    @GetMapping("{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }


    @PostMapping
    public ResponseEntity<UserResponse> addUser(@Validated @RequestBody UserRegisterRequest request) {
        userService.registerUser(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('MANAGER', 'ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@Validated @PathVariable Long id, @RequestBody UserRegisterRequest request) {
        return ResponseEntity.ok(userService.updateUserDetails(id, request));
    }

    @PreAuthorize("hasRole('MANAGER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

