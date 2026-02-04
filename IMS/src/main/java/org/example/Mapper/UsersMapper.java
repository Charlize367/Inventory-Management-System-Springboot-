package org.example.Mapper;

import org.example.DTO.Request.UserRegisterRequest;
import org.example.DTO.Response.UserResponse;
import org.example.Entities.Users;
import org.springframework.stereotype.Component;

@Component
public class UsersMapper {

    public Users toEntity(UserRegisterRequest req) {
        Users user = new Users();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(req.getPassword());
        user.setRole(Users.Role.valueOf(req.getRole())); // assumes valid input
        return user;
    }

    public UserResponse toResponse(Users user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setPassword(user.getPassword());
        response.setRole(user.getRole().name());
        return response;
    }
}
