package org.betonquest.reposilite.mapper.settings;

import com.reposilite.configuration.shared.api.Doc;
import com.reposilite.configuration.shared.api.SharedSettings;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.reposilite.adapter.validation.Validatable;
import org.betonquest.reposilite.adapter.validation.ValidationLogLevel;
import org.betonquest.reposilite.adapter.validation.ValidationResult;
import org.betonquest.reposilite.adapter.validation.ValidationType;
import org.betonquest.reposilite.mapper.integration.PomMapperFacade;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * The settings for the advanced restful API.
 */
@SuppressWarnings({"FieldCanBeLocal", "PMD.DataClass"})
@Doc(title = "PomMapper", description = "All settings related to the advanced restful API")
public final class PomMapperPluginSettings implements SharedSettings, Validatable<PomMapperFacade> {

    /**
     * The serialVersionUID.
     */
    @Serial
    private static final long serialVersionUID = -3339044274308286270L;

    /**
     * See {@link #isNamingConventionWarning()}.
     *
     * @see #isNamingConventionWarning()
     */
    /*default*/ boolean namingConventionWarning = true;

    /**
     * See {@link #isRunExistenceChecks()}.
     *
     * @see #isRunExistenceChecks()
     */
    /*default*/ boolean runExistenceChecks = true;

    /**
     * See {@link #getValidationLogLevel()}.
     *
     * @see #getValidationLogLevel()
     */
    /*default*/ ValidationLogLevel validationLogLevel = ValidationLogLevel.ALL;

    /**
     * See {@link #getArtifacts()}.
     *
     * @see #getArtifacts()
     */
    @SuppressFBWarnings("SE_BAD_FIELD")
    /*default*/ List<Artifact> artifacts = new ArrayList<>();

    /**
     * Default empty constructor for the settings.
     */
    public PomMapperPluginSettings() {
    }

    @Override
    public List<ValidationResult> validate(final PomMapperFacade facade) {
        final List<Artifact> artifacts = getArtifacts();
        final List<ValidationResult> results = new ArrayList<>();
        if (isNamingConventionWarning() && getValidationLogLevel() != ValidationLogLevel.IGNORE_ALL) {
            results.add(new ValidationResult("Running syntax tests...", ValidationType.INFO, List.of()));
            artifacts.stream().map(Artifact::validateNamingConvention).forEach(results::add);
        }
        if (isRunExistenceChecks() && getValidationLogLevel() != ValidationLogLevel.IGNORE_ALL) {
            results.add(new ValidationResult("Running semantics tests...", ValidationType.INFO, List.of()));
            artifacts.stream().map(artifact -> artifact.validateExistence(facade)).forEach(results::add);
        }
        return results;
    }

    /**
     * Prints warnings to the console if naming conventions according to Apache Maven are violated.
     *
     * @return true if warnings should be printed, false otherwise.
     */
    @Doc(title = "Syntax Tests", description = "Prints warnings to console if naming conventions according to apache maven are violated.")
    public boolean isNamingConventionWarning() {
        return namingConventionWarning;
    }

    /**
     * Prints warnings to the console if specified artifacts, repositories and such could not be found by their configuration.
     *
     * @return true if warnings should be printed, false otherwise.
     */
    @Doc(title = "Semantics Tests", description = "Prints warnings to console if specified artifacts, repositories and such could not be found by their configuration.")
    public boolean isRunExistenceChecks() {
        return runExistenceChecks;
    }

    /**
     * The number of logs that are outputted.
     *
     * @return the log level
     */
    @Doc(title = "Log Level", description = "Change the number of logs that are outputted.")
    public ValidationLogLevel getValidationLogLevel() {
        return validationLogLevel;
    }

    /**
     * All artifacts that are considered for listing requests.
     *
     * @return the artifacts
     */
    @Doc(title = "Artifacts", description = """
            All artifacts the are considered for listing requests.
            The id is supposed to be unique and is used for identifying the artifact in the advanced REST calls as well to enable swapping the artifact source easily.
            The repository simply defines a fixed default repository to source the artifact from. You can still address another repository via REST.
            The group id is the group id of the maven artifact. The artifact id is expect to be identical to how its defined in its configuration and without version.""")
    public List<Artifact> getArtifacts() {
        return artifacts;
    }
}
