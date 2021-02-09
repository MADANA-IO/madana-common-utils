package com.madana.common.utils.datacollection.questionaires;

import java.util.Map;

public class QuestionResult {
	String question;
	String answerType;
	Map<String, Integer> counter;
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswerType() {
		return answerType;
	}
	public void setAnswerType(String answerType) {
		this.answerType = answerType;
	}
	public Map<String, Integer> getCounter() {
		return counter;
	}
	public void setCounter(Map<String, Integer> counter) {
		this.counter = counter;
	}
}
