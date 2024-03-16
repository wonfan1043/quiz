package com.example.quiz.controller;

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
import com.example.quiz.vo.DeleteQuizReq;
import com.example.quiz.vo.DeleteQusReq;
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
	public BaseRes deleteQuiz(@RequestBody DeleteQuizReq req) {
		return quizService.deleteQuiz(req);
	}

	@PostMapping(value = "quiz/delete_question")
	public BaseRes deleteQuestions(@RequestBody DeleteQusReq req) {
		return quizService.deleteQuestions(req);
	}

	@PostMapping(value = "quiz/update")
	public BaseRes update(@RequestBody CreateOrUpdateReq req) {
		return quizService.update(req);
	}

	@PostMapping(value = "quiz/answer")
	public BaseRes answer(@RequestBody AnswerReq req) {
		return quizService.answer(req);
	}

	@GetMapping(value = "quiz/statistics")
	public StatisticsRes statistics(@RequestParam(value = "quiz_id") int quizId) {
		return quizService.statistics(quizId);
	}
}
