package io.gati.web.doc;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;

import org.springframework.http.MediaType;
import org.springframework.restdocs.headers.RequestHeadersSnippet;
import org.springframework.restdocs.headers.ResponseHeadersSnippet;

import com.google.common.net.HttpHeaders;

import io.gati.web.http.CustomHeaders;

public class HttpHeadersDocumentation {

	public static final RequestHeadersSnippet DEF_REQ_HDR_SNIPPET = requestHeaderSnippet();
	public static final ResponseHeadersSnippet DEF_RES_HDR_SNIPPET = responseHeaderSnippet();

	private static RequestHeadersSnippet requestHeaderSnippet() {
		return requestHeaders(headerWithName(HttpHeaders.ACCEPT).description(MediaType.APPLICATION_JSON),
				headerWithName(CustomHeaders.X_CORRELATION_ID).description("Correlation Id"));
	}

	private static ResponseHeadersSnippet responseHeaderSnippet() {
		return responseHeaders(headerWithName(HttpHeaders.CONTENT_TYPE).description(MediaType.APPLICATION_JSON),
				headerWithName(CustomHeaders.X_CORRELATION_ID).description("Correlation Id"));
	}
}
