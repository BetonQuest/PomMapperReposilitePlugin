package org.betonquest.reposilite.mapper.restful;

/**
 * Contains all paths and query parameter definitions for the restful API.
 */
@SuppressWarnings("PMD.ConstantsInInterface")
public interface RestfulDefinitions {

    /**
     * The root path of the restful API for all services.
     */
    String ROOT = "/api/pommapper/";

    // ------------------- Service: direct -------------------

    /**
     * The path of the direct repository service.
     */
    String SERVICE_DIRECT_PREFIXED = ROOT + "repo/";

    /**
     * The path of the direct repository service.
     */
    String SERVICE_DIRECT_PATH = SERVICE_DIRECT_PREFIXED + "{repository}/{gav}";

    /**
     * The path of the direct repository service with open api syntax.
     */
    String SERVICE_DIRECT_PATH_REPOSILITE = SERVICE_DIRECT_PREFIXED + "{repository}/<gav>";

    /**
     * The query parameter for snapshot versions of the direct repository service.
     */
    String SERVICE_DIRECT_QPARAM_NAME_SNAPSHOT = "snapshots";

    /**
     * The default value for snapshot versions of the direct repository service.
     */
    boolean SERVICE_DIRECT_QPARAM_DEFAULT_SNAPSHOT = true;

    /**
     * The query parameter for release versions of the direct repository service.
     */
    String SERVICE_DIRECT_QPARAM_NAME_RELEASE = "releases";

    /**
     * The default value for release versions of the direct repository service.
     */
    boolean SERVICE_DIRECT_QPARAM_DEFAULT_RELEASE = true;

    // ------------------- Service: accessor -------------------

    /**
     * The path prefix of the id service.
     */
    String SERVICE_ACCESSOR_PREFIXED = ROOT + "id/";

    /**
     * The full path of the id service.
     */
    String SERVICE_ACCESSOR_PATH = SERVICE_ACCESSOR_PREFIXED + "{id}";

    /**
     * The path of the id service with open api syntax.
     */
    String SERVICE_ACCESSOR_PATH_REPOSILITE = SERVICE_ACCESSOR_PATH;

    /**
     * The name of the snapshot query parameter for the id service.
     */
    String SERVICE_ACCESSOR_QPARAM_NAME_SNAPSHOT = "snapshots";

    /**
     * The default value of the snapshot query parameter for the id service.
     */
    boolean SERVICE_ACCESSOR_QPARAM_DEFAULT_SNAPSHOT = true;

    /**
     * The default value of the release query parameter for the id service.
     */
    String SERVICE_ACCESSOR_QPARAM_NAME_RELEASE = "releases";

    /**
     * The default value of the release query parameter for the id service.
     */
    boolean SERVICE_ACCESSOR_QPARAM_DEFAULT_RELEASE = true;

    // ------------------- Rest API Results -------------------

    /**
     * The key for the maven version in the JSON result.
     */
    String RESULT_JSON_KEY_MVN_VERSION = "version";

    /**
     * The key for the artifact's version list in the JSON result.
     */
    String RESULT_JSON_KEY_VERSIONS = "versions";

    /**
     * The key for the artifact's jar path in the JSON result.
     */
    String RESULT_JSON_KEY_JAR_PATH = "jar";

    /**
     * The key for all entries filtered using xPaths from the artifact's pom.xml in the JSON result.
     */
    String RESULT_JSON_KEY_ENTRIES = "entries";

    /**
     * The key for the artifact's maven version group in the JSON result.
     */
    String RESULT_JSON_KEY_GROUP = "group";
}
