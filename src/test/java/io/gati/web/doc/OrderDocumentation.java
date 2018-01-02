package io.gati.web.doc;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import org.springframework.restdocs.constraints.ConstraintDescriptionResolver;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

import io.gati.web.model.Line;
import io.gati.web.model.Order;
import lombok.Setter;

public class OrderDocumentation {

	private static final String LINE_QTY_DESC = "quantity ordered";
	private static final String ITEM_PRICE_DESC = "price of the item";
	private static final String ITEM_ID_DESC = "item id";
	private static final String LINE_ID_DESC = "order line id";
	private static final String ORDER_IS_DRAFT_DESC = "order is draft";
	private static final String ORDER_BRAND_DESC = "brand of the order";
	private static final String ORDER_ID_DESC = "id of the order";
	private static final String EMAIL_ID_DESC = "email id of customer";
	
	@Setter
	private ConstraintDescriptionResolver constraintDescriptionResolver;
	
	public ResponseFieldsSnippet orderResponseSnippet() {
		return responseFields(fieldWithPath("emailId").type(JsonFieldType.STRING).description(EMAIL_ID_DESC).optional(),
				fieldWithPath("id").type(JsonFieldType.STRING).description(ORDER_ID_DESC),
				fieldWithPath("brand").type(JsonFieldType.STRING).description(ORDER_BRAND_DESC).optional(),
				fieldWithPath("isDraft").type(JsonFieldType.BOOLEAN).description(ORDER_IS_DRAFT_DESC).optional(),
				fieldWithPath("lines[].id").type(JsonFieldType.NUMBER).description(LINE_ID_DESC).optional(),
				fieldWithPath("lines[].itemId").type(JsonFieldType.STRING).description(ITEM_ID_DESC).optional(),
				fieldWithPath("lines[].price").type(JsonFieldType.NUMBER).description(ITEM_PRICE_DESC).optional(),
				fieldWithPath("lines[].quantity").type(JsonFieldType.NUMBER).description(LINE_QTY_DESC).optional());
	}

	public RequestFieldsSnippet orderRequestSnippet() {
		ConstrainedFields orderFields = new ConstrainedFields(Order.class, constraintDescriptionResolver);
		ConstrainedFields lineFields = new ConstrainedFields(Line.class, constraintDescriptionResolver);
		return requestFields(orderFields.withPath("emailId").description(EMAIL_ID_DESC),
				orderFields.withPath("brand").description(ORDER_BRAND_DESC),
				orderFields.withPath("isDraft").description(ORDER_IS_DRAFT_DESC),
				lineFields.withPath("lines[].id").description(LINE_ID_DESC),
				lineFields.withPath("lines[].itemId").description(ITEM_ID_DESC),
				lineFields.withPath("lines[].price").description(ITEM_PRICE_DESC).optional(),
				lineFields.withPath("lines[].quantity").description(LINE_QTY_DESC));
	}
}
