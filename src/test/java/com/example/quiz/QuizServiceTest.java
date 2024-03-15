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

	// ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼
	// 將程式碼抽出變成私有方法然後加上@BeforeEach等的annotation這樣就會自動化
	// ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼

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
	 * @BeforeEach //每一個測試執行前都會做一次 → 幾個測試就做幾次 
	 * private void beforeEach(){
	 * System.out.println("BeforeEach test"); 
	 * }
	 * 
	 * @BeforeAll //執行所有測試前做一次 → 只做一次 
	 * private static void beforeAll() {
	 * System.out.println("BeforeAll test"); 
	 * }
	 * 
	 * @AfterEach //每一個測試執行後都會做一次 → 幾個測試就做幾次 
	 * private void afterEach() {
	 * System.out.println("AfterEach test"); 
	 * }
	 * 
	 * @AfterAll //執行所有測試後做一次 → 只做一次 
	 * private void afterAll() {
	 * System.out.println("AfterAll test"); 
	 * }
	*/
	
	// ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼
	// 測試．測試．測試．測試．測試．測試．測試．測試．測試．測試．測試．測試．測試
	// ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼

	@Test
	public void createTest() {
		// =====測試 req為空=====
		CreateOrUpdateReq req = new CreateOrUpdateReq();
		BaseRes res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "測試 req為空");
		// =====測試 quizId=====
		quizIdTest(req, res);
		// =====測試 quId=====
		quIdTest(req, res);
		// =====測試 quizName=====
		quizNameTest(req, res);
		// =====測試 startDate=====
		startDateTest(req, res);
		// =====測試 endDate=====
		endDateTest(req, res);
		// =====測試 question=====
		questionTest(req, res);
		// =====測試 type=====
		qtypeTest(req, res);
		// =====測試 startDate > endDate=====
		startDateGreaterThanEndDateTest(req, res);
		// =====測試 成功=====
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "test", "test", LocalDate.now().plusDays(2),
				LocalDate.now().plusDays(9), "q_test", "singel", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 200, "測試 成功");
		// =====測試 已存在資料=====
		req.setQuizList(new ArrayList<>(Arrays.asList(new Quiz(1, 1, "test", "test", LocalDate.now().plusDays(2),
				LocalDate.now().plusDays(9), "q_test", "singel", true, "A;B;C;D", false))));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "測試 已存在資料");
		// =====最後 刪除測試資料=====
		quizDao.deleteByQuizId(req.getQuizList().get(0).getQuizId());
	}
	
	// ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼
	//    私有方法們．私有方法們．私有方法們．私有方法們．私有方法們．私有方法們
	// ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼
	
	// =====新建一筆正常的資料=====
	private Quiz newQuiz() {
		return new Quiz(2, 2, "test", "test", LocalDate.now().plusDays(2),
				LocalDate.now().plusDays(9), "q_test", "singel", true, "A;B;C;D", false);
	}

	// =====測試 quizId=====
	private void quizIdTest(CreateOrUpdateReq req, BaseRes res) {
		Quiz quiz= newQuiz();
		quiz.setQuizId(0);
		req.setQuizList(new ArrayList<>(Arrays.asList(quiz)));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "測試 quizId");
	}
	
	// =====測試 quId=====
	private void quIdTest(CreateOrUpdateReq req, BaseRes res) {
		Quiz quiz= newQuiz();
		quiz.setQuId(-1);
		req.setQuizList(new ArrayList<>(Arrays.asList(quiz)));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "測試 quId");
	}
	
	// =====測試 quizName=====
	private void quizNameTest(CreateOrUpdateReq req, BaseRes res) {
		Quiz quiz= newQuiz();
		quiz.setQuizName(null);
		req.setQuizList(new ArrayList<>(Arrays.asList(quiz)));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "測試 quizName");
	}

	// =====測試 startDate=====
	private void startDateTest(CreateOrUpdateReq req, BaseRes res) {
		Quiz quiz= newQuiz();
		quiz.setStartDate(null);
		req.setQuizList(new ArrayList<>(Arrays.asList(quiz)));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "測試 startDate");
	}
	
	// =====測試 endDate=====
	private void endDateTest(CreateOrUpdateReq req, BaseRes res) {
		Quiz quiz= newQuiz();
		quiz.setEndDate(null);
		req.setQuizList(new ArrayList<>(Arrays.asList(quiz)));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "測試 endDate");
	}
	
	// =====測試 question=====
	private void questionTest(CreateOrUpdateReq req, BaseRes res) {
		Quiz quiz= newQuiz();
		quiz.setQuestion(null);
		req.setQuizList(new ArrayList<>(Arrays.asList(quiz)));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "測試 question");
	}
	
	// =====測試 type=====
	private void qtypeTest(CreateOrUpdateReq req, BaseRes res) {
		Quiz quiz= newQuiz();
		quiz.setType(null);
		req.setQuizList(new ArrayList<>(Arrays.asList(quiz)));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "測試 type");
	}
	
	// =====測試 startDate > endDate=====
	private void startDateGreaterThanEndDateTest(CreateOrUpdateReq req, BaseRes res) {
		Quiz quiz= newQuiz();
		quiz.setStartDate(LocalDate.now().plusDays(9));
		quiz.setEndDate(LocalDate.now().plusDays(2));
		req.setQuizList(new ArrayList<>(Arrays.asList(quiz)));
		res = quizService.create(req);
		Assert.isTrue(res.getCode() == 400, "測試 startDate > EndDate");
	}
}
