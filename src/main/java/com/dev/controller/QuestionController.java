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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/questions")
@Tag(name = "Questions", description = "Question management APIs")
public class QuestionController {

    private final IQuestionService questionService;

    @PostMapping()
    @Operation(summary = "Create a new question", description = "Creates a new question in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Question created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public Mono<QuestionResponseDTO> createQuestion(@Valid @RequestBody QuestionRequestDTO questionRequestDTO) {
        return questionService.createQuestion(questionRequestDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get question by ID", description = "Retrieves a question by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Question found"),
        @ApiResponse(responseCode = "404", description = "Question not found")
    })
    public Mono<QuestionResponseDTO> getQuestionById(
            @Parameter(description = "Question ID") @PathVariable("id") String id) {
        return questionService.getQuestionById(id)
        .doOnSuccess(question -> System.out.println("Fetched question ID: " + question.getId()))
        .doOnError(error -> System.err.println("Error fetching question ID " + id + ": " + error.getMessage()));
    }

    @GetMapping
    @Operation(summary = "Get all questions", description = "Retrieves all questions with pagination support")
    @ApiResponse(responseCode = "200", description = "Questions retrieved successfully")
    public Flux<QuestionResponseDTO> getAllQuestions(
        @Parameter(description = "Cursor for pagination") @RequestParam(value = "cursor", required = false) String cursor,
        @Parameter(description = "Page size") @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return questionService.getAllQuestions(cursor, size)
                .doOnError(error -> System.out.println("Error fetching questions : "+error))
                .doOnComplete(() -> System.out.println("All question fetched succeccfully."));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete question", description = "Deletes a question by ID (Not implemented)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Question deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Question not found")
    })
    public Mono<Void> deleteQuestion(
            @Parameter(description = "Question ID") @PathVariable("id") String id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping("/search")
    @Operation(summary = "Search questions", description = "Search for questions based on query string")
    @ApiResponse(responseCode = "200", description = "Search results retrieved successfully")
    public Flux<QuestionResponseDTO> searchQuestions(
            @Parameter(description = "Search query", required = true) @RequestParam("query") String query,
            @Parameter(description = "Page number") @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(value = "size", defaultValue = "100") int size) {
        return questionService.searchQuestions(query, page, size);
    }

    @GetMapping("/tag/{tag}")
    @Operation(summary = "Get questions by tag", description = "Retrieves questions filtered by tag (Not implemented)")
    @ApiResponse(responseCode = "200", description = "Questions retrieved successfully")
    public Flux<QuestionResponseDTO> getQuestionsByTag(
        @Parameter(description = "Tag name") @PathVariable("tag") String tag,
        @Parameter(description = "Page number") @RequestParam(value = "page", defaultValue = "0") int page,
        @Parameter(description = "Page size") @RequestParam(value = "size", defaultValue = "100") int size) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
