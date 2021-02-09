package com.madana.common.utils.datacollection.questionaires.questions;

import com.madana.common.utils.datacollection.questionaires.Answer;
import com.madana.common.utils.datacollection.questionaires.answers.FreeTextAnswer;

public class LeadingQuestion extends ClosedEndedQuestion{
	public LeadingQuestion(String questionText) {
		super(questionText);
		// TODO Auto-generated constructor stub
	}
	public LeadingQuestion() {
		super();
		}
	@Override
	public Answer getAnswerType() {
		// TODO Auto-generated method stub
		return new FreeTextAnswer();
	}
}
