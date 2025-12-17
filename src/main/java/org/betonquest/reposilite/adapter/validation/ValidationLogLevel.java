package org.betonquest.reposilite.adapter.validation;

/**
 * The log level for validation results.
 */
public enum ValidationLogLevel {

    /**
     * Prints everything.
     */
    ALL,
    /**
     * Prints validation results.
     */
    INFO,
    /**
     * Prints only validation results with errors.
     */
    ERRORS_ONLY,
    /**
     * Ignores all validation results.
     */
    IGNORE_ALL
}
