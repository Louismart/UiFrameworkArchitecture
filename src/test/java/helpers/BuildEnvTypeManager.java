package helpers;

import com.google.common.collect.ImmutableMap;
import configuration.EnvironmentConfig;
import exception.WrongFrameworkUsageException;
import tags.PlutusTest;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public class BuildEnvTypeManager {

    private static final Map<Class<? extends Annotation>, Supplier<BuildEnvType>> supplierMap = Map.of(
            PlutusTest.class, BuildEnvTypeManager::getBuildEnvTypeForPlutus
    );
    private static final Set<Class<? extends Annotation>> SUPPORTED_PROJECTS = supplierMap.keySet();
    private static final ImmutableMap<Class<? extends Annotation>, BuildEnvType> projectBuildEnvTypes = createProjectBuildEnvTypes();

    public static BuildEnvType getBuildEnvTypeFor(final Class<? extends Annotation> project) {
        return Optional.ofNullable(projectBuildEnvTypes.get(project))
                .orElseThrow(() -> new WrongFrameworkUsageException("Unknown project: " + project));
    }
    private static ImmutableMap<Class<? extends Annotation>, BuildEnvType> createProjectBuildEnvTypes() {
        ImmutableMap.Builder<Class<? extends Annotation>, BuildEnvType> mapBuilder = ImmutableMap.builder();
        return mapBuilder.build();

    }

    /**
     * npm-plutonium based application images are env-agnostic, the configuration is changed
     * based on settings provided in runtime.
     *
     * For web-sandbox, it's the devops/kubernetes/overlays/sandbox/plutus/settings.json file
     */
    private static BuildEnvType getBuildEnvTypeForPlutus() {
        return BuildEnvType.PRD;
    }
}
