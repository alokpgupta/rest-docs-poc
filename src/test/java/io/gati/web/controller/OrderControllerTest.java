package io.gati.web.controller;

import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.gati.web.WebApplication;
import io.gati.web.config.WebTestConfig;
import io.gati.web.doc.ErrorResponseDocumentation;
import io.gati.web.doc.HttpHeadersDocumentation;
import io.gati.web.doc.OrderDocumentation;
import io.gati.web.http.CustomHeaders;
import io.gati.web.mock.MockWebServerTracker;
import io.gati.web.model.ErrorResponse;
import io.gati.web.model.Order;
import io.gati.web.model.OrderFixtureFactory;
import lombok.val;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = WebApplication.class)
@ContextConfiguration(classes = WebTestConfig.class)
@ActiveProfiles("iso-test")
@IfProfileValue(name="test-groups", values = {"all", "iso-test"})
public class OrderControllerTest {

	private WebTestClient webTestClient;

	@Autowired
	private ApplicationContext context;

	@Rule
	public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

	@Autowired
	private MockWebServerTracker mockWebServerTracker;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private OrderDocumentation orderDocumentation;

	@Before
	public void setUp() {
		this.webTestClient = WebTestClient.bindToApplicationContext(this.context).configureClient()
				.filter(documentationConfiguration(this.restDocumentation)).build();
	}

	@Test
	public void testFetchOrder() throws JsonProcessingException, InterruptedException {
		val orderId = UUID.randomUUID();
		val correlationId = UUID.randomUUID().toString();

		Order order = OrderFixtureFactory.createDefaultOrder();
		order.setId(orderId);
		val expectedJson = objectMapper.writeValueAsString(order);

		mockWebServerTracker.prepareResponse(response -> response
				.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).setBody(expectedJson));

		val response = webTestClient.get().uri("/order/" + orderId).accept(MediaType.APPLICATION_JSON)
				.header(CustomHeaders.X_CORRELATION_ID, correlationId).exchange().expectStatus().isOk();

		response.expectHeader().valueEquals(CustomHeaders.X_CORRELATION_ID, correlationId);

		response.expectBody()
				.consumeWith(document("order/fetch/success", HttpHeadersDocumentation.DEF_REQ_HDR_SNIPPET,
						HttpHeadersDocumentation.DEF_RES_HDR_SNIPPET, orderDocumentation.orderResponseSnippet()))
				.json(expectedJson);

		mockWebServerTracker.expectRequestCount(1);
		mockWebServerTracker.expectRequest(request -> {
			Assert.assertEquals("/order/" + orderId, request.getPath());
		});
	}

	@Test
	public void testCreateOrder() {
		Order order = OrderFixtureFactory.createDefaultOrder();

		val response = webTestClient.post().uri("/order").accept(MediaType.APPLICATION_JSON).syncBody(order).exchange()
				.expectStatus().isCreated();
		response.expectHeader().valueMatches(HttpHeaders.LOCATION, "^(/order/).*$");
		response.expectBody().consumeWith(document("order/create/success", orderDocumentation.orderRequestSnippet()));
	}

	@Test
	public void testInvalidBrand() throws JsonProcessingException {
		Order order = OrderFixtureFactory.createDefaultOrder();
		order.setBrand("WER");

		val response = webTestClient.post().uri("/order").accept(MediaType.APPLICATION_JSON).syncBody(order).exchange()
				.expectStatus().isBadRequest();

		val errorResponse = new ErrorResponse("brand", "Size", "Attribute size should be between 2 and 2");
		val expectedJson = objectMapper.writeValueAsString(new ErrorResponse[] { errorResponse });
		response.expectBody().consumeWith(document("order/create/invalidBrand",
				orderDocumentation.orderRequestSnippet(), ErrorResponseDocumentation.ERROR_RES_SNIPPET))
				.json(expectedJson);
	}

	@Test
	public void testInvalidEmailId() throws JsonProcessingException {
		Order order = OrderFixtureFactory.createDefaultOrder();
		order.setEmailId("testgmail.com");

		val response = webTestClient.post().uri("/order").accept(MediaType.APPLICATION_JSON).syncBody(order).exchange()
				.expectStatus().isBadRequest();

		val errorResponse = new ErrorResponse("emailId", "Pattern",
				"must match \"^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}$\"");
		val expectedJson = objectMapper.writeValueAsString(new ErrorResponse[] { errorResponse });
		response.expectBody().consumeWith(document("order/create/invalidEmailId",
				orderDocumentation.orderRequestSnippet(), ErrorResponseDocumentation.ERROR_RES_SNIPPET))
				.json(expectedJson);
	}
}
