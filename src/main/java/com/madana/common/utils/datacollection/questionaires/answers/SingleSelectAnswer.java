package com.madana.common.utils.datacollection.questionaires.answers;

import java.util.List;

import com.madana.common.utils.datacollection.questionaires.Answer;

public class SingleSelectAnswer extends Answer{
	
	List<String> options;

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}

}
