package com.example.quiz_service.service;

import com.example.quiz_service.dao.QuizDao;
import com.example.quiz_service.feign.QuizInterface;
import com.example.quiz_service.model.QuestionWrapper;
import com.example.quiz_service.model.Quiz;
import com.example.quiz_service.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Service
public class QuizService {

    @Autowired
    QuizDao quizDao;
    @Autowired
    QuizInterface quizInterface;

        public ResponseEntity<String> createQuiz(String category, int numQ, String title){

        List<Integer> questions = quizInterface.getQuestionsForQuiz(category, numQ).getBody();
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestionIds(questions);
        quizDao.save(quiz);

        return new ResponseEntity<>("Quiz created", HttpStatus.CREATED);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer id) {
      Quiz quiz =  quizDao.findById(id).get();
      List<Integer> questionIds = quiz.getQuestionIds();

      ResponseEntity<List<QuestionWrapper>> questions = quizInterface.getQuestionsFromID(questionIds);
      return questions;
    }

public ResponseEntity<Integer> calculateResult(Integer id, List<Response> responses) {

    ResponseEntity<Integer> score = quizInterface.getScore(responses);
    return score;
}

}
