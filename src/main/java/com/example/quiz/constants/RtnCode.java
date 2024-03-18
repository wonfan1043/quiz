package com.example.quiz.constants;

public enum RtnCode {

	SUCCESS(200, "Success!!!"),
	PARAM_ERROR(400, "Param error!!!"),
	QUIZ_ALREADY_EXISTS(400, "Quiz already exists!!!"),
	DUPLICATED_QUESTION_ID(400, "Duplicated question ID!!!"),
	TIME_FORMAT_ERROR(400, "Time format error!!!"),
	QUIZ_IS_NOT_FOUND(400, "Quiz is not found!!!"),
	QUIZ_ID_ERROR(400, "Quiz ID error!!!"),
	QUESTION_IS_NOT_ANSWERED(400, "Question is not answered!!!"),
	DUPLICATED_QUIZ_ANSWER(400, "Duplicated quiz answer!!!"),
	QUIZ_ID_DOES_NOT_MATCH(400, "Quiz ID does not match!!!"),
	DELETE_QUIZ_ERROR(400, "Delete quzi error!!!"),
	SAVE_QUIZ_ERROR(400, "Save quzi error!!!");
	
	//Java中static是方便呼叫，final是因為是常數，常數的話名稱要全大寫並用底線連接
	public static final int NOT_FOUND_CODE = 404;
	
	public static final int ERROR_CODE = 400;
	
	private int code;

	private String message;

	private RtnCode(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

}
