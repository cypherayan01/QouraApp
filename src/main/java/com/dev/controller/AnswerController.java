package com.dev.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.dev.dto.AnswerRequestDTO;
import com.dev.dto.AnswerResponseDTO;
import com.dev.dto.RecentEventsResponseDTO;
import com.dev.services.IAnswerService;
import com.dev.services.NotificationEventService;

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
@RequestMapping("/api/answers")
@Tag(name = "Answers", description = "Answer management APIs")
public class AnswerController {

    private final IAnswerService answerService;
    private final NotificationEventService notificationEventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new answer", description = "Creates a new answer for a question")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Answer created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public Mono<AnswerResponseDTO> createAnswer(@Valid @RequestBody AnswerRequestDTO answerRequestDTO,@RequestHeader("X-User-Id") String userId) {
        return answerService.createAnswer(answerRequestDTO,userId)
            .doOnSuccess(answer -> System.out.println("Created answer ID: " + answer.getId()))
            .doOnError(error -> System.err.println("Error creating answer: " + error.getMessage()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get answer by ID", description = "Retrieves an answer by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Answer found"),
        @ApiResponse(responseCode = "404", description = "Answer not found")
    })
    public Mono<AnswerResponseDTO> getAnswerById(
            @Parameter(description = "Answer ID") @PathVariable("id") String id) {
        return answerService.getAnswerById(id)
            .doOnSuccess(answer -> System.out.println("Fetched answer ID: " + answer.getId()))
            .doOnError(error -> System.err.println("Error fetching answer ID " + id + ": " + error.getMessage()));
    }

    @GetMapping
    @Operation(summary = "Get all answers", description = "Retrieves all answers in the system")
    @ApiResponse(responseCode = "200", description = "Answers retrieved successfully")
    public Flux<AnswerResponseDTO> getAllAnswers() {
        return answerService.getAllAnswers()
            .doOnError(error -> System.err.println("Error fetching all answers: " + error.getMessage()))
            .doOnComplete(() -> System.out.println("All answers fetched successfully"));
    }

    @GetMapping("/question/{questionId}")
    @Operation(summary = "Get answers by question ID", description = "Retrieves all answers for a specific question")
    @ApiResponse(responseCode = "200", description = "Answers retrieved successfully")
    public Flux<AnswerResponseDTO> getAnswersByQuestionId(
            @Parameter(description = "Question ID") @PathVariable("questionId") String questionId) {
        return answerService.getAnswersByQuestionId(questionId)
            .doOnError(error -> System.err.println("Error fetching answers for question ID " + questionId + ": " + error.getMessage()))
            .doOnComplete(() -> System.out.println("Answers for question ID " + questionId + " fetched successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update answer", description = "Updates an existing answer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Answer updated successfully"),
        @ApiResponse(responseCode = "404", description = "Answer not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public Mono<AnswerResponseDTO> updateAnswer(
            @Parameter(description = "Answer ID") @PathVariable("id") String id, 
            @Valid @RequestBody AnswerRequestDTO answerRequestDTO) {
        return answerService.updateAnswer(id, answerRequestDTO)
            .doOnSuccess(answer -> System.out.println("Updated answer ID: " + answer.getId()))
            .doOnError(error -> System.err.println("Error updating answer ID " + id + ": " + error.getMessage()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete answer", description = "Deletes an answer by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Answer deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Answer not found")
    })
    public Mono<Void> deleteAnswer(
            @Parameter(description = "Answer ID") @PathVariable("id") String id) {
        return answerService.deleteAnswer(id)
            .doOnSuccess(unused -> System.out.println("Deleted answer ID: " + id))
            .doOnError(error -> System.err.println("Error deleting answer ID " + id + ": " + error.getMessage()));
    }

    @GetMapping("/events/recent")
    @Operation(summary = "Get recent answer events", description = "Retrieves recent answer notification events with optional filtering")
    @ApiResponse(responseCode = "200", description = "Events retrieved successfully")
    public Mono<RecentEventsResponseDTO> getRecentAnswerEvents(
            @Parameter(description = "Number of hours to look back") @RequestParam(value = "hours", required = false) Integer hours,
            @Parameter(description = "Maximum number of events to return") @RequestParam(value = "limit", required = false) Integer limit,
            @Parameter(description = "Filter by question ID") @RequestParam(value = "questionId", required = false) String questionId,
            @Parameter(description = "Filter by notification type") @RequestParam(value = "type", required = false) String notificationType,
            @Parameter(description = "Filter by user ID") @RequestParam(value = "userId", required = false) String userId) {
        
        return notificationEventService.getRecentEvents(hours, limit, questionId, notificationType, userId)
            .doOnSuccess(response -> 
                System.out.println("Retrieved " + response.getEvents().size() + " recent notification events"))
            .doOnError(error -> 
                System.err.println("Error retrieving recent events: " + error.getMessage()));
    }
}
