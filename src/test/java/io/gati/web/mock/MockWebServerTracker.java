package io.gati.web.mock;

import java.util.function.Consumer;

import org.junit.Assert;

import lombok.Setter;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

public class MockWebServerTracker {
	@Setter
	private MockWebServer server;

	public void prepareResponse(final Consumer<MockResponse> consumer) {
		MockResponse response = new MockResponse();
		consumer.accept(response);
		this.server.enqueue(response);
	}

	public void expectRequest(final Consumer<RecordedRequest> consumer) throws InterruptedException {
		consumer.accept(this.server.takeRequest());
	}

	public void expectRequestCount(final int count) {
		Assert.assertEquals(count, this.server.getRequestCount());
	}
}
