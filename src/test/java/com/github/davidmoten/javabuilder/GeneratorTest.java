package com.github.davidmoten.javabuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;

import com.github.davidmoten.javabuilder.Generator.Definition;
import com.google.common.base.Optional;
import com.google.common.io.CharStreams;

public class GeneratorTest {

	@Test
	public void test1() throws IOException {
		Generator g = new Generator();
		String result = g.generate(getClass().getResourceAsStream(
				"/fragment1.txt"));
		String expected = CharStreams.toString(new InputStreamReader(getClass()
				.getResourceAsStream("/result1.txt")));
		System.out.println(result);
		assertEquals(expected, result);
	}

	@Test
	public void test2() {
		Generator g = new Generator();
		String result = g.generate(getClass().getResourceAsStream(
				"/fragment2.txt"));
		System.out.println(result);
	}

	@Test
	public void testParseLine() {
		Optional<Definition> def = Generator
				.parseDefinition("List<String> list");
		assertTrue(def.isPresent());
		assertEquals("list", def.get().name);
		assertEquals("List<String>", def.get().type);
	}

	@Test
	public void testParseLineWithSpaceInType() {
		Optional<Definition> def = Generator
				.parseDefinition("Thing<T, R> thing");
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
		assertFalse(Generator.parseDefinition("public class Fred {")
				.isPresent());
	}
}
