package com.github.davidmoten.javabuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.davidmoten.javabuilder.Generator.Definition;
import com.google.common.base.Optional;

public class GeneratorTest {

	@Test
	public void test1() {
		Generator g = new Generator();
		String result = g.generate(getClass().getResourceAsStream(
				"/fragment1.txt"));
		System.out.println(result);
	}

	@Test
	public void testParseLine() {
		Optional<Definition> def = Generator.parse("List<String> list");
		assertTrue(def.isPresent());
		assertEquals("list", def.get().name);
		assertEquals("List<String>", def.get().type);
	}

	@Test
	public void testParseLineWithSpaceInType() {
		Optional<Definition> def = Generator.parse("Thing<T, R> thing");
		assertTrue(def.isPresent());
		assertEquals("thing", def.get().name);
		assertEquals("Thing<T, R>", def.get().type);
	}

	@Test
	public void testParseClassName() {
		assertEquals("Fred",
				Generator.parseClassName("public static class Fred {").get());
	}

	@Test
	public void testDefinitionParserIgnoresClassDefinitions() {
		assertFalse(Generator.parse("public class Fred {").isPresent());
	}
}
