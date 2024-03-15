package com.example.quiz.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.quiz.ifs.QuizService;
import com.example.quiz.vo.AnswerReq;
import com.example.quiz.vo.BaseRes;
import com.example.quiz.vo.CreateOrUpdateReq;
import com.example.quiz.vo.SearchReq;
import com.example.quiz.vo.SearchRes;
import com.example.quiz.vo.StatisticsRes;

@CrossOrigin
@RestController
public class QuizServiceController {
	
	@Autowired
	private QuizService quizService;
	
	@PostMapping(value = "quiz/create")
	public BaseRes create(@RequestBody CreateOrUpdateReq req) {
		return quizService.create(req);
	}
	
	@GetMapping(value = "quiz/search")
	public SearchRes search(@RequestBody SearchReq req) {
		return quizService.search(req);
	}
	
	@PostMapping(value = "quiz/delete_quiz")
	public BaseRes deleteQuiz(List<Integer> quizIds) {
		return quizService.deleteQuiz(quizIds);
	}
	
	
	public BaseRes deleteQuestions(int quizId, List<Integer> quIds) {
		return quizService.deleteQuestions(quizId, quIds);
	}
	
	public BaseRes update(CreateOrUpdateReq req) {
		return quizService.update(req);
	}
	
	public BaseRes answer(AnswerReq req) {
		return quizService.answer(req);
	}
	
	@GetMapping(value = "quiz/statistics")
	public StatisticsRes statistics(@RequestParam(value = "quiz_id")  int quizId) {
		return quizService.statistics(quizId);
	}
}
