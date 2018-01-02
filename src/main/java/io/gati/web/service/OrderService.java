package io.gati.web.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import io.gati.web.model.Order;
import lombok.Setter;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class OrderService implements IOrderService {

	@Autowired
	@Setter
	private WebClient webClient;

	@Override
	public Mono<Order> findOrder(final UUID id) {
		return webClient.get()
				.uri("/order/" + id)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(Order.class)
				.subscribeOn(Schedulers.elastic());
	}

}
