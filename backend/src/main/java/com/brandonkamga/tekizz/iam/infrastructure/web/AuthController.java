package com.brandonkamga.tekizz.iam.infrastructure.web;

/**
 * Re-export: the actual implementation lives in the legacy package during migration.
 * The existing AuthController at /auth/** mapping is preserved.
 *
 * This class exists as a DDD structural marker.
 * The actual bean is com.brandonkamga.tekizz.controller.AuthController.
 */
// Note: This is intentionally not a @RestController - it's a structural placeholder.
// The actual AuthController in the legacy package handles /auth/** endpoints.
public final class AuthController {
    private AuthController() {}
}
