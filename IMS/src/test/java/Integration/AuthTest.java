package Integration;

import com.jayway.jsonpath.JsonPath;
import org.example.Application;
import org.example.Entities.Users;
import org.example.Repository.UsersRepository;
import org.example.Security.CustomUserDetails;
import org.example.Security.TokenService;
import org.example.Services.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class AuthTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsersRepository usersRepository;

    @MockitoBean
    private UserService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @Test
    void shouldAuthenticateAndReturnToken() throws Exception {

        Users userEntity = new Users(1L, "User",passwordEncoder.encode("password123"), "user@example.com",
                true, Users.Role.ADMIN);

        CustomUserDetails userDetails = new CustomUserDetails(userEntity);

        Mockito.when(userDetailsService.loadUserByUsername("user@example.com"))
                .thenReturn(userDetails);

        Mockito.when(usersRepository.findByEmail("user@example.com"))
                .thenReturn(userEntity);

        String loginRequest = """
        {
            "email": "user@example.com",
            "password": "password123"
        }
        """;

        Mockito.when(authenticationManager.authenticate(Mockito.any()))
                .thenAnswer(invocation -> {
                    return new UsernamePasswordAuthenticationToken(
                            userDetails,
                            "password123",
                            userDetails.getAuthorities()
                    );
                });

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();


    }


    @Test
    void testProtectedEndpoint() throws Exception {
        Users userEntity = new Users(1L, "User",passwordEncoder.encode("password123"), "user@example.com",
                true, Users.Role.ADMIN);

        CustomUserDetails userDetails = new CustomUserDetails(userEntity);

        Mockito.when(userDetailsService.loadUserByUsername("user@example.com"))
                .thenReturn(userDetails);

        Mockito.when(usersRepository.findByEmail("user@example.com"))
                .thenReturn(userEntity);

        String loginRequest = """
        {
            "email": "user@example.com",
            "password": "password123"
        }
        """;

        Mockito.when(authenticationManager.authenticate(Mockito.any()))
                .thenAnswer(invocation -> {
                    return new UsernamePasswordAuthenticationToken(
                            userDetails,
                            "password123",
                            userDetails.getAuthorities()
                    );
                });

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        String token = JsonPath.read(responseJson, "$.token");


        mockMvc.perform(get("/api/v1/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
