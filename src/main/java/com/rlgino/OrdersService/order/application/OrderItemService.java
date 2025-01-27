package com.rlgino.OrdersService.order.application;

import com.rlgino.OrdersService.order.domain.*;
import com.rlgino.OrdersService.order.domain.exceptions.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;

    public OrderItemService(OrderItemRepository orderItemRepository, OrderRepository orderRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
    }

    public Optional<OrderItem> findOrderItemByID(UUID id) {
        return this.orderItemRepository.findById(id);
    }

    public List<OrderItem> listAllOrderItems() {
        final Iterable<OrderItem> orderItems = this.orderItemRepository.findAll();
        final List<OrderItem> result = new ArrayList<>();
        orderItems.forEach(p -> {
            if (p.getDeleteAt() == null) result.add(p);
        });
        return result;
    }

    public void createOrderItem(OrderItem orderItem) {
        final Optional<OrderItem> existentOrderItem = this.orderItemRepository.findById(orderItem.getId());
        if (existentOrderItem.isPresent()) throw new DuplicatedOrderItemException(orderItem.getId());

        saveOrderItem(orderItem);
    }

    public void updateOrderItem(OrderItem orderItem) {
        final Optional<OrderItem> existentOrderItem = this.orderItemRepository.findById(orderItem.getId());
        if (existentOrderItem.isEmpty()) throw new OrderItemNotExistsException(orderItem.getId());

        saveOrderItem(orderItem);
    }

    private void saveOrderItem(OrderItem orderItem) {
        final Optional<Order> orderOpt = orderRepository.findById(orderItem.getOrderId());
        if (orderOpt.isEmpty()) throw new OrderNotExistsException(orderItem.getOrderId());
        Order order = orderOpt.get();

        orderRepository.save(order);
        orderItemRepository.save(orderItem);
    }

    public void deleteOrderItem(UUID id) {
        final Optional<OrderItem> orderItem = this.orderItemRepository.findById(id);
        if (orderItem.isEmpty()) throw new OrderItemNotExistsException(id);
        // check orderItem.orderItem.status before remove
        final OrderItem existentOrderItem = orderItem.get();
        final OrderItem deletedOrderItem = existentOrderItem.delete();
        this.orderItemRepository.save(deletedOrderItem);

    }
}
