package com.teamviewer.challenge.teamviewer_challenge.controllers;

import com.teamviewer.challenge.teamviewer_challenge.application.OrderService;
import com.teamviewer.challenge.teamviewer_challenge.domain.Order;
import com.teamviewer.challenge.teamviewer_challenge.domain.exceptions.DuplicatedOrderException;
import com.teamviewer.challenge.teamviewer_challenge.domain.exceptions.OrderNotExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    public OrderService orderService;

    @GetMapping("/{id}")
    public ResponseEntity findOrder(@PathVariable String id) {
        UUID parsedID;
        try {
            parsedID = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid UUID: " + id, HttpStatus.BAD_REQUEST);
        }
        Optional<Order> orderOpt = this.orderService.findOrderByID(parsedID);
        if (orderOpt.isEmpty())
            return new ResponseEntity<>(String.format("Order with ID %s not found", id), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(orderOpt.get(), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity saveOrder(@RequestBody Order order) {
        try {
            this.orderService.createOrder(order);
        } catch (DuplicatedOrderException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping()
    public ResponseEntity updateOrder(@RequestBody Order order) {
        try {
            this.orderService.updateOrder(order);
        } catch (OrderNotExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteOrder(@PathVariable String id) {
        try {
            final UUID parsedID = UUID.fromString(id);
            this.orderService.deleteOrder(parsedID);
        }catch (OrderNotExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid UUID: " + id, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<Order>> listOrders() {
        List<Order> allOrders = this.orderService.listAllOrders();
        return new ResponseEntity<>(allOrders, HttpStatus.OK);
    }
}

class OrderDTO {

}

