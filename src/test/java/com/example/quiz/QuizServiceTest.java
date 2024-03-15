package com.example.quiz;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import com.example.quiz.entity.Quiz;
import com.example.quiz.entity.QuizId;
import com.example.quiz.ifs.QuizService;
import com.example.quiz.repository.QuizDao;
import com.example.quiz.vo.BaseRes;
import com.example.quiz.vo.CreateOrUpdateReq;

@SpringBootTest
public class QuizServiceTest {

	@Autowired
	private QuizService quizService;

	@Autowired
	private QuizDao quizDao;

	// ������������������������������������������������������������������������������������������������������������������������
	// �N�{���X��X�ܦ��p����k�M��[�W@BeforeEach����annotation�o�˴N�|�۰ʤ�
	// ������������������������������������������������������������������������������������������������������������������������

	@BeforeEach
	private void addData() {
		CreateOrUpdateReq req = new CreateOrUpdateReq();
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(2, 1, "test", "test", LocalDate.now().plusDays(2),
				LocalDate.now().plusDays(9), "q_test", "singel", true, "A;B;C;D", false))));
		quizService.create(req);
	}
	
	@AfterEach
	private void deleteData() {
		quizDao.deleteById(new QuizId(2, 1));
	}
	
	/*
	 * @BeforeEach //�C�@�Ӵ��հ���e���|���@�� �� �X�Ӵ��մN���X�� 
	 * private void beforeEach(){
	 * System.out.println("BeforeEach test"); 
	 * }
	 * 
	 * @BeforeAll //����Ҧ����իe���@�� �� �u���@�� 
	 * private static void beforeAll() {
	 * System.out.println("BeforeAll test"); 
	 * }
	 * 
	 * @AfterEach //�C�@�Ӵ��հ���᳣�|���@�� �� �X�Ӵ��մN���X�� 
	 * private void afterEach() {
	 * System.out.println("AfterEach test"); 
	 * }
	 * 
	 * @AfterAll //����Ҧ����իᰵ�@�� �� �u���@�� 
	 * private void afterAll() {
	 * System.out.println("AfterAll test"); 
	 * }
	*/
	
	// ������������������������������������������������������������������������������������������������������������������������
	// ���աD���աD���աD���աD���աD���աD���աD���աD���աD���աD���աD���աD����
	// ������������������������������������������������������������������������������������������������������������������������

	@Test
	public void createTest() {
		// =====���� req����=====
		CreateOrUpdateReq req = new CreateOrUpdateReq();
		BaseRes res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "���� req����");
		// =====���� quizId=====
		quizIdTest(req, res);
		// =====���� quId=====
		quIdTest(req, res);
		// =====���� quizName=====
		quizNameTest(req, res);
		// =====���� startDate=====
		startDateTest(req, res);
		// =====���� endDate=====
		endDateTest(req, res);
		// =====���� question=====
		questionTest(req, res);
		// =====���� type=====
		qtypeTest(req, res);
		// =====���� startDate > endDate=====
		startDateGreaterThanEndDateTest(req, res);
		// =====���� ���\=====
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "test", "test", LocalDate.now().plusDays(2),
				LocalDate.now().plusDays(9), "q_test", "singel", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 200, "���� ���\");
		// =====���� �w�s�b���=====
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "test", "test", LocalDate.now().plusDays(2),
				LocalDate.now().plusDays(9), "q_test", "singel", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "���� �w�s�b���");
		// =====�̫� �R�����ո��=====
		quizDao.deleteByQuizId(req.getQuizList().get(0).getQuizId());
	}
	
	// ������������������������������������������������������������������������������������������������������������������������
	//    �p����k�̡D�p����k�̡D�p����k�̡D�p����k�̡D�p����k�̡D�p����k��
	// ������������������������������������������������������������������������������������������������������������������������
	
	// =====�s�ؤ@�����`�����=====
	private Quiz newQuiz() {
		return new Quiz(2, 2, "test", "test", LocalDate.now().plusDays(2),
				LocalDate.now().plusDays(9), "q_test", "singel", true, "A;B;C;D", false);
	}

	// =====���� quizId=====
	private void quizIdTest(CreateOrUpdateReq req, BaseRes res) {
		Quiz quiz= newQuiz();
		quiz.setQuizId(0);
		req.setQuizList(new ArrayList<>(Arrays.asList(quiz)));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "���� quizId");
	}
	
	// =====���� quId=====
	private void quIdTest(CreateOrUpdateReq req, BaseRes res) {
		Quiz quiz= newQuiz();
		quiz.setQuId(-1);
		req.setQuizList(new ArrayList<>(Arrays.asList(quiz)));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "���� quId");
	}
	
	// =====���� quizName=====
	private void quizNameTest(CreateOrUpdateReq req, BaseRes res) {
		Quiz quiz= newQuiz();
		quiz.setQuizName(null);
		req.setQuizList(new ArrayList<>(Arrays.asList(quiz)));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "���� quizName");
	}

	// =====���� startDate=====
	private void startDateTest(CreateOrUpdateReq req, BaseRes res) {
		Quiz quiz= newQuiz();
		quiz.setStartDate(null);
		req.setQuizList(new ArrayList<>(Arrays.asList(quiz)));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "���� startDate");
	}
	
	// =====���� endDate=====
	private void endDateTest(CreateOrUpdateReq req, BaseRes res) {
		Quiz quiz= newQuiz();
		quiz.setEndDate(null);
		req.setQuizList(new ArrayList<>(Arrays.asList(quiz)));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "���� endDate");
	}
	
	// =====���� question=====
	private void questionTest(CreateOrUpdateReq req, BaseRes res) {
		Quiz quiz= newQuiz();
		quiz.setQuestion(null);
		req.setQuizList(new ArrayList<>(Arrays.asList(quiz)));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "���� question");
	}
	
	// =====���� type=====
	private void qtypeTest(CreateOrUpdateReq req, BaseRes res) {
		Quiz quiz= newQuiz();
		quiz.setType(null);
		req.setQuizList(new ArrayList<>(Arrays.asList(quiz)));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "���� type");
	}
	
	// =====���� startDate > endDate=====
	private void startDateGreaterThanEndDateTest(CreateOrUpdateReq req, BaseRes res) {
		Quiz quiz= newQuiz();
		quiz.setStartDate(LocalDate.now().plusDays(9));
		quiz.setEndDate(LocalDate.now().plusDays(2));
		req.setQuizList(new ArrayList<>(Arrays.asList(quiz)));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "���� startDate > EndDate");
	}
}
