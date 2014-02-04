package com.github.davidmoten.javabuilder;

public class SingleFieldBuilder {

	private final String name;

	private SingleFieldBuilder(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private String name;

		private Builder() {

		}

		public Builder name(String value) {
			this.name = value;
			return this;
		}

		public SingleFieldBuilder build() {
			return new SingleFieldBuilder(name);
		}
	}

}
