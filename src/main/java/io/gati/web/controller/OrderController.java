package io.gati.web.controller;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;

import io.gati.web.http.CustomHeaders;
import io.gati.web.model.ErrorResponse;
import io.gati.web.model.Order;
import io.gati.web.service.IOrderService;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class OrderController {

	@Autowired
	private IOrderService orderService;

	@GetMapping(path = "/order/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<Mono<Order>>> fetchOrder(@PathVariable final UUID id,
			@RequestHeader(name = CustomHeaders.X_CORRELATION_ID, required = false) final Optional<String> correlationId) {
		val orderMono = orderService.findOrder(id);
		val headers = new HttpHeaders();
		correlationId.ifPresent(cid -> headers.set(CustomHeaders.X_CORRELATION_ID, cid));
		val responseEntity = new ResponseEntity<Mono<Order>>(orderMono, headers, HttpStatus.OK);
		return Mono.just(responseEntity);
	}

	@PostMapping("/order")
	public Mono<ResponseEntity<?>> createOrder(@Valid @RequestBody @NotNull final Mono<Order> orderMono) {
		return orderMono.map(order -> ResponseEntity.created(URI.create("/order/" + UUID.randomUUID())).build());
	}

	@ExceptionHandler(WebExchangeBindException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Flux<ErrorResponse> handleBindException(final WebExchangeBindException be) {
		log.error("Binding Error {}", be.getAllErrors());
		return Flux.fromStream(be.getFieldErrors().parallelStream().map(
				fieldErr -> new ErrorResponse(fieldErr.getField(), fieldErr.getCode(), fieldErr.getDefaultMessage())));
	}
}
