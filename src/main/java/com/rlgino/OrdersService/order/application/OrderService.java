package com.rlgino.OrdersService.order.application;

import com.rlgino.OrdersService.order.domain.Order;
import com.rlgino.OrdersService.order.domain.OrderID;
import com.rlgino.OrdersService.order.domain.OrderRepository;
import com.rlgino.OrdersService.order.domain.exceptions.DuplicatedOrderException;
import com.rlgino.OrdersService.order.domain.exceptions.OrderNotExistsException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Optional<Order> findOrderByID(OrderID id) {
        return this.orderRepository.findById(id);
    }

    public List<Order> listAllOrders() {
        final Iterable<Order> orders = this.orderRepository.findAll();
        final List<Order> result = new ArrayList<>();
        orders.forEach(p -> {
            if (p.getDeleteAt() == null) result.add(p);
        });
        return result;
    }

    public void createOrder(Order order) {
        final Optional<Order> existentOrder = this.orderRepository.findById(order.getId());
        if (existentOrder.isPresent()) throw new DuplicatedOrderException(order.getId());

        // We suspect that can contain product Items
        //final Order updatedOrder = order.calculateAmount();
        this.orderRepository.save(order);
    }

    public void updateOrder(Order order) {
        final Optional<Order> existentOrder = this.orderRepository.findById(order.getId());
        if (existentOrder.isEmpty()) throw new OrderNotExistsException(order.getId());
        // We suspect that can contain product Items
//        final Order updatedOrder = order.calculateAmount();
        this.orderRepository.save(order);
    }

    public void deleteOrder(OrderID id) {
        final Optional<Order> order = this.orderRepository.findById(id);
        if (order.isEmpty()) throw new OrderNotExistsException(id);
        final Order existentOrder = order.get();
        final Order deletedOrder = existentOrder.markAsDelete();
        this.orderRepository.save(deletedOrder);
    }
}
