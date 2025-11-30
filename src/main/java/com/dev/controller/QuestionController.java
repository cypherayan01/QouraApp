package com.dev.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dev.dto.QuestionRequestDTO;
import com.dev.dto.QuestionResponseDTO;
import com.dev.services.IQuestionService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/questions")
public class QuestionController {

    private final IQuestionService questionService;

    @PostMapping()
    public Mono<QuestionResponseDTO> createQuestion(@RequestBody QuestionRequestDTO questionRequestDTO) {
        return questionService.createQuestion(questionRequestDTO);
    }

    @GetMapping("/{id}")
    public Mono<QuestionResponseDTO> getQuestionById(@PathVariable("id") String id) {
        return questionService.getQuestionById(id)
        .doOnSuccess(question -> System.out.println("Fetched question ID: " + question.getId()))
        .doOnError(error -> System.err.println("Error fetching question ID " + id + ": " + error.getMessage()));
    }

    @GetMapping
    public Flux<QuestionResponseDTO> getAllQuestions(
        @RequestParam(value = "cursor", required = false) String cursor,
        @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return questionService.getAllQuestions(cursor, size)
                .doOnError(error -> System.out.println("Error fetching questions : "+error))
                .doOnComplete(() -> System.out.println("All question fetched succeccfully."));
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteQuestion(@PathVariable("id") String id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping("/search")
    public Flux<QuestionResponseDTO> searchQuestions(
            @RequestParam("query") String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "100") int size) {
        return questionService.searchQuestions(query, page, size);
    }

    @GetMapping("/tag/{tag}")
    public Flux<QuestionResponseDTO> getQuestionsByTag(
        @PathVariable("tag") String tag,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "100") int size) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
