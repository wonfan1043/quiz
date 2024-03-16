package com.example.quiz.ifs;

import com.example.quiz.vo.AnswerReq;
import com.example.quiz.vo.BaseRes;
import com.example.quiz.vo.CreateOrUpdateReq;
import com.example.quiz.vo.DeleteQuizReq;
import com.example.quiz.vo.DeleteQusReq;
import com.example.quiz.vo.SearchReq;
import com.example.quiz.vo.SearchRes;
import com.example.quiz.vo.StatisticsRes;

public interface QuizService {
	
	public BaseRes create(CreateOrUpdateReq req);
	
	public SearchRes search(SearchReq req);
	//因為沒有寫開始日期不能晚於結束日期的防呆，所以前端的下拉選單要記得寫
	
	public BaseRes deleteQuiz(DeleteQuizReq req);
	
	public BaseRes deleteQuestions(DeleteQusReq req);	
	
	public BaseRes update(CreateOrUpdateReq req);
	
	public BaseRes answer(AnswerReq req);
	
	public StatisticsRes statistics(int quizId);
}
