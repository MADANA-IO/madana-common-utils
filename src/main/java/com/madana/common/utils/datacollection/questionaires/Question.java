package com.madana.common.utils.datacollection.questionaires;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.madana.common.utils.datacollection.questionaires.questions.QuestionInterface;
@JsonIgnoreProperties(value = { "answerType" })
public  class Question implements QuestionInterface{
	
	String text;
	Answer answer;
	String type;


	public Question()
	{
		answer = getAnswerType();
		type= getType();
	}
	public  Question(String text)
	{
		answer = getAnswerType();
		type= getType();
		setText(text);
	}
	
	public Answer getAnswerType() {
		return answer;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Answer getAnswer() {
		return answer;
	}

	public void setAnswer(Answer answer) {
		this.answer = answer;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

}
