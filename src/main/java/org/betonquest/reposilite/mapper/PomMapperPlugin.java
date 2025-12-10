package org.betonquest.reposilite.mapper;

import com.reposilite.console.ConsoleFacade;
import com.reposilite.maven.MavenFacade;
import com.reposilite.maven.api.DeployEvent;
import com.reposilite.plugin.api.Facade;
import com.reposilite.plugin.api.Plugin;
import com.reposilite.plugin.api.ReposiliteInitializeEvent;
import com.reposilite.plugin.api.ReposilitePostInitializeEvent;
import com.reposilite.plugin.api.ReposiliteStartedEvent;
import com.reposilite.web.api.ReposiliteRoute;
import com.reposilite.web.api.RoutingSetupEvent;
import org.betonquest.reposilite.adapter.PluginAdapter;
import org.betonquest.reposilite.adapter.validation.ValidationLogLevel;
import org.betonquest.reposilite.adapter.validation.ValidationResult;
import org.betonquest.reposilite.mapper.command.UpdateCacheCommand;
import org.betonquest.reposilite.mapper.integration.ArtifactsVersionsCache;
import org.betonquest.reposilite.mapper.integration.PomMapperFacade;
import org.betonquest.reposilite.mapper.restful.RestfulRoutes;
import org.betonquest.reposilite.mapper.settings.Artifact;
import org.betonquest.reposilite.mapper.settings.PomMapperPluginSettings;
import org.jetbrains.annotations.Nullable;
import panda.std.reactive.MutableReference;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The PomMapper main class for Reposilite.
 */
@Plugin(name = "PomMapper", settings = PomMapperPluginSettings.class, dependencies = {"shared-configuration", "maven"})
public class PomMapperPlugin extends PluginAdapter<PomMapperFacade, PomMapperPluginSettings> {

    /**
     * The cache of all {@link Artifact} versions as defined in the plugin settings.
     */
    private final ArtifactsVersionsCache artifactsVersionsCache;

    /**
     * The facade for the plugin as api via reposilite.
     */
    private final PomMapperFacade baseFacade;

    /**
     * The implementation of the restful endpoints.
     */
    private RestfulRoutes restfulImplementation;

    /**
     * Default Constructor for the PomMapperPlugin.
     */
    public PomMapperPlugin() {
        super("PomMapper", PomMapperFacade.class, PomMapperPluginSettings.class);
        this.artifactsVersionsCache = new ArtifactsVersionsCache(this);
        this.baseFacade = new PomMapperFacade(this, this.artifactsVersionsCache);
    }

    @Override
    @Nullable
    public Facade onLoad() {
        extensions().registerEvent(ReposiliteInitializeEvent.class, this.baseFacade);
        extensions().facade(ConsoleFacade.class).registerCommand(new UpdateCacheCommand(this::updateCache));
        return baseFacade;
    }

    @Override
    public void onInitialize(final ReposiliteInitializeEvent event) {
        info("Initializing...");

        this.restfulImplementation = new RestfulRoutes(getFacade(MavenFacade.class), baseFacade);

        final MutableReference<PomMapperPluginSettings> config = getConfig();
        final PomMapperPluginSettings settings = config.get();
        config.subscribe(sets -> ValidationResult.printBlock(sets.validate(baseFacade), this::warn, this::info, ValidationLogLevel.ERRORS_ONLY));

        final List<String> artifacts = settings.getArtifacts().stream().map(Artifact::id).toList();
        info("Loaded " + artifacts.size() + " artifacts.");
        debug("  > " + String.join(", ", artifacts));
    }

    @Override
    public void onEnable(final ReposilitePostInitializeEvent event) {
        info("Attempting to generate cache...");
        updateCache();
        info("Cache generation complete.");
    }

    @Override
    public void onStart(final ReposiliteStartedEvent event) {
        final List<ValidationResult> validate = getConfig().get().validate(baseFacade);
        ValidationResult.printBlock(validate, this::warn, this::info, ValidationLogLevel.ALL);
        getConfig().subscribe(settings -> updateCache());
    }

    @Override
    public void onDeploy(final DeployEvent event) {
        final Artifact artifact = baseFacade.findArtifact(event.getRepository().getName(), event.getGav());
        if (artifact == null) {
            return;
        }
        if (artifactsVersionsCache.hasEntry(artifact.artifactId())) {
            debug("Updating cache for artifact with id: " + artifact.artifactId());
            updateCache();
        }
    }

    @Override
    public void onRoutingSetup(final RoutingSetupEvent event) {
        event.registerRoutes(this.restfulImplementation);
        info("Mapper routes registered: " + this.restfulImplementation.getRoutes().stream().map(ReposiliteRoute::getPath).collect(Collectors.joining(", ")));
    }

    private void updateCache() {
        final List<ValidationResult> validate = getConfig().get().validate(baseFacade);
        ValidationResult.printBlock(validate, this::warn, this::info, getConfig().get().getValidationLogLevel());
        final List<Artifact> artifacts = getConfig().get().getArtifacts();
        debug("Generating cache for " + artifacts.size() + " artifacts...");
        for (final Artifact artifact : artifacts) {
            if (artifactsVersionsCache.attemptToCache(artifact)) {
                debug("  > \"" + artifact.id() + "\" cache generated. (" + artifactsVersionsCache.getVersionsCount(artifact.id()) + " versions)");
            } else {
                warn("  > \"" + artifact.id() + "\" cache generation failed");
            }
        }
    }
}
