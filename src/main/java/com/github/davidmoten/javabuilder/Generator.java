package com.github.davidmoten.javabuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.io.CharStreams;

public class Generator {

	public String generate(InputStream is) {
		try {
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			PrintStream out = new PrintStream(bytes);
			List<String> lines = CharStreams
					.readLines(new InputStreamReader(is));

			String className = FluentIterable.from(lines)
					.transform(toClassDefinition)
					.filter(Generator.isPresent(String.class))
					.transform(get(String.class)).first().or("ClassName");

			ImmutableList<Definition> definitions = FluentIterable.from(lines)
					.filter(nonEmptyLines).transform(toDefinition)
					.filter(isPresent(Definition.class))
					.transform(get(Definition.class)).toList();
			writeClass(out, className, definitions);
			return bytes.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private final static List<String> reservedWords = Arrays.asList("private",
			"protected", "final", "public", "protected");

	private final static Predicate<String> nonEmptyLines = new Predicate<String>() {

		@Override
		public boolean apply(String s) {
			return s.trim().length() > 0;
		}
	};

	private final static Function<String, Optional<String>> toClassDefinition = new Function<String, Optional<String>>() {

		@Override
		public Optional<String> apply(String line) {
			return parseClassName(line);
		}

	};

	@VisibleForTesting
	static Optional<String> parseClassName(String line) {
		Pattern pattern = Pattern
				.compile("^.*\\bclass\\s+\\b(\\w+)\\b\\s*\\{.*$");
		Matcher matcher = pattern.matcher(line);
		if (matcher.matches())
			return Optional.of(matcher.group(1));
		else
			return Optional.absent();
	}

	private final static <T> Predicate<Optional<T>> isPresent(Class<T> cls) {
		return new Predicate<Optional<T>>() {

			@Override
			public boolean apply(Optional<T> input) {
				return input.isPresent();
			}
		};
	}

	private final static Function<String, Optional<Definition>> toDefinition = new Function<String, Optional<Definition>>() {

		@Override
		public Optional<Definition> apply(String line) {
			return parse(line);
		}
	};

	private <T> Function<Optional<T>, T> get(Class<T> cls) {
		return new Function<Optional<T>, T>() {

			@Override
			public T apply(Optional<T> def) {
				return def.get();
			}
		};
	}

	static class Definition {
		String name;
		String type;

		Definition(String name, String type) {
			super();
			this.name = name;
			this.type = type;
		}

		@Override
		public String toString() {
			return "Definition [name=" + name + ", type=" + type + "]";
		}

	}

	private void writeClass(PrintStream out, String className,
			ImmutableList<Definition> defs) {
		String indent = "    ";
		String sp = " ";
		String sc = ";";
		String eq = "=";
		for (Definition def : defs) {
			out.println(indent + "private final " + def.type + sp + def.name
					+ sc);
		}

		out.println();

		out.print(indent + "private " + className + "(");
		{
			boolean firstTime = true;
			for (Definition def : defs) {
				if (!firstTime)
					out.print(", ");
				out.print(def.type + sp + def.name);
				firstTime = false;
			}
		}
		out.println("){");

		for (Definition def : defs) {
			out.println(indent + indent + "this." + def.name + sp + eq + sp
					+ def.name + sc);
		}
		out.println(indent + "}");
		out.println();
		// getters
		for (Definition def : defs) {
			out.println(indent + "public " + def.type + sp + "get"
					+ upperFirst(def.name) + "() {");
			out.println(indent + indent + "return " + def.name + sc);
			out.println(indent + "}");
			out.println();
		}

		// static builder() method
		out.println(indent + "public static Builder builder() {");
		out.println(indent + indent + "return new Builder();");
		out.println(indent + "}");
		out.println();

		// builder class

		out.println(indent + "public static class Builder {");
		out.println();
		// members
		for (Definition def : defs) {
			out.println(indent + indent + "private " + def.type + sp + def.name
					+ sc);
		}
		out.println();
		// private builder constructor

		out.println(indent + indent + "private Builder() {");
		out.println(indent + indent + "}");
		out.println();

		// setters
		for (Definition def : defs) {
			out.println(indent + indent + "public Builder " + def.name + "("
					+ def.type + sp + def.name + ") {");
			out.println(indent + indent + indent + "this." + def.name + " = "
					+ def.name + sc);
			out.println(indent + indent + "}");
			out.println();
		}

		// build method

		out.println(indent + indent + "public " + className + " build() {");
		out.print(indent + indent + indent + "return new " + className + "(");
		boolean firstTime = true;
		for (Definition def : defs) {
			if (!firstTime)
				out.print(", ");
			out.print(def.name);
			firstTime = false;
		}
		out.println(");");

		out.println(indent + indent + "}");

		// close builder
		out.println(indent + "}");

	}

	private static String upperFirst(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	@VisibleForTesting
	static Optional<Definition> parse(String line) {
		Pattern p = Pattern.compile("\\bclass\\b");
		if (p.matcher(line).find())
			return Optional.absent();
		for (String word : reservedWords)
			line = line.replaceAll("\\b" + word + "\\b", "");
		line = line.replace(";", "");
		line = line.trim();
		Pattern pattern = Pattern
				.compile("^([^=]*)(?:\\s+)(\\b[^=]+\\b).*(=.*)?$");
		Matcher matcher = pattern.matcher(line);
		if (matcher.matches()) {
			return Optional.of(new Definition(matcher.group(2), matcher
					.group(1)));
		} else
			return Optional.absent();
	}
}
