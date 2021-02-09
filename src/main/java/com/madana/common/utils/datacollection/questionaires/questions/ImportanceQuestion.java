package com.madana.common.utils.datacollection.questionaires.questions;

import com.madana.common.utils.datacollection.questionaires.Answer;
import com.madana.common.utils.datacollection.questionaires.answers.SingleSelectAnswer;

public class ImportanceQuestion extends ClosedEndedQuestion{

	public ImportanceQuestion(String questionText) {
		super(questionText);
		// TODO Auto-generated constructor stub
	}
	public ImportanceQuestion() {
		super();
		}
	@Override
	public Answer getAnswerType() {
		// TODO Auto-generated method stub
		return new SingleSelectAnswer();
	}

}
