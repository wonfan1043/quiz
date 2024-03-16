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
	//�]���S���g�}�l�������ߩ󵲧���������b�A�ҥH�e�ݪ��U�Կ��n�O�o�g
	
	public BaseRes deleteQuiz(DeleteQuizReq req);
	
	public BaseRes deleteQuestions(DeleteQusReq req);	
	
	public BaseRes update(CreateOrUpdateReq req);
	
	public BaseRes answer(AnswerReq req);
	
	public StatisticsRes statistics(int quizId);
}
