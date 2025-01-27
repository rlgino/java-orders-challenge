package com.rlgino.OrdersService.application;

import com.rlgino.OrdersService.domain.OrderMother;
import com.rlgino.OrdersService.order.application.OrderService;
import com.rlgino.OrdersService.order.domain.Order;
import com.rlgino.OrdersService.order.domain.OrderID;
import com.rlgino.OrdersService.order.domain.OrderRepository;
import com.rlgino.OrdersService.order.domain.exceptions.DuplicatedOrderException;
import com.rlgino.OrdersService.order.domain.exceptions.OrderNotExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {
    @Autowired
    public OrderRepository orderRepository;
    @Autowired
    public OrderService orderService;

    @BeforeEach
    public void setup() {
        this.orderRepository = Mockito.mock(OrderRepository.class);
        this.orderService = new OrderService(this.orderRepository);
    }

    @Test
    public void findOrder_returnEmpty() {
        final OrderID id = new OrderID(UUID.randomUUID());
        when(orderRepository.findById(id)).thenReturn(Optional.empty());
        Optional<Order> order = this.orderService.findOrderByID(id);
        assertTrue(order.isEmpty());
    }

    @Test
    public void findOrder_returnValidOrder() {
        final Order result = OrderMother.dummy();
        when(orderRepository.findById(result.getId())).thenReturn(Optional.of(result));
        Optional<Order> order = this.orderService.findOrderByID(result.getId());
        assertFalse(order.isEmpty());
        assertEquals(order.get(), result);
    }

    @Test
    public void findOrder_returnAnException() {
        final Order result = OrderMother.dummy();
        when(orderRepository.findById(result.getId())).thenThrow(new RuntimeException("Custom exception"));

        final RuntimeException exception = assertThrows(RuntimeException.class, () -> this.orderService.findOrderByID(result.getId()));
        assertTrue(exception.getMessage().contains("Custom exception"));
    }

    @Test
    public void listOrders_returnListWithTwoOrders() {
        final Order order1 = new Order();
        final Order order2 = new Order();
        Order deletedOrder = new Order();
        deletedOrder = deletedOrder.markAsDelete();
        when(orderRepository.findAll()).thenReturn(Arrays.asList(order1, order2, deletedOrder));

        final List<Order> orders = orderService.listAllOrders();

        assertEquals(2, orders.size());
    }

    @Test
    public void listOrders_returnEmptyList() {
        when(orderRepository.findAll()).thenReturn(List.of());

        final List<Order> orders = orderService.listAllOrders();

        assertTrue(orders.isEmpty());
    }

    @Test
    public void createOrder_shouldSaveOrder() {
        final Order order = OrderMother.dummy();
        when(this.orderRepository.findById(order.getId())).thenReturn(Optional.empty());

        orderService.createOrder(order);

        verify(this.orderRepository, times(1)).save(order);
    }

    @Test
    public void createOrder_orderIdAlreadyExists() {
        final Order order = OrderMother.dummy();
        when(this.orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        DuplicatedOrderException exception = assertThrows(DuplicatedOrderException.class, () -> orderService.createOrder(order));

        assertTrue(exception.getMessage().contains("Duplicated order for ID"));
    }

    @Test
    public void updateOrder_shouldUpdateOrder() {
        final Order order = OrderMother.dummy();
        when(this.orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        final Order updatedOrder = new Order(order.getId());

        orderService.updateOrder(updatedOrder);

        verify(this.orderRepository, times(1)).save(updatedOrder);
    }

    @Test
    public void updateOrder_orderIdNoExists() {
        final Order order = OrderMother.dummy();
        when(this.orderRepository.findById(order.getId())).thenReturn(Optional.empty());

        OrderNotExistsException exception = assertThrows(OrderNotExistsException.class, () -> orderService.updateOrder(order));

        assertTrue(exception.getMessage().contains("Order not found for ID"));
    }

    @Test
    public void deleteOrder_shouldDeleteOrder() {
        final Order order = OrderMother.dummy();
        when(this.orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        final Order updatedOrder = new Order(order.getId());

        orderService.deleteOrder(updatedOrder.getId());

        verify(this.orderRepository, times(1)).save(Mockito.isA(Order.class));
    }

    @Test
    public void deleteOrder_orderIdNoExists() {
        final Order order = OrderMother.dummy();
        when(this.orderRepository.findById(order.getId())).thenReturn(Optional.empty());

        OrderNotExistsException exception = assertThrows(OrderNotExistsException.class, () -> orderService.deleteOrder(order.getId()));

        assertTrue(exception.getMessage().contains("Order not found for ID"));
    }
}
