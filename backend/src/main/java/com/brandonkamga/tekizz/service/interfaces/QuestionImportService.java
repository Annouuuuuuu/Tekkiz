package com.brandonkamga.tekizz.service.interfaces;

import com.brandonkamga.tekizz.dto.importData.QuestionImportRequest;
import com.brandonkamga.tekizz.dto.importData.QuestionImportResponse;

import java.io.InputStream;

/**
 * Service interface for importing questions from JSON files.
 */
public interface QuestionImportService {

    /**
     * Import questions from a request object.
     * Category must exist in database.
     * Tags are created automatically (normalized to lowercase).
     * 
     * @param request the import request containing questions data
     * @return response with import statistics and any errors
     */
    QuestionImportResponse importQuestions(QuestionImportRequest request);

    /**
     * Import questions from a JSON file path.
     * 
     * @param filePath path to the JSON file
     * @return response with import statistics and any errors
     */
    QuestionImportResponse importQuestionsFromFile(String filePath);

    /**
     * Import questions from an InputStream.
     * Works with resources inside JAR files.
     * 
     * @param inputStream the input stream containing JSON data
     * @param filename the filename for logging purposes
     * @return response with import statistics and any errors
     */
    QuestionImportResponse importQuestionsFromInputStream(InputStream inputStream, String filename);
}
