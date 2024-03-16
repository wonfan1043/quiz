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
// �������������������� ���b�ˬd ��������������������
		return checkParams(req, true);
//		if(res != null) {
//			return res;
//		}
//// Step 6�G�ˬd�ݨ��O�_�w�s�b
//		if (quizDao.existsByQuizId(req.getQuizList().get(0).getQuizId())) {
//			return new BaseRes(RtnCode.QUIZ_ALREADY_EXISTS.getCode(), RtnCode.QUIZ_ALREADY_EXISTS.getMessage());
//		}
//
//// Step 7 :���D�O�I�A�ھګe�ݶǦ^���O�_�n�o�������L�ȦA���ק�list����published�A�M��A�s�W��i�ݨ��æ^�Ǧ��\�T��
//		for (Quiz item : req.getQuizList()) {
//			item.setPublished(req.isPublished());
//		}
//		quizDao.saveAll(req.getQuizList());
//		return new BaseRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage());
	}

	public SearchRes search(SearchReq req) {
		// �p�G�S����J�ݨ��W�ٴN�a�Ŧr��Acontaining�[�W�Ŧr��N��j�M���Fnull�H�~���Ҧ����
		if (!StringUtils.hasText(req.getQuizName())) {
			req.setQuizName(""); 
		}
		// �p�G�S����J�}�l�ɶ��N���@�ӫܦ��ܦ����ɶ�
		if (req.getStartDate() == null) {
			req.setStartDate(LocalDate.of(1970, 1, 1));
		}
		// �p�G�S����J�����ɶ��N���@�ӫܱܱ߫ߪ��ɶ�
		if (req.getEndDate() == null) {
			req.setEndDate(LocalDate.of(9999, 12, 31));
		}

		// ����ơG�����e�x���x�A�e�x�ϥΪ̷j�M���ܤ���ݨ쥼�o�����ݨ��A��x�ϥΪ̫h��������ݨ�
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
		// List�ݩ�Collection�A�ҥH�i�H�ΥL���y�k�P�ɧP�_quizIds�O�_��null�ΪŶ��X
		// �P�_���O�Ū��Y�i�A�Y�KquizIds���t�Ƥ]�S���Y�A�u�n���@�ӥ��ƴN�ȱo�i�JDB�`���
		if (CollectionUtils.isEmpty(req.getQuizIds())) {
			return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
		}
		quizDao.deleteByQuizIdInAndPublishedFalseOrQuizIdInAndStartDateAfter(req.getQuizIds(), req.getQuizIds(), LocalDate.now());
		return new BaseRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage());
	}

	public BaseRes deleteQuestions(DeleteQusReq req) {
		// 1. �ˬd�ǤJ����ƬO�_���~
		// 2. ���X��i�ݨ��A�R�����n����� or �u�O�d�n�d�U����ơA�p�G�䤣��ݨ��N�^�ǿ��~�T��
		// 3. �R����Ʈw������Ӱݨ�
		// 4. �s�J��i�B�z�n���ݨ�
		// 5. �^�Ǧ��\�T��

//1. �ˬd�ǤJ����ƬO�_���~
		if (req.getQuizId() <= 0 || CollectionUtils.isEmpty(req.getQuIds())) {
			return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
		}
//2. ���X��i�ݨ��A�R�����n�����(��k�@) or �u�O�d�n�d�U�����(��k�G)�A�p�G�䤣��ݨ��N�^�ǿ��~�T��
		List<Quiz> res = quizDao.findByQuizIdAndPublishedFalseOrQuizIdAndStartDateAfterOrderByQuId(req.getQuizId(), req.getQuizId(),
				LocalDate.now());
		if (res.isEmpty()) {
			return new BaseRes(RtnCode.QUIZ_IS_NOT_FOUND.getCode(), RtnCode.QUIZ_IS_NOT_FOUND.getMessage());
		}
// <��k�@> �R�����w���
		// quids = 1, 4
		// quids = 1 �� j = 0 & item = 1 �� item - 1 - j = 1 - 1 - 0 = 0 �� �R��res��index 0
		// quids = 4 �� j = 1 & item = 1 �� item - 1 - j = 4 - 1 - 1 = 0 �� �R��res��index 2
		int j = 0;
		for (Integer item : req.getQuIds()) {
			res.remove(item - 1 - j);
			j++;
		}
		// �R����ƫ᭫�s�Ƨ�
		for (int i = 0; i < res.size(); i++) {
			res.get(i).setQuId(i + 1);
		}
//<��k�G> �N�n�O�d����ƥ[��t�@��List
		// �ŧi�@�ӥΨө�n�O�d����ƪ�List �� retainList
		// �p�GquIds�����]�tres��quId�h�[�JretainList �� �Y�O�d���b�R���M��quIds�������
		List<Quiz> retainList = new ArrayList<>();
		for (Quiz item : res) {
			if (!req.getQuIds().contains(item.getQuId())) {
				retainList.add(item);
			}
		}
		// �R����ƫ᭫�s�Ƨ�
		for (int i = 0; i < res.size(); i++) {
			res.get(i).setQuId(i + 1);
		}
//3. �R����Ʈw������i�ݨ�
		quizDao.deleteByQuizId(req.getQuizId());
//4. �s�J��i�B�z�n���ݨ��A���e�D�O�R�����٦��F��i�H�s
//<��k�@����>
		if (!retainList.isEmpty()) {
			quizDao.saveAll(res);
		}
//<��k�G����>
		if (!retainList.isEmpty()) {
			quizDao.saveAll(retainList);
		}
//5. �^�Ǧ��\�T��
		return new BaseRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage());
	}

	public BaseRes update(CreateOrUpdateReq req) {
		return checkParams(req, false);
////1. �ˬd�ǤJ����ƬO�_����
//		BaseRes res = checkParams(req);
//		if(res != null) {
//			return res;
//		}
////2. �ˬd�ǤJ����ƬO�_���P�@�i�ݨ��A�]���@���u��ק�@�i�ݨ�
//		Set<Integer> quizIdSet = new HashSet<>();
//		for (Quiz item : req.getQuizList()) {
//			quizIdSet.add(item.getQuId());
//		}
//		if (quizIdSet.size() != 1) {
//			return new BaseRes(RtnCode.QUIZ_ID_ERROR.getCode(), RtnCode.QUIZ_ID_ERROR.getMessage());
//		}
////3. �ˬd�ݨ��O�_����ק�A�Y�O�_�����o���Υ��}�l
//		if (!quizDao.existsByQuizIdAndPublishedFalseOrQuizIdAndStartDateAfter(req.getQuizList().get(0).getQuizId(),
//				req.getQuizList().get(0).getQuizId(), LocalDate.now())) {
//			return new BaseRes(RtnCode.QUIZ_IS_NOT_FOUND.getCode(), RtnCode.QUIZ_IS_NOT_FOUND.getMessage());
//		}
////4. �R����Ʈw������i�ݨ�
//		quizDao.deleteByQuizId(req.getQuizList().get(0).getQuizId());
////5. ���D�O�I�A�ھګe�ݶǦ^���O�_�n�o�������L�ȦA���ק�list����published�A�M��A�s�W��i�ݨ��æ^�Ǧ��\�T��
//		for (Quiz item : req.getQuizList()) {
//			item.setPublished(req.isPublished());
//		}
//		quizDao.saveAll(req.getQuizList());
//		return new BaseRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage());
	}

	public BaseRes answer(AnswerReq req) {
// Step 1�G�ˬd�ǤJ��answerList���S���F��
		if (CollectionUtils.isEmpty(req.getAnswerList())) {
			return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
		}
// Step 2�G�ˬd�ǤJ��answerList���ѼƬO�_OK
		for (Answer item : req.getAnswerList()) {
			if (!StringUtils.hasText(item.getName()) || !StringUtils.hasText(item.getPhone())
					|| !StringUtils.hasText(item.getEmail()) || item.getQuizId() <= 0 || item.getQuId() <= 0
					|| item.getAge() < 0) {
				return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
			}
		}
// Step 3�G�ˬdquizId���@�˸�quId��������
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

// Step 4�G�ˬd���񶵥جO�_������
		List<Integer> res = quizDao.findQuIdsByQuizIdAndNecessaryTrue(req.getAnswerList().get(0).getQuizId());
//<��k�@> 		
		for (Answer item : req.getAnswerList()) {
			if (res.contains(item.getQuId()) && !StringUtils.hasText(item.getAnswer())) {
				return new BaseRes(RtnCode.QUESTION_IS_NOT_ANSWERED.getCode(),
						RtnCode.QUESTION_IS_NOT_ANSWERED.getMessage());
			}
		}
//<��k�G>
		for (int item : res) {
			Answer ans = req.getAnswerList().get(item - 1);
			if (!StringUtils.hasText(ans.getAnswer())) {
				return new BaseRes(RtnCode.QUESTION_IS_NOT_ANSWERED.getCode(),
						RtnCode.QUESTION_IS_NOT_ANSWERED.getMessage());
			}
		}
// Step 5�G�T�{�P�@��email���୫�ƶ�g�P�@�i�ݨ�
		if (answerDao.existsByQuizIdAndEmail(req.getAnswerList().get(0).getQuizId(),
				req.getAnswerList().get(0).getEmail())) {
			return new BaseRes(RtnCode.DUPLICATED_QUIZ_ANSWER.getCode(), RtnCode.DUPLICATED_QUIZ_ANSWER.getMessage());
		}
// Step 6�G�ˬd�����A�x�s�iDB�æ^�Ǧ��\�T��
		answerDao.saveAll(req.getAnswerList());
		return new BaseRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage());
	}

	public StatisticsRes statistics(int quizId) {
// Step 1�G�ˬd�ѼƬO�_���T
		if (quizId <= 0) {
			return new StatisticsRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
		}
// Step 2�G��quiz��ݨ����X����o���D��type���D²����������i�t�@��List�̭�
		// �ŧi�@��Quiz��list�A�M���quizId�����i�ݨ����X��
		List<Quiz> quizList = quizDao.findByQuizId(quizId);
		// �ŧi�@�ө��D�ؽs����Integer List�Ψө�Ҧ�����D���D�ؽs��
		List<Integer> quIdList = new ArrayList<>();
		for (Quiz item : quizList) {
			// �P�_���G�p�Goption�O�Ū��N����D�S���ﶵ�A�Y��²���D�A�]���n�[�J���Ooption�D�ťժ��D�ؽs��
			if (StringUtils.hasText(item.getOptions())) {
				quIdList.add(item.getQuId());
			}
		}
// Step 3�G�̧ǧ�C�D�U�۪����׼��X�ӡA�M��ꦨ�r��
		// �ŧi�@��Answer��List�A����ӱi�ݨ����Ҧ��^�����X�A�åB�n��Ҧ����ר̾�quId�Ʀn
		List<Answer> answerList = answerDao.findByQuizIdOrderByQuId(quizId);
		// �ŧi�@�ӧ���D�s����Ҧ��^����mapping��Map
		// �Ψӧ�C�D����D���^���U�ۦꦨ�@�Ӧr��A�Y�@�ӿﶵ/���׷|���@�Ӥj�r��
		Map<Integer, String> quIdAnswerMap = new HashMap<>();
		// �]���n��Ҧ��^���A�ҥH���Ｔ�X�����װ��M��
		for (Answer item : answerList) {
			// �P�_���G�p�G����D�s����List(quIdList)���t���q�Ҧ��^����List(answerList)�����X��quId
			//       �� ���D������D�A�ݭn���r��answer���걵
			if (quIdList.contains(item.getQuId())) {
				// �P�_���G�p�G�걵�U�D�ص��ת�quIdAnswerMap��Key�ȥ]�t�F�qanswerList�����X��quId
				//       �� ���D�w�g����JMap���A�u�ݭn���r��걵
				if (quIdAnswerMap.containsKey(item.getQuId())) {
					// �z�Lkey��(quId)���o������value(answer)
					String str = quIdAnswerMap.get(item.getQuId());
					// ��즳��value(answer)�M�o�����o��value(answer)���걵�M���svalue
					str += item.getAnswer();
					// ���s�n��value(�걵�n��answer)��^�h�쥻��key���U
					quIdAnswerMap.put(item.getQuId(), str);
				} else {
					// �P�_���G�p�GquIdAnswerMap��Key�ȩ|���]�t�qanswerList�����X��quId�A�N��Map���٨S�����D�A���N�����s�W
					quIdAnswerMap.put(item.getQuId(), item.getAnswer());
				}
			}
		}
// Step 4�G�p��C�D�C�ӿﶵ������ �� ���n���r�ꤤ���U�ﶵ���O�W�ߥΪŦr��N���A�A�Ϋe����׮t�h�p�⦸��
		// new�@��Map�Ψ�mapping�u���D�s�� - �ﶵ - ���ơv�A�Ymapping���D�s����U�誺answerCountMap
		// �o��Map����ƫ��A��answerCountMap����ƫ��AMap<String, Integer>
		// p.s. �o�̪����Ƿ|�O���g�Xfor�j��B�ŧi�XanswerCountMap��A�~�^�ӫŧiquziIdAndAnsCountMap
		Map<Integer, Map<String, Integer>> quziIdAndAnsCountMap = new HashMap<>();
		// �]���n�p��C�ӿﶵ�Q�諸���ơA�ҥH����quIdAnswerMap���M��
		// �uMap�W��.entrySet()�v �� ��n�M������H�qMap�নentrySet�A�o�˴N�i�H�������oMap����key�Mvalue
		for (Entry<Integer, String> item : quIdAnswerMap.entrySet()) {
			// new�@��Map�ΨӺ�C�ӵ��ת����ơA�Ykey(�ﶵ/����)�Mvalue(����)��mapping
			Map<String, Integer> answerCountMap = new HashMap<>();
			// �����o�C�Ӱ��D���ﶵ�G
			// ���u�n���X���H�諸�ﶵ �� ��quIdAnswerMap����quId�h����
			// Entry<Integer, String> item : quIdAnswerMap.entrySet()
			//          Key       Value
			// item = �D�ؽs�� + ��n�����צr��
			String[] optionList = quizList.get(item.getKey() - 1).getOptions().split(";");
			//String[] optionList =     ��U�ӿﶵ��J�W��optionList���r��}�C��
			//quizList.                 ��ݨ���ƥ]
			//get(item.getKey() - 1).   ���X(index)����ơ�item.getKey()�����H�諸�ﶵ���D����1���]��quizList����ƬO�qindex 0�}�l�A�]���C�@�D���s��b�D��-1��index��
			//getOptions().             �~����X���D���ﶵ
			//split(";");               �̫�L���Τ������}���P�ﶵ
			
			// ��foreach�j���optionList���M���H�p�����
			for (String option : optionList) {
				// �qquIdAnswerMap�����X��n���r��íp������
				String newStr = item.getValue();
				int length1 = newStr.length();
				// �̧ǧ�ﶵ�����Ŧr����^�h�íp�����
				newStr = newStr.replace(option, "");
				int length2 = newStr.length();
				// ���� = (length1 - length2)/�ﶵ���� (�]���ﶵ���u�@�Ӧr)
				int count = (length1 - length2) / option.length();
				answerCountMap.put(option, count);
			}
			//�N�p�⵲�G�̾��D����J�̫�έp��quziIdAndAnsCountMap
			//item.getKey()�� �D�ؽs��, answerCountMap �� �ﶵ�M����
			quziIdAndAnsCountMap.put(item.getKey(), answerCountMap);
		}
// Step 5�G�^�Ǧ��\�T���Y���
		return new StatisticsRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage(), quziIdAndAnsCountMap);
	}

