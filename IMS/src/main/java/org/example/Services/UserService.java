package org.example.Services;


import org.example.DTO.Request.UserRegisterRequest;
import org.example.DTO.Response.PageResponse;
import org.example.DTO.Response.SkuResponse;
import org.example.DTO.Response.UserResponse;
import org.example.Entities.Sku;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Cacheable(value = "users", key = "'page_'+#pageable.pageNumber+'_'+#pageable.pageSize+'_'+#pageable.sort.toString()")
    public PageResponse<UserResponse> getUsers(Pageable pageable) {
        logger.info("Displaying all users");
        Page<Users> page = usersRepository.findAll(pageable);

        Page<UserResponse> mapped = page.map(usersMapper::toResponse);

        return new PageResponse<>(mapped);
    }

    @Cacheable(value = "usersByRole", key = "'page_'+#pageable.pageNumber+'_'+#pageable.pageSize+'_'+#pageable.sort.toString()")
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

    @Cacheable(value = "usersAllByRole", key = "all")
    public List<UserResponse> getAllUsersByRole(String roleName) {
        logger.info("Displaying users by role without pagination ");

        Users.Role role = Users.Role.valueOf(roleName.toUpperCase());
        return usersRepository.findAllByRole(role).stream()
                .map(usersMapper::toResponse)
                .toList();
    }


    @Cacheable(value = "user", key = "#userId")
    public UserResponse getUserById(Long userId) {
        logger.info("Fetching user with userId: {}", userId);
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with userId: {} does not exist", userId);
                    return new ResourceNotFoundException("User not found.");
                });
        logger.info("Successfully fetched user:{} (userId: {})", user.getUsername(), user.getUserId() );
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


    @CachePut(value = "user", key = "#result.userId")
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


    @CachePut(value = "user", key = "#result.userId")
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

    @CacheEvict(value = "user", key = "#userId")
    public void deleteUser(Long userId) {

        logger.info("Attempting to delete user by ID");
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        usersRepository.delete(user);
        logger.info("Successfully deleted user: {} (userId: {})", user.getUsername(), userId);
    }


}
