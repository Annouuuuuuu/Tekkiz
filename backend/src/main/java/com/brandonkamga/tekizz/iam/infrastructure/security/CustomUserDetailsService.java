package com.brandonkamga.tekizz.iam.infrastructure.security;

/**
 * Re-export: the actual implementation lives in the legacy package.
 * This class extends it to register under the DDD package.
 *
 * We keep the original in place to avoid breaking the @ComponentScan
 * configuration. This is a transitional approach.
 */
public class CustomUserDetailsService extends com.brandonkamga.tekizz.security.CustomUserDetailsService {

    public CustomUserDetailsService(com.brandonkamga.tekizz.repository.UserRepository userRepository) {
        super(userRepository);
    }
}
