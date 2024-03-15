package com.example.quiz.constants;

public enum Type {
	
	SINGEL_CHOICE("singel choice"), //
	MULTI_CHOICE("multi choice"), //
	TEXT("text");

	private String type;

	private Type(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
