package org.example.Services;


import org.example.DTO.Request.UserRegisterRequest;
import org.example.DTO.Response.UserResponse;
import org.example.Entities.Purchases;
import org.example.Entities.Suppliers;
import org.example.Entities.Users;
import org.example.Exception.ResourceAlreadyExistsException;
import org.example.Exception.ResourceNotFoundException;
import org.example.Mapper.UsersMapper;
import org.example.Repository.UsersRepository;
import org.example.Security.CustomUserDetails;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UsersRepository usersRepository;
    private final UsersMapper usersMapper;


    private final PasswordEncoder passwordEncoder;

    public UserService(UsersRepository usersRepository, PasswordEncoder passwordEncoder, UsersMapper usersMapper) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.usersMapper = usersMapper;

    }
    public Page<UserResponse> getUsers(Pageable pageable) {
        logger.info("Displaying all users");
        return usersRepository.findAll(pageable)
                .map(usersMapper::toResponse);
    }

    public Page<UserResponse> getUsersByRole(String roleName, Pageable pageable) {
        logger.info("Displaying all staff");

        Users.Role role = Users.Role.valueOf(roleName.toUpperCase());
        return usersRepository.findByRole(role, pageable)
                .map(usersMapper::toResponse);
    }


    public List<UserResponse> getAllUsers() {
        logger.info("Displaying all users without pagination");
        return usersRepository.findAll().stream()
                .map(usersMapper::toResponse)
                .toList();
    }

    public List<UserResponse> getAllUsersByRole(String roleName) {
        logger.info("Displaying users by role without pagination ");

        Users.Role role = Users.Role.valueOf(roleName.toUpperCase());
        return usersRepository.findAllByRole(role).stream()
                .map(usersMapper::toResponse)
                .toList();
    }


    public UserResponse getUserById(Long id) {
        logger.info("Fetching user with id: {}", id);
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("User with id: {} does not exist", id);
                    return new ResourceNotFoundException("User not found.");
                });
        logger.info("Successfully fetched user:{} (id: {})", user.getUsername(), user.getUserId() );
        return usersMapper.toResponse(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        logger.info("Fetching user with email: {}", email);
        Users user = usersRepository.findByEmail(email);

        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }


        logger.info("Successfully fetched user:{} (email: {})", user.getUsername(), email );
        return new CustomUserDetails(user);
    }

    public UserResponse registerUser(UserRegisterRequest request) {

        logger.info("Attempting to add new user with email: {}", request.getEmail());

        Users user = usersRepository.findByEmail(request.getEmail());

        if (user != null) {
            logger.warn("Supplier already exists with email: {}", request.getEmail());
            throw new ResourceAlreadyExistsException("Supplier already exists.");
        }

        String safeName = Jsoup.clean(request.getUsername(), Safelist.none()).trim();
        String safeEmail = Jsoup.clean(request.getEmail(), Safelist.none()).trim();
        Users newUser = new Users();
        newUser.setUsername(safeName);
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        newUser.setPassword(encodedPassword);
        newUser.setEmail(safeEmail);
        newUser.setRole(Users.Role.valueOf(request.getRole()));
        newUser.setEnabled(true);

        Users savedUser = usersRepository.save(newUser);

        logger.info("Successfully added new user: {} ({})", safeName, safeEmail);

        return usersMapper.toResponse(savedUser);
    }


    public UserResponse updateUserDetails(Long id, UserRegisterRequest request) {
        logger.info("Updating user id: {} ", id);
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("User not found with id: {}", id);
                    return new ResourceNotFoundException("User not found");
                });
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setRole(Users.Role.valueOf(request.getRole()));
        user.setEnabled(true);
        Users updatedUser = usersRepository.save(user);

        logger.info("Successfully updated user id: {} ", id);
        return usersMapper.toResponse(updatedUser);
    }

    public void deleteUser(Long id) {

        logger.info("Attempting to delete user by ID");
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        usersRepository.delete(user);
        logger.info("Successfully deleted user: {} (id: {})", user.getUsername(), id);
    }


}
