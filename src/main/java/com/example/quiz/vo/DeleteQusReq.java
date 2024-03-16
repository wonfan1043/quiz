package com.example.quiz.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeleteQusReq {
	
	@JsonProperty("quiz_id")
	int quizId;
	
	@JsonProperty("qu_ids")
	List<Integer> quIds;

	public DeleteQusReq() {
		super();
	}

	public DeleteQusReq(int quizId, List<Integer> quIds) {
		super();
		this.quizId = quizId;
		this.quIds = quIds;
	}

	public int getQuizId() {
		return quizId;
	}

	public void setQuizId(int quizId) {
		this.quizId = quizId;
	}

	public List<Integer> getQuIds() {
		return quIds;
	}

	public void setQuIds(List<Integer> quIds) {
		this.quIds = quIds;
	}
	
}
