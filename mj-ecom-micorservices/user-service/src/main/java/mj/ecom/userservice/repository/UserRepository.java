package mj.ecom.userservice.repository;

import mj.ecom.userservice.dto.UserResponse;
import mj.ecom.userservice.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    UserResponse getUserDetailsByUserName(String username);
}
