package com.brandonkamga.tekizz.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminQuestionRequest {

    @NotBlank(message = "Content is required")
    private String content;

    private String explanation;

    private String hint;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotBlank(message = "Level is required")
    private String level; // EASY, MEDIUM, HARD, EXPERT

    @Builder.Default
    private String status = "ACTIVE";

    private List<Long> tagIds;

    @NotNull(message = "Answers are required")
    @Size(min = 2, max = 6, message = "Between 2 and 6 answers required")
    private List<AnswerRequest> answers;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AnswerRequest {
        @NotBlank(message = "Answer content is required")
        private String content;

        @NotNull(message = "isCorrect flag is required")
        private Boolean isCorrect;
    }
}
