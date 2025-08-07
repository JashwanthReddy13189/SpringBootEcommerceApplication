package mj.ecom.productservice.repository;

import mj.ecom.productservice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByActiveTrue();

    List<Product> findByActiveTrueAndStockQuantityGreaterThanAndNameRegexIgnoreCase(int stockQuantity, String nameRegex);

    Optional<Product> findByIdAndActiveTrue(String id);
}
