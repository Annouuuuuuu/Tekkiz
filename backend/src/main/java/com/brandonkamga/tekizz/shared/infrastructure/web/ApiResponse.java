package com.brandonkamga.tekizz.shared.infrastructure.web;

/**
 * Shared API response alias - delegates to existing ApiResponse.
 * This allows new code to import from the shared package while
 * preserving backward compatibility.
 */
public class ApiResponse<T> extends com.brandonkamga.tekizz.dto.ApiResponse<T> {
    // Inherits all static factory methods from the existing ApiResponse
}
