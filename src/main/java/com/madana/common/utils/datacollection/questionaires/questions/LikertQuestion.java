package com.madana.common.utils.datacollection.questionaires.questions;

import com.madana.common.utils.datacollection.questionaires.Answer;
import com.madana.common.utils.datacollection.questionaires.answers.SingleSelectAnswer;

public class LikertQuestion  extends ClosedEndedQuestion{

	public LikertQuestion(String questionText) {
		super(questionText);
		// TODO Auto-generated constructor stub
	}
	public LikertQuestion() {
		super();
		}
	@Override
	public Answer getAnswerType() {
		// TODO Auto-generated method stub
		return new SingleSelectAnswer();
	}


}
