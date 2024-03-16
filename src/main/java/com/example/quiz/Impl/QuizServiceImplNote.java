package com.example.quiz.Impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.example.quiz.constants.RtnCode;
import com.example.quiz.entity.Answer;
import com.example.quiz.entity.Quiz;
import com.example.quiz.ifs.QuizService;
import com.example.quiz.repository.AnswerDao;
import com.example.quiz.repository.QuizDao;
import com.example.quiz.vo.AnswerReq;
import com.example.quiz.vo.BaseRes;
import com.example.quiz.vo.CreateOrUpdateReq;
import com.example.quiz.vo.DeleteQuizReq;
import com.example.quiz.vo.DeleteQusReq;
import com.example.quiz.vo.SearchReq;
import com.example.quiz.vo.SearchRes;
import com.example.quiz.vo.StatisticsRes;

public class QuizServiceImplNote{

	@Autowired
	private QuizDao quizDao;

	@Autowired
	private AnswerDao answerDao;

	public BaseRes create(CreateOrUpdateReq req) {
// ▼▼▼▼▼▼▼▼▼▼ 防呆檢查 ▼▼▼▼▼▼▼▼▼▼
		return checkParams(req, true);
//		if(res != null) {
//			return res;
//		}
//// Step 6：檢查問卷是否已存在
//		if (quizDao.existsByQuizId(req.getQuizList().get(0).getQuizId())) {
//			return new BaseRes(RtnCode.QUIZ_ALREADY_EXISTS.getCode(), RtnCode.QUIZ_ALREADY_EXISTS.getMessage());
//		}
//
//// Step 7 :為求保險，根據前端傳回的是否要發布之布林值再次修改list中的published，然後再新增整張問卷並回傳成功訊息
//		for (Quiz item : req.getQuizList()) {
//			item.setPublished(req.isPublished());
//		}
//		quizDao.saveAll(req.getQuizList());
//		return new BaseRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage());
	}

