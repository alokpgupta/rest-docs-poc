package io.gati.web.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import io.gati.web.model.Order;
import lombok.val;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@IfProfileValue(name="test-groups", values = {"all", "unit-test"})
public class OrderServiceTest {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testFindOrder() {
		val webClient = mock(WebClient.class);
		val requestHeadersUriSpec = Mockito.mock(RequestHeadersUriSpec.class);
		val responseSpec = Mockito.mock(ResponseSpec.class);

		val orderService = new OrderService();
		orderService.setWebClient(webClient);
		val orderId = UUID.randomUUID();

		val expectedOrderMono = Mono.just(new Order().withId(orderId));

		when(webClient.get()).thenReturn(requestHeadersUriSpec);
		when(requestHeadersUriSpec.uri("/order/" + orderId)).thenReturn(requestHeadersUriSpec);
		when(requestHeadersUriSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(requestHeadersUriSpec);
		when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
		when(responseSpec.bodyToMono(Order.class)).thenReturn(expectedOrderMono);

		val actualOrderMono = orderService.findOrder(orderId);

		StepVerifier.create(actualOrderMono).consumeNextWith(order -> Assert.assertEquals(order.getId(), orderId))
				.thenCancel().verify();
		verify(webClient, times(1)).get();
		verify(requestHeadersUriSpec, times(1)).uri("/order/" + orderId);
	}
}