//	�������������������� ���k!!! ��������������������
//	�������������������� ���k!!! ��������������������
//	�������������������� ���k!!! ��������������������

	// �i��J�Ѽƪ��ˬd�j
	private BaseRes checkParams(CreateOrUpdateReq req, boolean isCreate) {
// Step 1�G�ˬd�ǤJ��list�O�_���F��
		if (CollectionUtils.isEmpty(req.getQuizList())) {
			return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
		}
// Step 2�G���񶵥جO�_������
		for (Quiz item : req.getQuizList()) {
			if (item.getQuizId() <= 0 || item.getQuId() <= 0 || !StringUtils.hasText(item.getQuizName())
					|| item.getStartDate() == null || item.getEndDate() == null
					|| !StringUtils.hasText(item.getQuestion()) || !StringUtils.hasText(item.getType())) {
				return new BaseRes(RtnCode.PARAM_ERROR.getCode(), RtnCode.PARAM_ERROR.getMessage());
			}
		}

// Step 3�G�`��req���Ҧ���quizId
// ��h�W�O�@��req���Ҧ���quizId���|�@�ˡA���]���i��䤤�@���ƪ�quizId�O����(�@�i�ݨ��h�Ӱ��D)
// ���O�ҩҦ���ƪ����T�ʡA���h�`��req���Ҧ���quizId��A�����

// <��k�@>
// �ϥ�List�G���ŧi�@�ӷs��list�ө�J�Ҧ���quizId��A�ˬd�O�_�����P����
//		List<Integer> quizIds = new ArrayList<>();
//		for (Quiz item : req.getQuizList()) {
//			//�P�_���G�p�GquizIds�٨S���]�t�qquizList���X��Id�A�h�[�JquizIds
//			if (!quizIds.contains(item.getQuizId())) {
//				quizIds.add(item.getQuizId());
//			}
//		}

// 26��M41�檺���O�@�˪�for�j��A�]�����i�H�X�֡A�o�̦]�������ѩҥH���o��

// <��k�G>
// �ϥ�Set�G�۸���List���\���ƭȪ��s�b�Aset�����\�A�p�G�w�s�b�ۦP���ȴN���|�s�W
		Set<Integer> quizIds = new HashSet<>();
		for (Quiz item : req.getQuizList()) {
			quizIds.add(item.getQuizId());
		}

		// �P�_���G�p�GquizIds�̲ת��j�p������1�A�N���X�{���P��quziId�A�N��ǤJ����Ʀ��~(�ݨ��w�s�b)
		if (quizIds.size() != 1) {
			return new BaseRes(RtnCode.QUIZ_ID_DOES_NOT_MATCH.getCode(), RtnCode.QUIZ_ID_DOES_NOT_MATCH.getMessage());
		}
// Step 4�G�ˬd���D�s���O�_���ơA���`�ӻ����|����
		Set<Integer> quIds = new HashSet<>();
		for (Quiz item : req.getQuizList()) {
			quIds.add(item.getQuId());
		}
		// �P�_���G�p�GquIds���̲פj�p������quizList�A�N���s������
		if (quIds.size() != req.getQuizList().size()) {
			return new BaseRes(RtnCode.DUPLICATED_QUESTION_ID.getCode(), RtnCode.DUPLICATED_QUESTION_ID.getMessage());
		}

// Step 5�G�ˬd�ɶ��d��O�_���T�A�Y�}�l�ɶ����i�j�󵲧��ɶ�
		for (Quiz item : req.getQuizList())
			if (item.getStartDate().isAfter(item.getEndDate())) {
				return new BaseRes(RtnCode.TIME_FORMAT_ERROR.getCode(), RtnCode.TIME_FORMAT_ERROR.getMessage());
			}
// Step 6�G�ھڬO�_��create�h�����P���ާ@
		// �p�G�Ocreate�A�h�ˬd�ݨ��O�_�w�s�b�A�Y�O�N�^�ǿ��~�T��
		if (isCreate) {
			if (quizDao.existsByQuizId(req.getQuizList().get(0).getQuizId())) {
				return new BaseRes(RtnCode.QUIZ_ALREADY_EXISTS.getCode(), RtnCode.QUIZ_ALREADY_EXISTS.getMessage());
			}
			// �p�G�Oupdate�h�T�{�O�_����ק�A�Y�O�_���o���Υ��}�l�A�Y���O�h�^�ǿ��~�T���A�Y�O�N�i�H���ݨ��R��
		} else {
			if (!quizDao.existsByQuizIdAndPublishedFalseOrQuizIdAndStartDateAfter(req.getQuizList().get(0).getQuizId(),
					req.getQuizList().get(0).getQuizId(), LocalDate.now())) {
				return new BaseRes(RtnCode.QUIZ_IS_NOT_FOUND.getCode(), RtnCode.QUIZ_IS_NOT_FOUND.getMessage());
			}
			quizDao.deleteByQuizId(req.getQuizList().get(0).getQuizId());
		}
// Step 6�G�ˬd���������A�̫ᬰ�D�O�I�A�ھګe�ݶǦ^���O�_�n�o�������L�ȦA���ק�list����published�A�M��A�s�W��i�ݨ��æ^�Ǧ��\�T��
		for (Quiz item : req.getQuizList()) {
			item.setPublished(req.isPublished());
		}
		quizDao.saveAll(req.getQuizList());
		return new BaseRes(RtnCode.SUCCESS.getCode(), RtnCode.SUCCESS.getMessage());
	}

}
