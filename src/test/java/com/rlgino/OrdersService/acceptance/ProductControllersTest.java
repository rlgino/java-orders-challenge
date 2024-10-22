package com.rlgino.OrdersService.acceptance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.rlgino.OrdersService.PostgresContainerTest;
import com.rlgino.OrdersService.TeamviewerChallengeApplication;
import com.rlgino.OrdersService.domain.Product;
import com.rlgino.OrdersService.domain.ProductMother;
import com.rlgino.OrdersService.domain.ProductRepository;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TeamviewerChallengeApplication.class)
@WebAppConfiguration
@ContextConfiguration(initializers = {ProductControllersTest.Initializer.class})
public class ProductControllersTest {
    @ClassRule
    public static PostgreSQLContainer postgreSQLContainer = PostgresContainerTest.getInstance();

    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        productRepository.deleteAll();
    }

    @Test
    public void getProduct_ShouldReturnDummyProduct() throws Exception {
        Product product = ProductMother.dummy();
        this.productRepository.save(product);

        MvcResult mvcResult = this.mockMvc.perform(get("/products/" + product.getId()))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();

        String bodyExpected = "{\"id\":\"" + product.getId() + "\",\"name\":\"test\",\"price\":10.00}";
        assertEquals(bodyExpected, mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void getProduct_ReturnNotFound() throws Exception {
        this.mockMvc.perform(get("/products/" + UUID.randomUUID()))
                .andDo(print()).andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void getProduct_InvalidUUID() throws Exception {
        this.mockMvc.perform(get("/products/invalid-uuid"))
                .andDo(print()).andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void getProducts_ShouldReturnList() throws Exception {
        final Product product1 = new Product(UUID.randomUUID(), "Product 1", BigDecimal.ONE);
        final Product product2 = new Product(UUID.randomUUID(), "Product 2", BigDecimal.TWO);
        this.productRepository.save(product1);
        this.productRepository.save(product2);

        MvcResult mvcResult = this.mockMvc.perform(get("/products"))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();

        String bodyExpected = "[{\"id\":\"" + product1.getId() + "\",\"name\":\"Product 1\",\"price\":1.00},{\"id\":\"" + product2.getId() +"\",\"name\":\"Product 2\",\"price\":2.00}]";
        assertEquals(bodyExpected, mvcResult.getResponse().getContentAsString());
    }

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);
    private final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    @Test
    public void postProduct_ShouldSaveProduct() throws Exception {
        final Product product = ProductMother.dummy();
        String bodyRequest  = ow.writeValueAsString(product);

        this.mockMvc.perform(post("/products").contentType(APPLICATION_JSON_UTF8).content(bodyRequest))
                .andDo(print()).andExpect(status().isCreated())
                .andReturn();

        Optional<Product> actualProduct = productRepository.findById(product.getId());
        assertTrue(actualProduct.isPresent());
    }

    @Test
    public void postProduct_ShouldFailBecauseProductExists() throws Exception {
        final Product product = ProductMother.dummy();
        productRepository.save(product);
        String bodyRequest  = ow.writeValueAsString(product);

        this.mockMvc.perform(post("/products").contentType(APPLICATION_JSON_UTF8).content(bodyRequest))
                .andDo(print()).andExpect(status().isConflict())
                .andReturn();
    }

    @Test
    public void putProduct_ShouldUpdateProduct() throws Exception {
        final Product product = ProductMother.dummy();
        productRepository.save(product);
        final Product updatedProduct = new Product(product.getId(), "New name", BigDecimal.TWO);
        String bodyRequest  = ow.writeValueAsString(updatedProduct);

        this.mockMvc.perform(put("/products").contentType(APPLICATION_JSON_UTF8).content(bodyRequest))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();

        Optional<Product> actualProduct = productRepository.findById(product.getId());
        assertTrue(actualProduct.isPresent());
        assertEquals(updatedProduct, actualProduct.get());
    }

    @Test
    public void putProduct_ShouldFailBecauseProductNoExists() throws Exception {
        final Product product = ProductMother.dummy();
        String bodyRequest  = ow.writeValueAsString(product);

        this.mockMvc.perform(put("/products").contentType(APPLICATION_JSON_UTF8).content(bodyRequest))
                .andDo(print()).andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void deleteProduct_ShouldDeleteProduct() throws Exception {
        final Product product = ProductMother.dummy();
        productRepository.save(product);
        final Product updatedProduct = new Product(product.getId(), "New name", BigDecimal.TWO);
        String bodyRequest  = ow.writeValueAsString(updatedProduct);

        this.mockMvc.perform(put("/products").contentType(APPLICATION_JSON_UTF8).content(bodyRequest))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();

        Optional<Product> actualProduct = productRepository.findById(product.getId());
        assertTrue(actualProduct.isPresent());
        assertEquals(updatedProduct, actualProduct.get());
    }

    @Test
    public void deleteProduct_ShouldFailBecauseProductNoExists() throws Exception {
        final Product product = ProductMother.dummy();

        this.mockMvc.perform(delete("/products/" + product.getId()))
                .andDo(print()).andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void deleteProduct_InvalidUUID() throws Exception {
        this.mockMvc.perform(delete("/products/invalid-uuid"))
                .andDo(print()).andExpect(status().isBadRequest())
                .andReturn();
    }
}
