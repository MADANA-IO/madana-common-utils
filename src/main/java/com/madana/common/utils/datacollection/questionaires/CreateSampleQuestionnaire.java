package com.madana.common.utils.datacollection.questionaires;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.madana.common.utils.datacollection.questionaires.answers.SingleSelectAnswer;
import com.madana.common.utils.datacollection.questionaires.questions.DichotomousQuestion;
import com.madana.common.utils.datacollection.questionaires.questions.ImportanceQuestion;
import com.madana.common.utils.datacollection.questionaires.questions.LeadingQuestion;
import com.madana.common.utils.datacollection.questionaires.questions.LikertQuestion;
import com.madana.common.utils.datacollection.questionaires.questions.OpenEndedQuestion;

public class CreateSampleQuestionnaire {

	public static void main(String[] args)
	{
		QuestionFactory factory = new QuestionFactory();
		System.out.println(factory.getAvailableQuestionTypes());
		Questionnaire questionnaire = new Questionnaire();
		List<Question> questions = new ArrayList<Question>();
		SingleSelectAnswer answer;
		List<String> answerOptions; 
		
	
		OpenEndedQuestion question = (OpenEndedQuestion) factory.createQuestion("OpenEndedQuestion", "What are you planning to buy from the supermarket today?");
		questions.add(question);
		
		LeadingQuestion question1 = (LeadingQuestion) factory.createQuestion("LeadingQuestion","What do you think about our communityhub?");
		questions.add(question1);

		
		
		
		answerOptions = new ArrayList<String>();
		answerOptions.add("1 - Extremely helpful");
		answerOptions.add("2 - Very helpful");
		answerOptions.add("3 - Somewhat helpful");
		answerOptions.add("4 - Not very helpful");
		answerOptions.add("5 - Not at all helpful");
		ImportanceQuestion question2 = (ImportanceQuestion) factory.createQuestion("ImportanceQuestion","How do you rate our services?",answerOptions);
		questions.add(question2);
		
		
	
		answerOptions = new ArrayList<String>();
		answerOptions.add("1 - Never");
		answerOptions.add("2 - Rarely");
		answerOptions.add("3 - Sometimes");
		answerOptions.add("4 - Often");
		answerOptions.add("5 - Always");
		LikertQuestion question3 = (LikertQuestion) factory.createQuestion("LikertQuestion","How often do you check the communityhub after visiting our homepage?",answerOptions);
		questions.add(question3);
		
	

		answerOptions = new ArrayList<String>();
		answerOptions.add("Yes");
		answerOptions.add("No");
		DichotomousQuestion question4 = (DichotomousQuestion) factory.createQuestion("DichotomousQuestion","Do you think we should add a google log-in?" ,answerOptions);
		questions.add(question4);

		
		questionnaire.setQuestions(questions);
		
		ObjectMapper mapper = new ObjectMapper();
		
		//Object to JSON in String
		try {
			String jsonInString = mapper.writeValueAsString(questionnaire);
			System.out.println(jsonInString);
			try {
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				Questionnaire readQuestionnaire = mapper.readValue(jsonInString, Questionnaire.class);
				System.out.println(jsonInString);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		QuestionnaireResponse result = new QuestionnaireResponse();
		List<QuestionResponse> responses = new ArrayList<QuestionResponse>();
		QuestionResponse response = new QuestionResponse();
		response.setQuestion(questions.get(0).getText());
		response.setAnswer("Probably apples");
		responses.add(response);
		
	
		 response = new QuestionResponse();
		response.setQuestion(questions.get(1).getText());
		response.setAnswer("Nice");
		responses.add(response);
		
		 response = new QuestionResponse();
			response.setQuestion(questions.get(2).getText());
			response.setAnswer(((SingleSelectAnswer) questions.get(2).getAnswer()).getOptions().get(2));
			responses.add(response);
			
			 response = new QuestionResponse();
				response.setQuestion(questions.get(3).getText());
				response.setAnswer(((SingleSelectAnswer) questions.get(3).getAnswer()).getOptions().get(1));
				responses.add(response);
				
				 response = new QuestionResponse();
					response.setQuestion(questions.get(4).getText());
					response.setAnswer(((SingleSelectAnswer) questions.get(4).getAnswer()).getOptions().get(1));
					responses.add(response);
					
					result.setQuestionResponses(responses);
					
					
					
					//Object to JSON in String
					try {
						String jsonInString = mapper.writeValueAsString(result);
						System.out.println(jsonInString);
						try {
							mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
							Questionnaire readQuestionnaire = mapper.readValue(jsonInString, Questionnaire.class);
							Map<String,List<String>> merged = new HashMap<String,List<String>>();
							for(int i=0; i <result.getQuestionResponses().size(); i++)
							{
								if(merged.containsKey(result.getQuestionResponses().get(i).getQuestion()))
								{
									List<String> answers = merged.get(result.getQuestionResponses().get(i).getQuestion());
									answers.add(result.getQuestionResponses().get(i).getAnswer());
									merged.put(result.getQuestionResponses().get(i).getQuestion(), answers);
								}
								else
								{
									List<String> answers = new ArrayList<String>();
									answers.add(result.getQuestionResponses().get(i).getAnswer());
									merged.put(result.getQuestionResponses().get(i).getQuestion(), answers);
								}
					
							}
							QuestionnaireResult quesResult = new QuestionnaireResult();
							List<QuestionResult> results = new ArrayList<QuestionResult>();
							for(int i=0; i <questionnaire.getQuestions().size(); i++)
							{
								Question currentQuestion = questionnaire.getQuestions().get(i);
								QuestionResult curQuesResult = new QuestionResult();
								curQuesResult.setQuestion(currentQuestion.getText());
								curQuesResult.setAnswerType(currentQuestion.getAnswer().getType());
							
									Map<String, Integer> counter = new 	HashMap<String, Integer> ();
									List<String> answers = merged.get(curQuesResult.getQuestion());
									for(int j=0; j < answers.size(); j++)
									{
										String curAnswer = answers.get(j);
										if(counter.containsKey(curAnswer))
										{
											 int value= counter.get(curAnswer);
											 value++;
											 counter.put(curAnswer, value);
											
										}
										else
										{
											counter.put(curAnswer, 1);
										}
									}
									curQuesResult.setCounter(counter);
									results.add(curQuesResult);
							
							}
							quesResult.setResults(results);
							System.out.println(jsonInString);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (JsonProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		
	}
	

}
