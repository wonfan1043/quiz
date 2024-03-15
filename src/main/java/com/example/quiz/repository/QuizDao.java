package com.example.quiz.repository;

import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.quiz.entity.Quiz;
import com.example.quiz.entity.QuizId;

@Repository
@Transactional
public interface QuizDao extends JpaRepository<Quiz, QuizId> {

//確認問卷是否已存在
	public boolean existsByQuizId(int quizId);

//搜尋問卷
	public List<Quiz> findByQuizNameContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqual(String quizName,
			LocalDate startDate, LocalDate endDate);
	
	public List<Quiz> findByQuizNameContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqualAndPublishedTrue(String quizName,
			LocalDate startDate, LocalDate endDate);

//刪除問卷
//	All可寫可不寫，因為findByQuizIdIn本身就是回一個list，寫All只是方便工程師判斷回傳的資料是否為多筆
//	public List<Quiz> findAllByQuizIdIn(List<Integer> quizIds);
	public void deleteByQuizIdInAndPublishedFalseOrQuizIdInAndStartDateAfter(List<Integer> quizIds1,
			List<Integer> quizIds2, LocalDate now);

//刪除問題
	public void deleteByQuizIdAndQuIdInAndPublishedFalseOrQuizIdAndQuIdInAndStartDateAfter(int quizId1,
			List<Integer> quIds1, int quizId2, List<Integer> quIds2, LocalDate now);

	public List<Quiz> findByQuizIdAndPublishedFalseOrQuizIdAndStartDateAfterOrderByQuId(int quizId1, int quizId2,
			LocalDate now);

	public void deleteByQuizId(int quizId);
	
//更新問卷
	//檢查問卷是否能夠被修改
	public boolean existsByQuizIdAndPublishedFalseOrQuizIdAndStartDateAfter(int quizId1, int quizId2,
			LocalDate now);
	
//找出必填問題
	@Query(value = "select qu_id from quiz where quiz_id = ?1 and necessary = true", nativeQuery = true)
	public List<Integer> findQuIdsByQuizIdAndNecessaryTrue(int quizId);
	//?為佔位符，代表要帶入參數，1代表第一個參數，因此?1代表帶入第一個參數
	
//用quizId找出問卷
	public List<Quiz> findByQuizId(int quizId);
	
}
