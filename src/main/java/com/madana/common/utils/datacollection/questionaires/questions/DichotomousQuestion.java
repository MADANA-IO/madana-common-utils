package com.madana.common.utils.datacollection.questionaires.questions;

import com.madana.common.utils.datacollection.questionaires.Answer;
import com.madana.common.utils.datacollection.questionaires.answers.SingleSelectAnswer;

public class DichotomousQuestion  extends ClosedEndedQuestion{

	public DichotomousQuestion()
	{
		super();
	}
	public DichotomousQuestion(String questionText) {
		super(questionText);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Answer getAnswerType() {
		// TODO Auto-generated method stub
		return new SingleSelectAnswer();
	}
}