	public SearchRes search(SearchReq req) {
		// 如果沒有輸入問卷名稱就帶空字串，containing加上空字串代表搜尋除了null以外的所有資料
		if (!StringUtils.hasText(req.getQuizName())) {
			req.setQuizName(""); 
		}
		// 如果沒有輸入開始時間就給一個很早很早的時間
		if (req.getStartDate() == null) {
			req.setStartDate(LocalDate.of(1970, 1, 1));
		}
		// 如果沒有輸入結束時間就給一個很晚很晚的時間
		if (req.getEndDate() == null) {
			req.setEndDate(LocalDate.of(9999, 12, 31));
		}

		// 撈資料：分為前台跟後台，前台使用者搜尋的話不能看到未發布的問卷，後台使用者則全部都能看到
		if (req.isBackend()) {
			return new SearchRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage(),
					quizDao.findByQuizNameContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqual(req.getQuizName(),
							req.getStartDate(), req.getEndDate()));
		} else {
			return new SearchRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage(),
					quizDao.findByQuizNameContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqualAndPublishedTrue(
							req.getQuizName(), req.getStartDate(), req.getEndDate()));
		}
	}

	public BaseRes deleteQuiz(DeleteQuizReq req) {
		// List屬於Collection，所以可以用他的語法同時判斷quizIds是否為null及空集合
		// 判斷不是空的即可，即便quizIds有負數也沒關係，只要有一個正數就值得進入DB蒐資料
		if (CollectionUtils.isEmpty(req.getQuizIds())) {
			return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
		}
		quizDao.deleteByQuizIdInAndPublishedFalseOrQuizIdInAndStartDateAfter(req.getQuizIds(), req.getQuizIds(), LocalDate.now());
		return new BaseRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage());
	}

	public BaseRes deleteQuestions(DeleteQusReq req) {
		// 1. 檢查傳入的資料是否有誤
		// 2. 取出整張問卷，刪除不要的資料 or 只保留要留下的資料，如果找不到問卷就回傳錯誤訊息
		// 3. 刪除資料庫中的整個問卷
		// 4. 存入整張處理好的問卷
		// 5. 回傳成功訊息

//1. 檢查傳入的資料是否有誤
		if (req.getQuizId() <= 0 || CollectionUtils.isEmpty(req.getQuIds())) {
			return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
		}
//2. 取出整張問卷，刪除不要的資料(方法一) or 只保留要留下的資料(方法二)，如果找不到問卷就回傳錯誤訊息
		List<Quiz> res = quizDao.findByQuizIdAndPublishedFalseOrQuizIdAndStartDateAfterOrderByQuId(req.getQuizId(), req.getQuizId(),
				LocalDate.now());
		if (res.isEmpty()) {
			return new BaseRes(RtnCode.QUIZ_IS_NOT_FOUND.getCode(), RtnCode.QUIZ_IS_NOT_FOUND.getMessage());
		}
// <方法一> 刪除指定資料
		// quids = 1, 4
		// quids = 1 → j = 0 & item = 1 → item - 1 - j = 1 - 1 - 0 = 0 → 刪除res的index 0
		// quids = 4 → j = 1 & item = 1 → item - 1 - j = 4 - 1 - 1 = 0 → 刪除res的index 2
		int j = 0;
		for (Integer item : req.getQuIds()) {
			res.remove(item - 1 - j);
			j++;
		}
		// 刪除資料後重新排序
		for (int i = 0; i < res.size(); i++) {
			res.get(i).setQuId(i + 1);
		}
//<方法二> 將要保留的資料加到另一個List
		// 宣告一個用來放要保留的資料的List → retainList
		// 如果quIds中不包含res的quId則加入retainList → 即保留不在刪除清單quIds中的資料
		List<Quiz> retainList = new ArrayList<>();
		for (Quiz item : res) {
			if (!req.getQuIds().contains(item.getQuId())) {
				retainList.add(item);
			}
		}
		// 刪除資料後重新排序
		for (int i = 0; i < res.size(); i++) {
			res.get(i).setQuId(i + 1);
		}
//3. 刪除資料庫中的整張問卷
		quizDao.deleteByQuizId(req.getQuizId());
//4. 存入整張處理好的問卷，但前題是刪完後還有東西可以存
//<方法一的話>
		if (!retainList.isEmpty()) {
			quizDao.saveAll(res);
		}
//<方法二的話>
		if (!retainList.isEmpty()) {
			quizDao.saveAll(retainList);
		}
//5. 回傳成功訊息
		return new BaseRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage());
	}

	public BaseRes update(CreateOrUpdateReq req) {
		return checkParams(req, false);
////1. 檢查傳入的資料是否為空
//		BaseRes res = checkParams(req);
//		if(res != null) {
//			return res;
//		}
////2. 檢查傳入的資料是否為同一張問卷，因為一次只能修改一張問卷
//		Set<Integer> quizIdSet = new HashSet<>();
//		for (Quiz item : req.getQuizList()) {
//			quizIdSet.add(item.getQuId());
//		}
//		if (quizIdSet.size() != 1) {
//			return new BaseRes(RtnCode.QUIZ_ID_ERROR.getCode(), RtnCode.QUIZ_ID_ERROR.getMessage());
//		}
////3. 檢查問卷是否能夠修改，即是否為未發布或未開始
//		if (!quizDao.existsByQuizIdAndPublishedFalseOrQuizIdAndStartDateAfter(req.getQuizList().get(0).getQuizId(),
//				req.getQuizList().get(0).getQuizId(), LocalDate.now())) {
//			return new BaseRes(RtnCode.QUIZ_IS_NOT_FOUND.getCode(), RtnCode.QUIZ_IS_NOT_FOUND.getMessage());
//		}
////4. 刪除資料庫中的整張問卷
//		quizDao.deleteByQuizId(req.getQuizList().get(0).getQuizId());
////5. 為求保險，根據前端傳回的是否要發布之布林值再次修改list中的published，然後再新增整張問卷並回傳成功訊息
//		for (Quiz item : req.getQuizList()) {
//			item.setPublished(req.isPublished());
//		}
//		quizDao.saveAll(req.getQuizList());
//		return new BaseRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage());
	}

	public BaseRes answer(AnswerReq req) {
// Step 1：檢查傳入的answerList有沒有東西
		if (CollectionUtils.isEmpty(req.getAnswerList())) {
			return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
		}
// Step 2：檢查傳入的answerList之參數是否OK
		for (Answer item : req.getAnswerList()) {
			if (!StringUtils.hasText(item.getName()) || !StringUtils.hasText(item.getPhone())
					|| !StringUtils.hasText(item.getEmail()) || item.getQuizId() <= 0 || item.getQuId() <= 0
					|| item.getAge() < 0) {
				return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
			}
		}
// Step 3：檢查quizId都一樣跟quId都不重複
		Set<Integer> quizIds = new HashSet<>();
		Set<Integer> quIds = new HashSet<>();
		for (Answer item : req.getAnswerList()) {
			quizIds.add(item.getQuizId());
			quIds.add(item.getQuId());
		}
		if (quizIds.size() != 1) {
			return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
		}
		if (quIds.size() != req.getAnswerList().size()) {
			return new BaseRes(RtnCode.DUPLICATED_QUESTION_ID.getCode(), RtnCode.DUPLICATED_QUESTION_ID.getMessage());
		}

// Step 4：檢查必填項目是否有必填
		List<Integer> res = quizDao.findQuIdsByQuizIdAndNecessaryTrue(req.getAnswerList().get(0).getQuizId());
//<方法一> 		
		for (Answer item : req.getAnswerList()) {
			if (res.contains(item.getQuId()) && !StringUtils.hasText(item.getAnswer())) {
				return new BaseRes(RtnCode.QUESTION_IS_NOT_ANSWERED.getCode(),
						RtnCode.QUESTION_IS_NOT_ANSWERED.getMessage());
			}
		}
//<方法二>
		for (int item : res) {
			Answer ans = req.getAnswerList().get(item - 1);
			if (!StringUtils.hasText(ans.getAnswer())) {
				return new BaseRes(RtnCode.QUESTION_IS_NOT_ANSWERED.getCode(),
						RtnCode.QUESTION_IS_NOT_ANSWERED.getMessage());
			}
		}
// Step 5：確認同一個email不能重複填寫同一張問卷
		if (answerDao.existsByQuizIdAndEmail(req.getAnswerList().get(0).getQuizId(),
				req.getAnswerList().get(0).getEmail())) {
			return new BaseRes(RtnCode.DUPLICATED_QUIZ_ANSWER.getCode(), RtnCode.DUPLICATED_QUIZ_ANSWER.getMessage());
		}
// Step 6：檢查完畢，儲存進DB並回傳成功訊息
		answerDao.saveAll(req.getAnswerList());
		return new BaseRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage());
	}

	public StatisticsRes statistics(int quizId) {
// Step 1：檢查參數是否正確
		if (quizId <= 0) {
			return new StatisticsRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
		}
// Step 2：到quiz把問卷撈出後取得問題的type為非簡答的部分放進另一個List裡面
		// 宣告一個Quiz的list，然後把quizId的那張問卷撈出來
		List<Quiz> quizList = quizDao.findByQuizId(quizId);
		// 宣告一個放題目編號的Integer List用來放所有選擇題的題目編號
		List<Integer> quIdList = new ArrayList<>();
		for (Quiz item : quizList) {
			// 判斷式：如果option是空的代表該題沒有選項，即為簡答題，因此要加入的是option非空白的題目編號
			if (StringUtils.hasText(item.getOptions())) {
				quIdList.add(item.getQuId());
			}
		}
// Step 3：依序把每題各自的答案撈出來，然後串成字串
		// 宣告一個Answer的List，先把該張問卷的所有回答撈出，並且要把所有答案依據quId排好
		List<Answer> answerList = answerDao.findByQuizIdOrderByQuId(quizId);
		// 宣告一個把問題編號跟所有回答做mapping的Map
		// 用來把每題選擇題的回答各自串成一個字串，即一個選項/答案會有一個大字串
		Map<Integer, String> quIdAnswerMap = new HashMap<>();
		// 因為要串所有回答，所以先對撈出的答案做遍歷
		for (Answer item : answerList) {
			// 判斷式：如果選擇題編號的List(quIdList)中含有從所有回答的List(answerList)中取出的quId
			//       → 該題為選擇題，需要做字串answer的串接
			if (quIdList.contains(item.getQuId())) {
				// 判斷式：如果串接各題目答案的quIdAnswerMap的Key值包含了從answerList中取出的quId
				//       → 該題已經有放入Map中，只需要做字串串接
				if (quIdAnswerMap.containsKey(item.getQuId())) {
					// 透過key值(quId)取得對應的value(answer)
					String str = quIdAnswerMap.get(item.getQuId());
					// 把原有的value(answer)和這次取得的value(answer)做串接然後更新value
					str += item.getAnswer();
					// 把更新好的value(串接好的answer)塞回去原本的key之下
					quIdAnswerMap.put(item.getQuId(), str);
				} else {
					// 判斷式：如果quIdAnswerMap的Key值尚未包含從answerList中取出的quId，代表Map中還沒有該題，那就直接新增
					quIdAnswerMap.put(item.getQuId(), item.getAnswer());
				}
			}
		}
// Step 4：計算每題每個選項的次數 → 把串好的字串中的各選項分別獨立用空字串代換，再用前後長度差去計算次數
		// new一個Map用來mapping「問題編號 - 選項 - 次數」，即mapping問題編號跟下方的answerCountMap
		// 這個Map的資料型態為answerCountMap的資料型態Map<String, Integer>
		// p.s. 這裡的順序會是先寫出for迴圈、宣告出answerCountMap後，才回來宣告quziIdAndAnsCountMap
		Map<Integer, Map<String, Integer>> quziIdAndAnsCountMap = new HashMap<>();
		// 因為要計算每個選項被選的次數，所以先對quIdAnswerMap做遍歷
		// 「Map名稱.entrySet()」 → 把要遍歷的對象從Map轉成entrySet，這樣就可以直接取得Map中的key和value
		for (Entry<Integer, String> item : quIdAnswerMap.entrySet()) {
			// new一個Map用來算每個答案的次數，即key(選項/答案)和value(次數)的mapping
			Map<String, Integer> answerCountMap = new HashMap<>();
			// 先取得每個問題的選項：
			// ※只要取出有人選的選項 → 用quIdAnswerMap中的quId去取※
			// Entry<Integer, String> item : quIdAnswerMap.entrySet()
			//          Key       Value
			// item = 題目編號 + 串好的答案字串
			String[] optionList = quizList.get(item.getKey() - 1).getOptions().split(";");
			//String[] optionList =     把各個選項放入名為optionList的字串陣列中
			//quizList.                 原問卷資料包
			//get(item.getKey() - 1).   取出(index)的資料→item.getKey()→有人選的選項之題號減1→因為quizList的資料是從index 0開始，因此每一題都存放在題號-1的index中
			//getOptions().             繼續取出該題的選項
			//split(";");               最後無腦用分號切開不同選項
			
			// 用foreach迴圈對optionList做遍歷以計算長度
			for (String option : optionList) {
				// 從quIdAnswerMap中取出串好的字串並計算其長度
				String newStr = item.getValue();
				int length1 = newStr.length();
				// 依序把選項換成空字串後塞回去並計算長度
				newStr = newStr.replace(option, "");
				int length2 = newStr.length();
				// 次數 = (length1 - length2)/選項長度 (因為選項不只一個字)
				int count = (length1 - length2) / option.length();
				answerCountMap.put(option, count);
			}
			//將計算結果依據題號放入最後統計的quziIdAndAnsCountMap
			//item.getKey()→ 題目編號, answerCountMap → 選項和次數
			quziIdAndAnsCountMap.put(item.getKey(), answerCountMap);
		}
// Step 5：回傳成功訊息即資料
		return new StatisticsRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage(), quziIdAndAnsCountMap);
	}

