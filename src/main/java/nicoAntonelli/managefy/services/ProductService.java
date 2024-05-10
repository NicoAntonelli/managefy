package nicoAntonelli.managefy.services;

import jakarta.transaction.Transactional;
import nicoAntonelli.managefy.entities.Business;
import nicoAntonelli.managefy.entities.Product;
import nicoAntonelli.managefy.repositories.ProductRepository;
import nicoAntonelli.managefy.utils.Exceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final BusinessService businessService;

    @Autowired
    public ProductService(ProductRepository productRepository, BusinessService businessService) {
        this.productRepository = productRepository;
        this.businessService = businessService;
    }

    public List<Product> GetProducts() {
        return productRepository.findAllActives();
    }

    public Boolean ExistsProduct(Long productID) {
        return productRepository.existsByIdActive(productID);
    }

    public Product GetOneProduct(Long productID) {
        Optional<Product> product = productRepository.findByIdActive(productID);
        if (product.isEmpty()) {
            throw new Exceptions.BadRequestException("Error at 'GetOneProduct' - Product with ID: " + productID + " doesn't exist");
        }

        return product.get();
    }

    public Product CreateProduct(Product product) {
        // Validate associated business
        Business business = product.getBusiness();
        if (business == null || business.getId() == null) {
            throw new Exceptions.BadRequestException("Error at 'CreateProduct' - Business not supplied");
        }
        if (!businessService.ExistsBusiness(business.getId())) {
            throw new Exceptions.BadRequestException("Error at 'CreateProduct' - Business with ID: " + business.getId() + " doesn't exist");
        }

        product.setId(null);
        product.setDeletionDate(null);

        return productRepository.save(product);
    }

    public Product UpdateProduct(Product product) {
        boolean exists = ExistsProduct(product.getId());
        if (!exists) {
            throw new Exceptions.BadRequestException("Error at 'UpdateProduct' - Product with ID: " + product.getId() + " doesn't exist");
        }

        return productRepository.save(product);
    }

    // Logic deletion (field: deletion date)
    public Long DeleteProduct(Long productID) {
        Product product = GetOneProduct(productID);
        product.setDeletionDate(LocalDateTime.now());
        productRepository.save(product);

        return productID;
    }
}
