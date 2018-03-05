package io.gati.web.model;

import java.math.BigDecimal;
import java.util.ArrayList;

import lombok.val;

public class OrderFixtureFactory {

	public static Order createDefaultOrder() {
		Order order = new Order().withBrand("WE").withIsDraft(true).withEmailId("test@gmail.com")
				.withLines(new ArrayList<>());
		val line1 = new Line().withId(1).withItemId("S623812").withQuantity(2);
		val line2 = new Line().withId(2).withItemId("S623814").withPrice(BigDecimal.ONE).withQuantity(1);
		order.getLines().map(list -> list.add(line1));
		order.getLines().map(list -> list.add(line2));
		return order;
	}
}
