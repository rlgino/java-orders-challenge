package com.rlgino.OrdersService.controllers;

import com.rlgino.OrdersService.application.OrderItemService;
import com.rlgino.OrdersService.domain.OrderItem;
import com.rlgino.OrdersService.domain.exceptions.DuplicatedOrderItemException;
import com.rlgino.OrdersService.domain.exceptions.OrderItemNotExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/order-item")
public class OrderItemController {
    @Autowired
    public OrderItemService orderItemService;

    @GetMapping("/{id}")
    public ResponseEntity findOrderItem(@PathVariable String id) {
        UUID parsedID;
        try {
            parsedID = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid UUID: " + id, HttpStatus.BAD_REQUEST);
        }
        Optional<OrderItem> orderItemOpt = this.orderItemService.findOrderItemByID(parsedID);
        if (orderItemOpt.isEmpty())
            return new ResponseEntity<>(String.format("OrderItem with ID %s not found", id), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(orderItemOpt.get(), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity saveOrderItem(@RequestBody OrderItem orderItem) {
        try {
            this.orderItemService.createOrderItem(orderItem);
        } catch (DuplicatedOrderItemException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping()
    public ResponseEntity updateOrderItem(@RequestBody OrderItem orderItem) {
        try {
            this.orderItemService.updateOrderItem(orderItem);
        } catch (OrderItemNotExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteOrderItem(@PathVariable String id) {
        try {
            final UUID parsedID = UUID.fromString(id);
            this.orderItemService.deleteOrderItem(parsedID);
        }catch (OrderItemNotExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid UUID: " + id, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<OrderItem>> listOrderItems() {
        List<OrderItem> allOrderItems = this.orderItemService.listAllOrderItems();
        return new ResponseEntity<>(allOrderItems, HttpStatus.OK);
    }
}

