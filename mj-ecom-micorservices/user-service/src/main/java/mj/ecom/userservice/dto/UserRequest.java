package mj.ecom.userservice.dto;

import lombok.Data;
import mj.ecom.userservice.model.UserRole;

@Data
public class UserRequest {
    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private AddressDTO address;
    private UserRole role;
}
