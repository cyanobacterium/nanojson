package com.grack.nanojson;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class JsonWriterTest {
	/**
	 * Test emitting simple values.
	 */
	@Test
	public void testSimpleValues() {
		assertEquals("true", JsonWriter.string().value(true).close());
		assertEquals("null", JsonWriter.string().value(null).close());
		assertEquals("1.0", JsonWriter.string().value(1.0).close());
		assertEquals("1", JsonWriter.string().value(1).close());
		assertEquals("\"abc\"", JsonWriter.string().value("abc").close());
	}

	/**
	 * Test a simple array.
	 */
	@Test
	public void testArray() {
		String json = JsonWriter.string()
			.array()
				.value(true)
				.value(false)
				.value(true)
			.end()
		.close();
		assertEquals("[true,false,true]", json);
	}

	/**
	 * Test a nested array.
	 */
	@Test
	public void testNestedArray() {
		String json = JsonWriter.string()
			.array()
				.array()
					.array()
						.value(true)
						.value(false)
						.value(true)
					.end()
				.end()
			.end()
		.close();
		assertEquals("[[[true,false,true]]]", json);
	}

	/**
	 * Test a nested array.
	 */
	@Test
	public void testNestedArray2() {
		String json = JsonWriter.string()
			.array()
				.value(true)
				.array()
					.array()
						.value(false)
					.end()
				.end()
				.value(true)
			.end()
		.close();
		assertEquals("[true,[[false]],true]", json);
	}

	/**
	 * Test a simple object.
	 */
	@Test
	public void testObject() {
		String json = JsonWriter.string()
			.object()
				.value("a", true)
				.value("b", false)
				.value("c", true)
			.end()
		.close();
		assertEquals("{\"a\":true,\"b\":false,\"c\":true}", json);
	}

	/**
	 * Test a nested object.
	 */
	@Test
	public void testNestedObject() {
		String json = JsonWriter.string()
			.object()
				.object("a")
					.value("b", false)
					.value("c", true)
				.end()
			.end()
		.close();
		assertEquals("{\"a\":{\"b\":false,\"c\":true}}", json);
	}
}