package com.rlgino.OrdersService.application;

import com.rlgino.OrdersService.domain.*;
import com.rlgino.OrdersService.domain.exceptions.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderItemService(OrderItemRepository orderItemRepository, OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
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
        final Optional<Product> productOpt = productRepository.findById(orderItem.getProductId());
        if (productOpt.isEmpty()) throw new ProductNotExistsException(orderItem.getProductId());

        final Optional<Order> orderOpt = orderRepository.findById(orderItem.getOrderId());
        if (orderOpt.isEmpty()) throw new OrderNotExistsException(orderItem.getOrderId());
        Order order = orderOpt.get();
        order = order.calculateAmount();

        if (orderItem.getQuantity() <= 0) throw new InvalidQuantityException();
        orderItem.setProduct(productOpt.get());
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
