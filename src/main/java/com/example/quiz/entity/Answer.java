package com.example.quiz.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "answer")
public class Answer {
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// ↑↑↑↑↑ 關於GeneratedValue標籤 ↑↑↑↑↑
	// 因為id欄位在DB是AI，所以要加上此標籤
	// GeneratedType.IDENTITY 是指主鍵的數字增長交由資料庫處理
	// BUT!!! 當Entity屬性的資料型態是Integer時，必須加
	//        當Entity屬性的資料型態是int時，非必須；但若要在新增資料(JPA的Save)後即時取得該筆資料的流水號，就要加上
	private int id;
	
	@Column(name = "name")
	private String name;

	@Column(name = "phone")
	private String phone;

	@Column(name = "email")
	private String email;

	@Column(name = "age")
	private int age;

	@Column(name = "quiz_id")
	private int quizId;

	@Column(name = "qu_id")
	private int quId;

	@Column(name = "answer")
	private String answer = "";
	
	public Answer() {
		super();
	}

	public Answer(String name, String phone, String email, int age, int quizId, int quId, String answer) {
		super();
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.age = age;
		this.quizId = quizId;
		this.quId = quId;
		this.answer = answer;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getQuizId() {
		return quizId;
	}

	public void setQuizId(int quizId) {
		this.quizId = quizId;
	}

	public int getQuId() {
		return quId;
	}

	public void setQuId(int quId) {
		this.quId = quId;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

}
