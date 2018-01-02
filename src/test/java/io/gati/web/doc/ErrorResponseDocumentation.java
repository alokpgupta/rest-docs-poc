package io.gati.web.doc;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class ErrorResponseDocumentation {

	public static final ResponseFieldsSnippet ERROR_RES_SNIPPET = errorResponseSnippet();
	
	private static ResponseFieldsSnippet errorResponseSnippet() {
		return responseFields(fieldWithPath("[].attr").description("Attribute Name"),
				fieldWithPath("[].code").description("Error Code"),
				fieldWithPath("[].desc").description("Error Description"));
	}
}