//	▼▼▼▼▼▼▼▼▼▼ 抽方法!!! ▼▼▼▼▼▼▼▼▼▼
//	▼▼▼▼▼▼▼▼▼▼ 抽方法!!! ▼▼▼▼▼▼▼▼▼▼
//	▼▼▼▼▼▼▼▼▼▼ 抽方法!!! ▼▼▼▼▼▼▼▼▼▼

	// 【輸入參數的檢查】
	private BaseRes checkParams(CreateOrUpdateReq req, boolean isCreate) {
// Step 1：檢查傳入的list是否有東西
		if (CollectionUtils.isEmpty(req.getQuizList())) {
			return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
		}
// Step 2：必填項目是否都有填
		for (Quiz item : req.getQuizList()) {
			if (item.getQuizId() <= 0 || item.getQuId() <= 0 || !StringUtils.hasText(item.getQuizName())
					|| item.getStartDate() == null || item.getEndDate() == null
					|| !StringUtils.hasText(item.getQuestion()) || !StringUtils.hasText(item.getType())) {
				return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
			}
		}

// Step 3：蒐集req中所有的quizId
// 原則上是一個req中所有的quizId都會一樣，但也有可能其中一比資料的quizId是錯的(一張問卷多個問題)
// 為保證所有資料的正確性，先去蒐集req中所有的quizId後再做比對

// <方法一>
// 使用List：先宣告一個新的list來放入所有的quizId後再檢查是否有不同的值
//		List<Integer> quizIds = new ArrayList<>();
//		for (Quiz item : req.getQuizList()) {
//			//判斷式：如果quizIds還沒有包含從quizList提出的Id，則加入quizIds
//			if (!quizIds.contains(item.getQuizId())) {
//				quizIds.add(item.getQuizId());
//			}
//		}

// 26行和41行的其實是一樣的for迴圈，因此其實可以合併，這裡因為有註解所以不這麼做

// <方法二>
// 使用Set：相較於List允許重複值的存在，set不允許，如果已存在相同的值就不會新增
		Set<Integer> quizIds = new HashSet<>();
		for (Quiz item : req.getQuizList()) {
			quizIds.add(item.getQuizId());
		}

		// 判斷式：如果quizIds最終的大小不等於1，代表有出現不同的quziId，代表傳入的資料有誤(問卷已存在)
		if (quizIds.size() != 1) {
			return new BaseRes(RtnCode.QUIZ_ID_DOES_NOT_MATCH.getCode(), RtnCode.QUIZ_ID_DOES_NOT_MATCH.getMessage());
		}
// Step 4：檢查問題編號是否重複，正常來說不會重複
		Set<Integer> quIds = new HashSet<>();
		for (Quiz item : req.getQuizList()) {
			quIds.add(item.getQuId());
		}
		// 判斷式：如果quIds的最終大小不等於quizList，代表有編號重複
		if (quIds.size() != req.getQuizList().size()) {
			return new BaseRes(RtnCode.DUPLICATED_QUESTION_ID.getCode(), RtnCode.DUPLICATED_QUESTION_ID.getMessage());
		}

// Step 5：檢查時間範圍是否正確，即開始時間不可大於結束時間
		for (Quiz item : req.getQuizList())
			if (item.getStartDate().isAfter(item.getEndDate())) {
				return new BaseRes(RtnCode.TIME_FORMAT_ERROR.getCode(), RtnCode.TIME_FORMAT_ERROR.getMessage());
			}
// Step 6：根據是否為create去做不同的操作
		// 如果是create，則檢查問卷是否已存在，若是就回傳錯誤訊息
		if (isCreate) {
			if (quizDao.existsByQuizId(req.getQuizList().get(0).getQuizId())) {
				return new BaseRes(RtnCode.QUIZ_ALREADY_EXISTS.getCode(), RtnCode.QUIZ_ALREADY_EXISTS.getMessage());
			}
			// 如果是update則確認是否能夠修改，即是否未發布或未開始，若不是則回傳錯誤訊息，若是就可以把原問卷刪除
		} else {
			if (!quizDao.existsByQuizIdAndPublishedFalseOrQuizIdAndStartDateAfter(req.getQuizList().get(0).getQuizId(),
					req.getQuizList().get(0).getQuizId(), LocalDate.now())) {
				return new BaseRes(RtnCode.QUIZ_IS_NOT_FOUND.getCode(), RtnCode.QUIZ_IS_NOT_FOUND.getMessage());
			}
			quizDao.deleteByQuizId(req.getQuizList().get(0).getQuizId());
		}
// Step 6：檢查全部完成，最後為求保險，根據前端傳回的是否要發布之布林值再次修改list中的published，然後再新增整張問卷並回傳成功訊息
		for (Quiz item : req.getQuizList()) {
			item.setPublished(req.isPublished());
		}
		quizDao.saveAll(req.getQuizList());
		return new BaseRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage());
	}

}
