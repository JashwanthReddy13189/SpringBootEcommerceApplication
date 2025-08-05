package mj.ecom.userservice.service;

import mj.ecom.userservice.dto.AddressDTO;
import mj.ecom.userservice.dto.UserRequest;
import mj.ecom.userservice.dto.UserResponse;
import mj.ecom.userservice.model.Address;
import mj.ecom.userservice.model.User;
import mj.ecom.userservice.model.UserRole;
import mj.ecom.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private KeyCloakAdmin keyCloakAdmin;

    @InjectMocks
    private UserService userService;

    private UserRequest sampleUserRequest;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        // creating a sample user request
        sampleUserRequest = new UserRequest();
        sampleUserRequest.setFirstName("jashwanth");
        sampleUserRequest.setLastName("reddy");
        sampleUserRequest.setPhone("8886093806");
        sampleUserRequest.setRole(UserRole.USER);
        sampleUserRequest.setEmail("jashwanth@gmail.com");
        sampleUserRequest.setUserName("jashwanth");
        sampleUserRequest.setPassword("jashwanth");

        // Address of the user
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setCity("Bhadrachalam");
        addressDTO.setCountry("India");
        addressDTO.setZipcode("507114");
        addressDTO.setStreet("MP Banjara");
        addressDTO.setState("Telangana");

        // creating a sample user
        sampleUser = new User();
        sampleUser.setId("1");
        sampleUser.setUserName(sampleUserRequest.getUserName());
        sampleUser.setFirstName(sampleUserRequest.getFirstName());
        sampleUser.setLastName(sampleUserRequest.getLastName());
        sampleUser.setPhone(sampleUserRequest.getPhone());
        sampleUser.setRole(sampleUserRequest.getRole());
        sampleUser.setEmail(sampleUserRequest.getEmail());

        Address address = new Address();
        address.setCity(addressDTO.getCity());
        address.setCountry(addressDTO.getCountry());
        address.setZipcode(addressDTO.getZipcode());
        address.setStreet(addressDTO.getStreet());
        address.setState(addressDTO.getState());

        sampleUser.setAddress(address);
        sampleUser.setKeycloakId("keycloak-123");
    }

    @Test
    void fetchAllUsers_returnsUserResponses() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(sampleUser));

        List<UserResponse> users = userService.fetchAllUsers();

        assertThat(users).hasSize(1);
        UserResponse userResponse = users.get(0);
        assertThat(userResponse.getId()).isEqualTo("1");
        assertThat(userResponse.getUserName()).isEqualTo("jashwanth");
        assertThat(userResponse.getFirstName()).isEqualTo("jashwanth");
        assertThat(userResponse.getPhone()).isEqualTo("8886093806");
        assertThat(userResponse.getAddress()).isNotNull();
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void addUser_createsUserAndAssignsRole() {
        // Mock token retrieval and user creation in keycloak
        when(keyCloakAdmin.getAdminAccessToken()).thenReturn("fake-token");
        when(keyCloakAdmin.createUser(anyString(), any(UserRequest.class))).thenReturn("keycloak-123");
        doNothing().when(keyCloakAdmin).assignRealmRoleToUser(anyString(), any(UserRole.class), anyString());

        // simulate the user saving
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.addUser(sampleUserRequest);

        // verify keycloak methods were called
        verify(keyCloakAdmin, times(1)).getAdminAccessToken();
        verify(keyCloakAdmin, times(1)).createUser(eq("fake-token"), eq(sampleUserRequest));
        verify(keyCloakAdmin, times(1)).assignRealmRoleToUser(eq(sampleUserRequest.getUserName()), eq(UserRole.USER), eq("keycloak-123"));

        // Verify userRepository.save called with user having keycloakId set
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getKeycloakId()).isEqualTo("keycloak-123");

        assertThat(savedUser.getFirstName()).isEqualTo(sampleUserRequest.getFirstName());
    }

    @Test
    void fetchUser_returnsUserResponseWhenFound() {
        when(userRepository.findById("1")).thenReturn(Optional.of(sampleUser));

        Optional<UserResponse> userResponseOpt = userService.fetchUser("1");

        assertThat(userResponseOpt).isPresent();
        UserResponse userResponse = userResponseOpt.get();
        assertThat(userResponse.getId()).isEqualTo("1");
        assertThat(userResponse.getFirstName()).isEqualTo("jashwanth");
        assertThat(userResponse.getEmail()).isNotNull();
    }

    @Test
    void fetchUser_returnsEmptyOptionalWhenNotFound() {
        when(userRepository.findById("unknown")).thenReturn(Optional.empty());

        Optional<UserResponse> userResponseOpt = userService.fetchUser("unknown");

        assertThat(userResponseOpt).isEmpty();
    }

    @Test
    void updateUser_updatesAndReturnsTrueWhenUserExists() {
        when(userRepository.findById("1")).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserRequest updateRequest = new UserRequest();
        updateRequest.setFirstName("srinivasa");
        updateRequest.setLastName("reddy");
        updateRequest.setEmail("srinivasareddy@gmail.com");
        updateRequest.setPhone("0987654321");

        boolean updated = userService.updateUser("1", updateRequest);

        assertThat(updated).isTrue();

        // The user's first name should be updated to "Jane"
        verify(userRepository).save(argThat(user -> "srinivasa".equals(user.getFirstName())));
    }

    @Test
    void updateUser_returnsFalseWhenUserNotFound() {
        when(userRepository.findById("unknown")).thenReturn(Optional.empty());

        UserRequest updateRequest = new UserRequest();
        boolean updated = userService.updateUser("unknown", updateRequest);

        assertThat(updated).isFalse();
        verify(userRepository, never()).save(any());
    }
}
