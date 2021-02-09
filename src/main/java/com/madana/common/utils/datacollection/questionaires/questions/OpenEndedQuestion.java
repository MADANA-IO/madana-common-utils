package com.madana.common.utils.datacollection.questionaires.questions;

import com.madana.common.utils.datacollection.questionaires.Answer;
import com.madana.common.utils.datacollection.questionaires.Question;
import com.madana.common.utils.datacollection.questionaires.answers.FreeTextAnswer;

public class OpenEndedQuestion extends Question
{

	public OpenEndedQuestion(String questionText) {
		super(questionText);
		// TODO Auto-generated constructor stub
	}
	public OpenEndedQuestion() {
		super();
		}
	@Override
	public Answer getAnswerType() {
		// TODO Auto-generated method stub
		return new FreeTextAnswer();
	}
}
