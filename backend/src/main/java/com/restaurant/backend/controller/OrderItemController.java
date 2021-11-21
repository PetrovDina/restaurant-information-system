package com.restaurant.backend.controller;

import com.restaurant.backend.domain.OrderItem;
import com.restaurant.backend.domain.Staff;
import com.restaurant.backend.dto.DataWithMessage;
import com.restaurant.backend.dto.OrderDTO;
import com.restaurant.backend.dto.OrderItemDTO;
import com.restaurant.backend.dto.OrderItemIds;
import com.restaurant.backend.service.OrderItemService;
import com.restaurant.backend.support.OrderItemToOrderItemDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/order-items", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class OrderItemController {
    private final OrderItemService orderItemService;
    private final OrderItemToOrderItemDTO toOrderItemDTO;

    @PreAuthorize("hasAnyRole('BARMAN', 'COOK')")
    @PutMapping("/accept")
    public ResponseEntity<DataWithMessage<List<OrderItemDTO>>> acceptOrderItems(@AuthenticationPrincipal Staff staff,
                                                                               @RequestBody OrderItemIds orderItemIds) {
        DataWithMessage<List<OrderItem>> acceptedItems = orderItemService.acceptOrderItems(staff, orderItemIds.ids);
        return new ResponseEntity<>(new DataWithMessage<>(toOrderItemDTO.convert(acceptedItems.getData()), acceptedItems.getMessage()),
                HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('BARMAN', 'COOK')")
    @PutMapping("/decline")
    public ResponseEntity<DataWithMessage<List<OrderItemDTO>>> declineOrderItems(@AuthenticationPrincipal Staff staff,
                                                                @RequestBody OrderItemIds orderItemIds) {
        DataWithMessage<List<OrderItem>> declinedItems = orderItemService.declineOrderItems(staff, orderItemIds.ids);
        return new ResponseEntity<>(new DataWithMessage<>(toOrderItemDTO.convert(declinedItems.getData()), declinedItems.getMessage()),
                HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('BARMAN', 'COOK')")
    @PutMapping("/mark-prepared")
    public ResponseEntity<DataWithMessage<List<OrderItemDTO>>> markOrderItemsAsPrepared(@AuthenticationPrincipal Staff staff,
                                                                       @RequestBody OrderItemIds orderItemIds) {
        DataWithMessage<List<OrderItem>> preparedItems = orderItemService.prepareOrderItems(staff, orderItemIds.ids);
        return new ResponseEntity<>(new DataWithMessage<>(toOrderItemDTO.convert(preparedItems.getData()), preparedItems.getMessage()),
                HttpStatus.OK);
    }

    @PreAuthorize("hasRole('WAITER')")
    @DeleteMapping("/cancel")
    public ResponseEntity<DataWithMessage<List<Long>>> cancelOrderItem(@RequestBody OrderItemIds orderItemIds) {
        return new ResponseEntity<>(orderItemService.cancelOrderItems(orderItemIds.ids), HttpStatus.OK);
    }
}
