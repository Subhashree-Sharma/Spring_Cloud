package com.example.quizApp.service;

import com.example.quizApp.dao.QuestionDao;
import com.example.quizApp.dao.QuizDao;
import com.example.quizApp.model.Question;
import com.example.quizApp.model.QuestionWrapper;
import com.example.quizApp.model.Quiz;
import com.example.quizApp.model.Response;
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
    QuestionDao questionDao;

    public ResponseEntity<String> createQuiz(@RequestParam String category, @RequestParam int numQ, @RequestParam String title) {

        List<Question> questions = questionDao.findRandomQuestionsby(category, numQ);
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestions(questions);
        quizDao.save(quiz);
        return new ResponseEntity<>("Quiz created", HttpStatus.CREATED);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer id) {
      Optional<Quiz> quiz =  quizDao.findById(id);//If you  are mentioning the quiz id which is not there -> throw null error to handle we are using optional
      List<Question> questionsFromDB = quiz.get().getQuestions();
      List<QuestionWrapper> questionsForUser = new ArrayList<>();
      for(Question q : questionsFromDB){
          QuestionWrapper qw = new QuestionWrapper(q.getId(), q.getQuestionTitle(),q.getOption1(), q.getOption2(), q.getOption3(), q.getOption4());
          questionsForUser.add(qw);
      }

      return new ResponseEntity<>(questionsForUser, HttpStatus.OK);
    }

//    public ResponseEntity<Integer> calculateResult(Integer id, List<Response> responses) {
//        //using quiz because in one quiz you have multiple questions
//        Quiz quiz = quizDao.findById(id).get(); // if you are using get here then no need of optional
//        List<Question> questions = quiz.getQuestions();
//        int right = 0;
//        int i = 0;
//        for(Response response : responses){
//            if(response.getResponse().equals(questions.get(i).getRightAnswer())){
//                right++;
//            }
//            i++;
//        }
//        return new ResponseEntity<>(right, HttpStatus.OK);
//    }
public ResponseEntity<Integer> calculateResult(Integer id, List<Response> responses) {
    Quiz quiz = quizDao.findById(id).get();
    List<Question> questions = quiz.getQuestions();

    int right = 0;

    // Store question answers in a map for quick lookup
    Map<Integer, String> answerMap = new HashMap<>();
    for (Question q : questions) {
        answerMap.put(q.getId(), q.getRightAnswer());
    }

    // Compare responses with correct answers
    for (Response response : responses) {
        if (answerMap.containsKey(response.getId()) &&
                answerMap.get(response.getId()).equals(response.getResponse())) {
            right++;
        }
    }

    return new ResponseEntity<>(right, HttpStatus.OK);
}

}
