package org.example.Entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Objects;

@Entity
public class Users {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 2, message = "Name must have at least 2 characters")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    private String password;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    private String email;

    private boolean enabled;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role is required")
    private Role role;



    public enum Role {
        ADMIN,
        MANAGER,
        STAFF
    }

    @OneToMany(mappedBy = "salesId", fetch=FetchType.EAGER)
    private List<Sales> sales;

    @OneToMany(mappedBy = "purchaseId", fetch=FetchType.EAGER)
    private List<Purchases> purchases;


    public Users(){}

    public Users(Long userId, String username, String password, String email, boolean enabled, Role role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.enabled = enabled;
        this.role = role;
    }


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Users users = (Users) o;
        return enabled == users.enabled && Objects.equals(userId, users.userId) && Objects.equals(username, users.username) && Objects.equals(password, users.password) && Objects.equals(email, users.email) && role == users.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, password, email, enabled, role);
    }





}
