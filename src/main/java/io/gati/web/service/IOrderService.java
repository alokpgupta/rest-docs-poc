package io.gati.web.service;

import java.util.UUID;

import io.gati.web.model.Order;
import reactor.core.publisher.Mono;

public interface IOrderService {
	Mono<Order> findOrder(UUID id);
}
