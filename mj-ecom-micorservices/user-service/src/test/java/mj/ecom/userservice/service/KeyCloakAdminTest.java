package mj.ecom.userservice.service;

import mj.ecom.userservice.dto.UserRequest;
import mj.ecom.userservice.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.utils.test.TestUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(properties = {
        "keycloak.admin.username=jashwanth",
        "keycloak.admin.password=jashwanth",
        "keycloak.admin.url=http://localhost:8443",
        "keycloak.admin.realm=mj-ecom",
        "keycloak.admin.client-id=oauth2-pkce",
        "keycloak.admin.client-uid=994b19fb-3436-4442-8cc2-837d994c9412"
})
class KeyCloakAdminTest {

    @InjectMocks
    private KeyCloakAdmin keyCloakAdmin;

    @Mock
    private RestTemplate restTemplate;


    @Test
    void getAdminAccessToken_returnsToken() {
        Map<String, String> tokenResponse = new HashMap<>();
        tokenResponse.put("access_token", "fake-token");

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(tokenResponse, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(responseEntity);

        String token = keyCloakAdmin.getAdminAccessToken();

        assertThat(token).isEqualTo("fake-token");
    }

    @Test
    void createUser_success_returnsUserId() {
        UserRequest userRequest = new UserRequest();
        userRequest.setUserName("john_doe");
        userRequest.setEmail("john@example.com");
        userRequest.setFirstName("John");
        userRequest.setLastName("Doe");
        userRequest.setPassword("pass");

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("http://localhost:8080/admin/realms/testrealm/users/12345"));

        ResponseEntity<String> responseEntity = new ResponseEntity<>("", headers, HttpStatus.CREATED);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        String userId = keyCloakAdmin.createUser("fake-token", userRequest);

        assertThat(userId).isEqualTo("12345");
    }

    @Test
    void createUser_noLocationHeader_throwsException() {
        UserRequest userRequest = new UserRequest();
        userRequest.setUserName("john_doe");
        userRequest.setEmail("john@example.com");
        userRequest.setFirstName("John");
        userRequest.setLastName("Doe");
        userRequest.setPassword("pass");

        HttpHeaders headers = new HttpHeaders();
        // No headers.setLocation()
        ResponseEntity<String> responseEntity = new ResponseEntity<>("{\"info\":\"created but no Location header\"}", headers, HttpStatus.CREATED);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        assertThatThrownBy(() -> keyCloakAdmin.createUser("fake-token", userRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Keycloak did not return Location Header");
    }

    @Test
    void createUser_fails_throwsException() {
        UserRequest userRequest = new UserRequest();

        ResponseEntity<String> responseEntity = new ResponseEntity<>("error", HttpStatus.BAD_REQUEST);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        assertThatThrownBy(() -> keyCloakAdmin.createUser("token", userRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to create user");
    }

    @Test
    void assignRealmRoleToUser_success() {
        Map<String, Object> roleRep = new HashMap<>();
        roleRep.put("id", "role-id");
        roleRep.put("name", "USER");

        ResponseEntity<Map> roleResponse = new ResponseEntity<>(roleRep, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(roleResponse);

        ResponseEntity<Void> postResponse = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Void.class)))
                .thenReturn(postResponse);

        // Mock token generation to be used internally
        KeyCloakAdmin spyAdmin = Mockito.spy(keyCloakAdmin);
        doReturn("fake-token").when(spyAdmin).getAdminAccessToken();

        spyAdmin.assignRealmRoleToUser("john_doe", UserRole.USER, "user-id");

        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class));
        verify(restTemplate, times(1)).postForEntity(anyString(), any(HttpEntity.class), eq(Void.class));
    }

    @Test
    void assignRealmRoleToUser_fails_throwsException() {
        Map<String, Object> roleRep = new HashMap<>();
        roleRep.put("id", "role-id");
        roleRep.put("name", "USER");

        ResponseEntity<Map> roleResponse = new ResponseEntity<>(roleRep, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(roleResponse);

        ResponseEntity<Void> postResponse = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Void.class)))
                .thenReturn(postResponse);

        KeyCloakAdmin spyAdmin = Mockito.spy(keyCloakAdmin);
        doReturn("fake-token").when(spyAdmin).getAdminAccessToken();

        assertThatThrownBy(() -> spyAdmin.assignRealmRoleToUser("john_doe", UserRole.USER, "user-id"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Fialed to assign role");
    }
}
