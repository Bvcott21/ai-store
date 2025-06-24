package com.bucott.store.config;

import com.bucott.store.model.address.Address;
import com.bucott.store.model.address.AddressType;
import com.bucott.store.model.product.Product;
import com.bucott.store.model.product.ProductCategory;
import com.bucott.store.model.user.Authority;
import com.bucott.store.model.user.Role;
import com.bucott.store.model.user.User;
import com.bucott.store.repository.address.AddressRepository;
import com.bucott.store.repository.product.ProductCategoryRepository;
import com.bucott.store.repository.product.ProductRepository;
import com.bucott.store.repository.user.RoleRepository;
import com.bucott.store.repository.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;
    private final Random random = new Random();


    public DataLoader(UserRepository userRepository, RoleRepository roleRepository,
                     ProductRepository productRepository, ProductCategoryRepository categoryRepository,
                     AddressRepository addressRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.addressRepository = addressRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        try {
            if (userRepository.count() == 0 && productRepository.count() == 0) {
                log.info("Database is empty. Loading test data...");
                
                // Create roles first
                Role userRole = createOrGetRole(Authority.ROLE_USER);
                
                // Create addresses first
                List<Address> addresses = createTestAddresses();
                
                // Create product categories
                List<ProductCategory> categories = createProductCategories();
                
                // Create test users
                createTestUsers(userRole, addresses);
                
                // Create test products
                createTestProducts(categories);
                
                log.info("Test data loaded successfully!");
            } else {
                log.info("Database already contains data. Skipping data loading.");
            }
        } catch (Exception e) {
            log.error("Error loading test data: {}", e.getMessage(), e);
        }
    }

    private Role createOrGetRole(Authority authority) {
        Role existingRole = roleRepository.findByAuthority(authority);
        if (existingRole != null) {
            return existingRole;
        }
        
        Role newRole = new Role(authority);
        return roleRepository.save(newRole);
    }

    private List<Address> createTestAddresses() {
        String[][] addressData = {
            {"123 Main Street", "", "New York", "NY", "10001", "United States"},
            {"456 Oak Avenue", "Apt 2B", "Los Angeles", "CA", "90210", "United States"},
            {"789 Elm Drive", "", "Chicago", "IL", "60601", "United States"},
            {"321 Pine Road", "Suite 5", "Houston", "TX", "77001", "United States"},
            {"654 Maple Lane", "", "Phoenix", "AZ", "85001", "United States"},
            {"987 Cedar Street", "Unit 12", "Philadelphia", "PA", "19101", "United States"},
            {"147 Birch Way", "", "San Antonio", "TX", "78201", "United States"},
            {"258 Spruce Court", "", "San Diego", "CA", "92101", "United States"},
            {"369 Willow Path", "Floor 3", "Dallas", "TX", "75201", "United States"},
            {"741 Poplar Drive", "", "San Jose", "CA", "95101", "United States"}
        };
        
        // Distribute address types evenly across addresses
        AddressType[] addressTypes = {
            AddressType.HOME, AddressType.WORK, AddressType.BILLING, AddressType.SHIPPING,
            AddressType.HOME, AddressType.WORK, AddressType.BILLING, AddressType.SHIPPING,
            AddressType.HOME, AddressType.WORK
        };
        
        List<Address> addresses = new ArrayList<>();
        for (int i = 0; i < addressData.length; i++) {
            String[] data = addressData[i];
            Address address = new Address();
            address.setStreetLine1(data[0]);
            address.setStreetLine2(data[1].isEmpty() ? null : data[1]);
            address.setCity(data[2]);
            address.setState(data[3]);
            address.setZipCode(data[4]);
            address.setCountry(data[5]);
            address.setAddressType(addressTypes[i]);
            addresses.add(address); // Don't save yet - let cascade handle it
        }
        
        log.info("Created {} test addresses with various address types", addresses.size());
        return addresses;
    }

    private List<ProductCategory> createProductCategories() {
        String[] categoryNames = {
            "Electronics", "Clothing", "Books", "Home & Garden", "Sports", 
            "Beauty", "Automotive", "Toys", "Health", "Food"
        };
        
        List<ProductCategory> categories = new ArrayList<>();
        for (String name : categoryNames) {
            ProductCategory category = new ProductCategory(name);
            categories.add(categoryRepository.save(category));
        }
        
        log.info("Created {} product categories", categories.size());
        return categories;
    }

    private void createTestUsers(Role userRole, List<Address> addresses) {
        String[] firstNames = {"John", "Jane", "Mike", "Sarah", "David", "Emma", "Chris", "Lisa", "Tom", "Anna"};
        String[] lastNames = {"Smith", "Johnson", "Brown", "Davis", "Wilson", "Miller", "Moore", "Taylor", "Anderson", "Thomas"};
        
        for (int i = 0; i < 10; i++) {
            String firstName = firstNames[i];
            String lastName = lastNames[i];
            String username = firstName.toLowerCase() + lastName.toLowerCase();
            String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@example.com";
            
            // Generate phone number that meets @Size(min=10, max=15) and regex pattern
            String phoneNumber = "+1" + String.format("%09d", 555000000 + random.nextInt(999999)); // Results in 12 chars total
            
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPhoneNumber(phoneNumber);
            user.setPassword(passwordEncoder.encode("password123")); // Now encoded password can be up to 100 chars
            user.setEnabled(true);
            user.setExpired(false);
            user.setLocked(false);
            user.setAddress(addresses.get(i)); // Assign corresponding address
            user.getRoles().add(userRole);
            
            userRepository.save(user);
        }
        
        log.info("Created 10 test users with addresses and phone numbers");
    }

    private void createTestProducts(List<ProductCategory> categories) {
        String[] productNames = {
            "iPhone 15 Pro Max", "Samsung Galaxy S24 Ultra", "MacBook Air M3 15-inch", "Dell XPS 13 Plus", "Sony WH-1000XM5 Headphones",
            "Nike Air Max 90 Sneakers", "Adidas Ultraboost 22 Running", "Levi's 501 Original Jeans", "North Face Winter Jacket", "Ray-Ban Classic Sunglasses",
            "The Great Gatsby Novel", "To Kill a Mockingbird Book", "George Orwell 1984 Classic", "Pride and Prejudice Edition", "Catcher in the Rye Book",
            "Premium Coffee Machine", "High-Speed Blender Pro", "Digital Air Fryer XL", "Robot Vacuum Cleaner", "Smart TV 55-inch 4K",
            "Premium Yoga Mat", "Adjustable Dumbbells Set", "Professional Tennis Racket", "Official Basketball Ball", "Smart Fitness Tracker",
            "Anti-Aging Moisturizer", "Organic Shampoo Bottle", "Designer Perfume Spray", "Matte Lipstick Collection", "Gentle Face Wash Gel",
            "Universal Car Phone Mount", "Synthetic Engine Oil 5W30", "Digital Tire Pressure Gauge", "Fast Car Charger USB-C", "HD Dashboard Camera",
            "LEGO Creator Expert Set", "Barbie Dreamhouse Doll", "Marvel Action Figure Set", "Strategy Board Game Classic", "Jigsaw Puzzle 1000 Pieces",
            "Vitamin D3 Supplements", "Whey Protein Powder Vanilla", "Daily Multivitamin Tablets", "Omega-3 Fish Oil Capsules", "Probiotic Health Capsules",
            "Raw Organic Honey Jar", "Premium Green Tea Bags", "Dark Chocolate Bar 70%", "Extra Virgin Olive Oil", "Organic Quinoa Grain"
        };

        String[] descriptions = {
            "High-quality product with excellent features and outstanding performance for daily use",
            "Premium quality item with modern design and cutting-edge technology for professionals",
            "Best-selling product with great customer reviews and proven reliability over time",
            "Professional grade equipment designed for durability and consistent performance",
            "Eco-friendly and sustainable option that reduces environmental impact significantly",
            "Latest technology with advanced features and intuitive user interface design",
            "Comfortable and durable design perfect for long-term use and maximum satisfaction",
            "Classic style with timeless appeal that never goes out of fashion or trends",
            "Innovative solution for everyday needs with smart features and easy operation",
            "Top-rated product by customers worldwide with exceptional quality and value proposition"
        };

        for (int i = 0; i < 50; i++) {
            String name = productNames[i];
            String description = descriptions[random.nextInt(descriptions.length)];
            
            // Random price between $10 and $500
            BigDecimal price = BigDecimal.valueOf(10 + random.nextDouble() * 490).setScale(2, java.math.RoundingMode.HALF_UP);
            
            // Cost is 60-80% of price
            BigDecimal cost = price.multiply(BigDecimal.valueOf(0.6 + random.nextDouble() * 0.2)).setScale(2, java.math.RoundingMode.HALF_UP);
            
            // Random stock between 5 and 100
            int stock = 5 + random.nextInt(96);
            
            Product product = new Product(name, description, price, cost, stock);
            
            // Assign 1-3 random categories
            int numCategories = 1 + random.nextInt(3);
            for (int j = 0; j < numCategories; j++) {
                ProductCategory category = categories.get(random.nextInt(categories.size()));
                product.getCategories().add(category);
            }
            
            productRepository.save(product);
        }
        
        log.info("Created 50 test products");
    }
}