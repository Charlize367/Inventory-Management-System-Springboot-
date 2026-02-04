package Unit;

import org.example.DTO.Request.UserRegisterRequest;
import org.example.DTO.Response.SupplierResponse;
import org.example.DTO.Response.UserResponse;
import org.example.Entities.Users;
import org.example.Mapper.UsersMapper;
import org.example.Repository.UsersRepository;
import org.example.Services.UserService;
import org.hibernate.validator.constraints.Mod10Check;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.example.Entities.Users.Role.STAFF;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UsersRepository userRepository;

    @Mock
    UsersMapper usersMapper;

    @Mock
    private PasswordEncoder passwordEncoder;


    @InjectMocks
    private UserService userService;


    @Test
    void testGetUserById() {
        Users user = new Users(1L, "John Doe", "password", "johndoe@gmail.com", true, STAFF);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setUserId(1L);
        expectedResponse.setUsername("John Doe");
        Mockito.when(usersMapper.toResponse(user)).thenReturn(expectedResponse);

        UserResponse result = userService.getUserById(1L);

        Assertions.assertEquals("John Doe", result.getUsername());
    }

    @Test
    void testRegisterUser() {

        UserRegisterRequest request = new UserRegisterRequest("John Doe", "password", "johndoe@gmail.com", "STAFF");

        Users savedUser = new Users(1L, "John Doe", "password", "johndoe@gmail.com", true, STAFF);
        Mockito.when(userRepository.save(Mockito.any(Users.class))).thenReturn(savedUser);
        Mockito.when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setUserId(1L);
        expectedResponse.setUsername("John Doe");
        Mockito.when(usersMapper.toResponse(savedUser)).thenReturn(expectedResponse);

        UserResponse result = userService.registerUser(request);

        Assertions.assertEquals(1L, result.getUserId());
        Assertions.assertEquals("John Doe", result.getUsername());

        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(Users.class));
    }

    @Test
    void testUpdateUser() {

        Long userId = 1L;
        Users existingUser = new Users(userId, "John Doe", "password", "johndoe@gmail.com", true, STAFF);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        Users updatedUser = new Users(userId, "Johnny Doe", "encodedNewPassword", "johndoe@gmail.com", true, Users.Role.STAFF);
        Mockito.when(userRepository.save(Mockito.any(Users.class))).thenReturn(updatedUser);


        UserRegisterRequest request = new UserRegisterRequest("Johnny Doe", "newPass", "johndoe@gmail.com", "STAFF");

        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setUserId(1L);
        expectedResponse.setUsername("Johnny Doe");
        Mockito.when(usersMapper.toResponse(updatedUser)).thenReturn(expectedResponse);

        UserResponse result = userService.updateUserDetails(userId, request);

        Assertions.assertEquals(userId, result.getUserId());
        Assertions.assertEquals("Johnny Doe", result.getUsername());
    }

    @Test
    void testDeleteUser() {
        Long userId = 1L;
        Users existingUser = new Users(userId, "John Doe", "password", "johndoe@gmail.com", true, STAFF);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        userService.deleteUser(userId);

        Mockito.verify(userRepository).findById(userId);

        Mockito.verify(userRepository).delete(existingUser);


    }
}
