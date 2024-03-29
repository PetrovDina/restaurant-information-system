package com.restaurant.backend.controller;

import com.restaurant.backend.domain.OrderRecord;
import com.restaurant.backend.domain.Staff;
import com.restaurant.backend.dto.OrderDTO;
import com.restaurant.backend.service.OrderService;
import com.restaurant.backend.support.OrderMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/order", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class OrderController {
	private final OrderService orderService;
	private final OrderMapper orderMapper;

	private final SimpMessagingTemplate messagingTemplate;

	@PreAuthorize("hasAnyRole('BARMAN', 'COOK', 'WAITER')")
	@GetMapping("/all")
	public ResponseEntity<List<OrderDTO>> getAllOrders() {
		return new ResponseEntity<>(orderMapper.convertAll(orderService.findAll()), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('WAITER')")
	@GetMapping("/all/{waiterId}")
	public ResponseEntity<List<OrderDTO>> getAllOrdersForWaiter(@PathVariable("waiterId") Long waiterId) {
		return new ResponseEntity<>(orderMapper.convertAll(orderService.findAllForWaiter(waiterId)), HttpStatus.OK);
	}

	@PreAuthorize("hasAnyRole('BARMAN', 'WAITER')")
	@GetMapping("/table/{tableId}")
	public ResponseEntity<OrderDTO> getForTable(@PathVariable("tableId") Integer tableId) {
		var o = orderService.findByTableId(tableId);
		return o.map(order -> new ResponseEntity<>(orderMapper.convertIncludingPrice(order), HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(null, HttpStatus.OK));
	}

	@PreAuthorize("hasRole('WAITER')")
	@PostMapping("/create")
	public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO order) {
		var data = orderMapper.convertIncludingPrice(orderService.create(order));
		if (data != null)
			messagingTemplate.convertAndSend("/topic/orders", data);
		return new ResponseEntity<>(data, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('BARMAN')")
	@PostMapping("/create-bar")
	public ResponseEntity<OrderDTO> createBarOrder(@RequestBody OrderDTO order) {
		var data = orderMapper.convertIncludingPrice(orderService.createBarOrder(order));
		if (data != null)
			messagingTemplate.convertAndSend("/topic/orders", data);
		return new ResponseEntity<>(data, HttpStatus.OK);
	}

	@PreAuthorize("hasAnyRole('WAITER', 'BARMAN')")
	@PutMapping("/edit")
	public ResponseEntity<OrderDTO> editOrder(@AuthenticationPrincipal Staff staff, @RequestBody OrderDTO order) {
		var data = orderMapper.convertIncludingPrice(orderService.editOrder(staff, order));
		if (data != null)
			messagingTemplate.convertAndSend("/topic/orders", data);
		return new ResponseEntity<>(data, HttpStatus.OK);
	}

	@PreAuthorize("hasAnyRole('WAITER', 'BARMAN')")
	@DeleteMapping("/finalize/{id}")
	public ResponseEntity<List<OrderRecord>> finalizeOrder(@PathVariable("id") Long id) {
		var records = orderService.finalizeOrder(id);
		messagingTemplate.convertAndSend("/topic/finalized-order", id);
		return new ResponseEntity<>(records, HttpStatus.OK);
	}
}
