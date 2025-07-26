package mj.ecom.userservice.dto;

import lombok.Data;
import mj.ecom.userservice.model.UserRole;

@Data
public class UserResponse {
    private String id;
    private String keycloakId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private UserRole role;
    private AddressDTO address;
}
