package com.teamviewer.challenge.teamviewer_challenge.acceptance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.teamviewer.challenge.teamviewer_challenge.PostgresContainerTest;
import com.teamviewer.challenge.teamviewer_challenge.TeamviewerChallengeApplication;
import com.teamviewer.challenge.teamviewer_challenge.domain.Order;
import com.teamviewer.challenge.teamviewer_challenge.domain.OrderMother;
import com.teamviewer.challenge.teamviewer_challenge.domain.OrderRepository;
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
@ContextConfiguration(initializers = {OrderControllersTest.Initializer.class})
public class OrderControllersTest {
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
    private OrderRepository orderRepository;
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        orderRepository.deleteAll();
    }

    @Test
    public void getOrder_ShouldReturnDummyOrder() throws Exception {
        Order order = OrderMother.dummy();
        this.orderRepository.save(order);

        MvcResult mvcResult = this.mockMvc.perform(get("/orders/" + order.getId()))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();

        String bodyExpected = "{\"id\":\"" + order.getId() + "\",\"amount\":0.00}";
        assertEquals(bodyExpected, mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void getOrder_ReturnNotFound() throws Exception {
        this.mockMvc.perform(get("/orders/" + UUID.randomUUID()))
                .andDo(print()).andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void getOrder_InvalidUUID() throws Exception {
        this.mockMvc.perform(get("/orders/invalid-uuid"))
                .andDo(print()).andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void getOrders_ShouldReturnList() throws Exception {
        final Order order1 = new Order(UUID.randomUUID());
        final Order order2 = new Order(UUID.randomUUID());
        this.orderRepository.save(order1);
        this.orderRepository.save(order2);

        MvcResult mvcResult = this.mockMvc.perform(get("/orders"))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();

        String bodyExpected = "[{\"id\":\"" + order1.getId() + "\",\"amount\":0.00},{\"id\":\"" + order2.getId() +"\",\"amount\":0.00}]";
        assertEquals(bodyExpected, mvcResult.getResponse().getContentAsString());
    }

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);
    private final ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

    @Test
    public void postOrder_ShouldSaveOrder() throws Exception {
        final Order order = OrderMother.dummy();
        String bodyRequest  = ow.writeValueAsString(order);

        this.mockMvc.perform(post("/orders").contentType(APPLICATION_JSON_UTF8).content(bodyRequest))
                .andDo(print()).andExpect(status().isCreated())
                .andReturn();

        Optional<Order> actualOrder = orderRepository.findById(order.getId());
        assertTrue(actualOrder.isPresent());
    }

    @Test
    public void postOrder_ShouldFailBecauseOrderExists() throws Exception {
        final Order order = OrderMother.dummy();
        orderRepository.save(order);
        String bodyRequest  = ow.writeValueAsString(order);

        this.mockMvc.perform(post("/orders").contentType(APPLICATION_JSON_UTF8).content(bodyRequest))
                .andDo(print()).andExpect(status().isConflict())
                .andReturn();
    }

    @Test
    public void putOrder_ShouldUpdateOrder() throws Exception {
        final Order order = OrderMother.dummy();
        orderRepository.save(order);
        final Order updatedOrder = new Order(order.getId());
        String bodyRequest  = ow.writeValueAsString(updatedOrder);

        this.mockMvc.perform(put("/orders").contentType(APPLICATION_JSON_UTF8).content(bodyRequest))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();

        Optional<Order> actualOrder = orderRepository.findById(order.getId());
        assertTrue(actualOrder.isPresent());
        assertEquals(updatedOrder, actualOrder.get());
    }

    @Test
    public void putOrder_ShouldFailBecauseOrderNoExists() throws Exception {
        final Order order = OrderMother.dummy();
        String bodyRequest  = ow.writeValueAsString(order);

        this.mockMvc.perform(put("/orders").contentType(APPLICATION_JSON_UTF8).content(bodyRequest))
                .andDo(print()).andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void deleteOrder_ShouldDeleteOrder() throws Exception {
        final Order order = OrderMother.dummy();
        orderRepository.save(order);
        final Order updatedOrder = new Order(order.getId());
        String bodyRequest  = ow.writeValueAsString(updatedOrder);

        this.mockMvc.perform(put("/orders").contentType(APPLICATION_JSON_UTF8).content(bodyRequest))
                .andDo(print()).andExpect(status().isOk())
                .andReturn();

        Optional<Order> actualOrder = orderRepository.findById(order.getId());
        assertTrue(actualOrder.isPresent());
        assertEquals(updatedOrder, actualOrder.get());
    }

    @Test
    public void deleteOrder_ShouldFailBecauseOrderNoExists() throws Exception {
        final Order order = OrderMother.dummy();

        this.mockMvc.perform(delete("/orders/" + order.getId()))
                .andDo(print()).andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    public void deleteOrder_InvalidUUID() throws Exception {
        this.mockMvc.perform(delete("/orders/invalid-uuid"))
                .andDo(print()).andExpect(status().isBadRequest())
                .andReturn();
    }
}
