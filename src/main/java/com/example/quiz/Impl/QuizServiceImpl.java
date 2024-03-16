package com.example.quiz.Impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.example.quiz.constants.RtnCode;
import com.example.quiz.entity.Answer;
import com.example.quiz.entity.Quiz;
import com.example.quiz.ifs.QuizService;
import com.example.quiz.repository.AnswerDao;
import com.example.quiz.repository.QuizDao;
import com.example.quiz.vo.CreateOrUpdateReq;
import com.example.quiz.vo.DeleteQuizReq;
import com.example.quiz.vo.DeleteQusReq;
import com.example.quiz.vo.SearchReq;
import com.example.quiz.vo.AnswerReq;
import com.example.quiz.vo.BaseRes;
import com.example.quiz.vo.SearchRes;
import com.example.quiz.vo.StatisticsRes;

@Service
public class QuizServiceImpl implements QuizService {

	@Autowired
	private QuizDao quizDao;

	@Autowired
	private AnswerDao answerDao;

	@Override
	public BaseRes create(CreateOrUpdateReq req) {
		return checkParams(req, true);
	}

	@Override
	public SearchRes search(SearchReq req) {
		if (!StringUtils.hasText(req.getQuizName())) {
			req.setQuizName("");
		}

		if (req.getStartDate() == null) {
			req.setStartDate(LocalDate.of(1970, 1, 1));
		}

		if (req.getEndDate() == null) {
			req.setEndDate(LocalDate.of(9999, 12, 31));
		}

		if (req.isBackend()) {
			return new SearchRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage(),
					quizDao.findByQuizNameContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqual(
							req.getQuizName(), req.getStartDate(), req.getEndDate()));
		} else {
			return new SearchRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage(),
					quizDao.findByQuizNameContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqualAndPublishedTrue(
							req.getQuizName(), req.getStartDate(), req.getEndDate()));
		}
	}

	@Override
	public BaseRes deleteQuiz(DeleteQuizReq req) {
		if (CollectionUtils.isEmpty(req.getQuizIds())) {
			return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
		}
		quizDao.deleteByQuizIdInAndPublishedFalseOrQuizIdInAndStartDateAfter(req.getQuizIds(), req.getQuizIds(),
				LocalDate.now());
		return new BaseRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage());
	}

	@Override
	public BaseRes deleteQuestions(DeleteQusReq req) {
		if (req.getQuizId() <= 0 || CollectionUtils.isEmpty(req.getQuIds())) {
			return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
		}
		List<Quiz> res = quizDao.findByQuizIdAndPublishedFalseOrQuizIdAndStartDateAfterOrderByQuId(req.getQuizId(),
				req.getQuizId(), LocalDate.now());
		if (res.isEmpty()) {
			return new BaseRes(RtnCode.QUIZ_IS_NOT_FOUND.getCode(), RtnCode.QUIZ_IS_NOT_FOUND.getMessage());
		}
		int j = 0;
		for (Integer item : req.getQuIds()) {
			res.remove(item - 1 - j);
			j++;
		}
		for (int i = 0; i < res.size(); i++) {
			res.get(i).setQuId(i + 1);
		}
		List<Quiz> retainList = new ArrayList<>();
		for (Quiz item : res) {
			if (!req.getQuIds().contains(item.getQuId())) {
				retainList.add(item);
			}
		}
		for (int i = 0; i < res.size(); i++) {
			res.get(i).setQuId(i + 1);
		}
		quizDao.deleteByQuizId(req.getQuizId());
		if (!retainList.isEmpty()) {
			quizDao.saveAll(res);
		}
		if (!retainList.isEmpty()) {
			quizDao.saveAll(retainList);
		}
		return new BaseRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage());
	}

	@Override
	public BaseRes update(CreateOrUpdateReq req) {
		return checkParams(req, false);
	}

	@Override
	public BaseRes answer(AnswerReq req) {
		if (CollectionUtils.isEmpty(req.getAnswerList())) {
			return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
		}
		for (Answer item : req.getAnswerList()) {
			if (!StringUtils.hasText(item.getName()) || !StringUtils.hasText(item.getPhone())
					|| !StringUtils.hasText(item.getEmail()) || item.getQuizId() <= 0 || item.getQuId() <= 0
					|| item.getAge() < 0) {
				return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
			}
		}
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
		List<Integer> res = quizDao.findQuIdsByQuizIdAndNecessaryTrue(req.getAnswerList().get(0).getQuizId());
		for (Answer item : req.getAnswerList()) {
			if (res.contains(item.getQuId()) && !StringUtils.hasText(item.getAnswer())) {
				return new BaseRes(RtnCode.QUESTION_IS_NOT_ANSWERED.getCode(),
						RtnCode.QUESTION_IS_NOT_ANSWERED.getMessage());
			}
		}
		if (answerDao.existsByQuizIdAndEmail(req.getAnswerList().get(0).getQuizId(),
				req.getAnswerList().get(0).getEmail())) {
			return new BaseRes(RtnCode.DUPLICATED_QUIZ_ANSWER.getCode(), RtnCode.DUPLICATED_QUIZ_ANSWER.getMessage());
		}
		answerDao.saveAll(req.getAnswerList());
		return new BaseRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage());
	}

	@Override
	public StatisticsRes statistics(int quizId) {
		if (quizId <= 0) {
			return new StatisticsRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
		}
		List<Quiz> quizList = quizDao.findByQuizId(quizId);
		List<Integer> quIdList = new ArrayList<>();
		for (Quiz item : quizList) {
			if (StringUtils.hasText(item.getOptions())) {
				quIdList.add(item.getQuId());
			}
		}
		List<Answer> answerList = answerDao.findByQuizIdOrderByQuId(quizId);

//<方法一：String.stream().filter()+Lambda寫法，直接把所有答案丟進一個List中，用filter過濾每個被回答的選項然後直接算該list的長度>		
/*		List<String> answerString = new ArrayList<>();
		for(Answer item : answerList) {
			if(quIdList.contains(item.getQuId())) {
				answerString.add(item.getAnswer());
				continue;
			}
			continue;
		}
		Map<Integer, Map<String, Integer>> quIdAnswerCountMap = new HashMap<>();
		for(Integer item : quIdList) {
			String[] optionList = quizList.get(item - 1).getOptions().split(";");
			Map<String, Integer> answerCount = new HashMap<>();
			for(String option : optionList) {
				List<String> answerEach = answerString.stream().filter(line -> option.equals(line)).collect(Collectors.toList());
				int count = answerEach.size();
				answerCount.put(option, count);
			}
			quIdAnswerCountMap.put(item, answerCount);
		}
		return new StatisticsRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage(), quIdAnswerCountMap);
*/
//<方法二：用替換的方式將要計算的選項代換成空字串，然後用字串長度的變化算出次數>			
		Map<Integer, String> quIdAnswerMap = new HashMap<>();
		for (Answer item : answerList) {
			if (quIdList.contains(item.getQuId())) {
				if (quIdAnswerMap.containsKey(item.getQuId())) {
					String str = quIdAnswerMap.get(item.getQuId());
					str += item.getAnswer();
					quIdAnswerMap.put(item.getQuId(), str);
				} else {
					quIdAnswerMap.put(item.getQuId(), item.getAnswer());
				}
			}
		}	
		Map<Integer, Map<String, Integer>> quziIdAndAnsCountMap = new HashMap<>();
		for (Entry<Integer, String> item : quIdAnswerMap.entrySet()) {
			Map<String, Integer> answerCountMap = new HashMap<>();
			String[] optionList = quizList.get(item.getKey() - 1).getOptions().split(";");
			for (String option : optionList) {
				String newStr = item.getValue();
				int length1 = newStr.length();
				newStr = newStr.replace(option, "");
				int length2 = newStr.length();
				int count = (length1 - length2) / option.length();
				answerCountMap.put(option, count);
			}
			quziIdAndAnsCountMap.put(item.getKey(), answerCountMap);
		} 		
		return new StatisticsRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage(), quziIdAndAnsCountMap);
	}

	/* ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ 私有方法們 ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ */

	private BaseRes checkParams(CreateOrUpdateReq req, boolean isCreate) {
		if (CollectionUtils.isEmpty(req.getQuizList())) {
			return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
		}
		for (Quiz item : req.getQuizList()) {
			if (item.getQuizId() == 0 || item.getQuId() == 0 || !StringUtils.hasText(item.getQuizName())
					|| item.getStartDate() == null || item.getEndDate() == null
					|| !StringUtils.hasText(item.getQuestion()) || !StringUtils.hasText(item.getType())) {
				return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
			}
		}

		Set<Integer> quizIds = new HashSet<>();
		Set<Integer> quIds = new HashSet<>();
		for (Quiz item : req.getQuizList()) {
			quizIds.add(item.getQuizId());
			quIds.add(item.getQuId());
		}
		if (quizIds.size() != 1) {
			return new BaseRes(RtnCode.QUIZ_ID_DOES_NOT_MATCH.getCode(), RtnCode.QUIZ_ID_DOES_NOT_MATCH.getMessage());
		}
		if (quIds.size() != req.getQuizList().size()) {
			return new BaseRes(RtnCode.DUPLICATED_QUESTION_ID.getCode(), RtnCode.DUPLICATED_QUESTION_ID.getMessage());
		}

		for (Quiz item : req.getQuizList())
			if (item.getStartDate().isAfter(item.getEndDate())) {
				return new BaseRes(RtnCode.TIME_FORMAT_ERROR.getCode(), RtnCode.TIME_FORMAT_ERROR.getMessage());
			}

		if (isCreate) {
			if (quizDao.existsByQuizId(req.getQuizList().get(0).getQuizId())) {
				return new BaseRes(RtnCode.QUIZ_ALREADY_EXISTS.getCode(), RtnCode.QUIZ_ALREADY_EXISTS.getMessage());
			}
		} else {
			if (!quizDao.existsByQuizIdAndPublishedFalseOrQuizIdAndStartDateAfter(req.getQuizList().get(0).getQuizId(),
					req.getQuizList().get(0).getQuizId(), LocalDate.now())) {
				return new BaseRes(RtnCode.QUIZ_IS_NOT_FOUND.getCode(), RtnCode.QUIZ_IS_NOT_FOUND.getMessage());
			}
			quizDao.deleteByQuizId(req.getQuizList().get(0).getQuizId());
		}

		for (Quiz item : req.getQuizList()) {
			item.setPublished(req.isPublished());
		}
		quizDao.saveAll(req.getQuizList());
		return new BaseRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage());
	}

}
