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

//�T�{�ݨ��O�_�w�s�b
	public boolean existsByQuizId(int quizId);

//�j�M�ݨ�
	public List<Quiz> findByQuizNameContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqual(String quizName,
			LocalDate startDate, LocalDate endDate);
	
	public List<Quiz> findByQuizNameContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqualAndPublishedTrue(String quizName,
			LocalDate startDate, LocalDate endDate);

//�R���ݨ�
//	All�i�g�i���g�A�]��findByQuizIdIn�����N�O�^�@��list�A�gAll�u�O��K�u�{�v�P�_�^�Ǫ���ƬO�_���h��
//	public List<Quiz> findAllByQuizIdIn(List<Integer> quizIds);
	public void deleteByQuizIdInAndPublishedFalseOrQuizIdInAndStartDateAfter(List<Integer> quizIds1,
			List<Integer> quizIds2, LocalDate now);

//�R�����D
	public void deleteByQuizIdAndQuIdInAndPublishedFalseOrQuizIdAndQuIdInAndStartDateAfter(int quizId1,
			List<Integer> quIds1, int quizId2, List<Integer> quIds2, LocalDate now);

	public List<Quiz> findByQuizIdAndPublishedFalseOrQuizIdAndStartDateAfterOrderByQuId(int quizId1, int quizId2,
			LocalDate now);

	public void deleteByQuizId(int quizId);
	
//��s�ݨ�
	//�ˬd�ݨ��O�_����Q�ק�
	public boolean existsByQuizIdAndPublishedFalseOrQuizIdAndStartDateAfter(int quizId1, int quizId2,
			LocalDate now);
	
//��X������D
	@Query(value = "select qu_id from quiz where quiz_id = ?1 and necessary = true", nativeQuery = true)
	public List<Integer> findQuIdsByQuizIdAndNecessaryTrue(int quizId);
	//?������šA�N��n�a�J�ѼơA1�N��Ĥ@�ӰѼơA�]��?1�N��a�J�Ĥ@�ӰѼ�
	
//��quizId��X�ݨ�
	public List<Quiz> findByQuizId(int quizId);
	
}
