package io.github.milczekt1.chassis.rewrite;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Option;
import org.openrewrite.ScanningRecipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.internal.StringUtils;
import org.openrewrite.maven.MavenDownloadingException;
import org.openrewrite.maven.MavenIsoVisitor;
import org.openrewrite.semver.LatestRelease;
import org.openrewrite.semver.Semver;
import org.openrewrite.xml.tree.Xml;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Edits only Chassis versions. Do not replace it with {@code UpgradeParentVersion} or {@code UpgradeDependencyVersion}:
 * after a change both run {@code RemoveRedundantDependencyVersions} over every dependency of the POM, which strips
 * unrelated explicit versions and can silently upgrade them.
 * <p>
 * The accumulator maps each property that holds a Chassis version, in any POM of the project, to an artifact it versions;
 * the property is bumped in whichever POM defines it, such as an in-repo parent.
 */
public class UpgradeChassisVersion extends ScanningRecipe<Map<String, String>> {

    private static final String CHASSIS_GROUP = "io.github.milczekt1";
    private static final String CHASSIS_ARTIFACTS = "chassis*";

    @Option(displayName = "New version", description = "The Chassis version to move to.", example = "0.5.6")
    private final String newVersion;

    @JsonCreator
    public UpgradeChassisVersion(@JsonProperty("newVersion") String newVersion) {
        this.newVersion = newVersion;
    }

    public String getNewVersion() {
        return newVersion;
    }

    @Override
    public String getDisplayName() {
        return "Upgrade Chassis version";
    }

    @Override
    public String getDescription() {
        return "Moves the `chassis-parent` parent, and every `io.github.milczekt1:chassis*` dependency, BOM import and " +
               "the property holding their version, to the new version once it is published. Never lowers a version " +
               "and changes nothing else in the POM.";
    }

    @Override
    public Map<String, String> getInitialValue(ExecutionContext ctx) {
        return new HashMap<>();
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getScanner(Map<String, String> versionProperties) {
        return new ChassisVisitor() {
            @Override
            public Xml.Tag visitTag(Xml.Tag tag, ExecutionContext ctx) {
                Xml.Tag t = super.visitTag(tag, ctx);
                String artifactId = chassisArtifactId(t);
                String version = t.getChildValue("version").orElse(null);
                if (artifactId != null && isProperty(version)) {
                    versionProperties.putIfAbsent(version.substring(2, version.length() - 1), artifactId);
                }
                return t;
            }
        };
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor(Map<String, String> versionProperties) {
        return new ChassisVisitor() {
            @Override
            public Xml.Tag visitTag(Xml.Tag tag, ExecutionContext ctx) {
                Xml.Tag t = super.visitTag(tag, ctx);
                try {
                    if (isPropertyTag() && versionProperties.containsKey(t.getName())) {
                        if (isUpgrade(t.getValue().orElse(null), versionProperties.get(t.getName()), ctx)) {
                            maybeUpdateModel();
                            return t.withValue(newVersion);
                        }
                        return t;
                    }
                    String artifactId = chassisArtifactId(t);
                    String version = t.getChildValue("version").orElse(null);
                    if (artifactId != null && !isProperty(version) && isUpgrade(version, artifactId, ctx)) {
                        maybeUpdateModel();
                        return changeChildTagValue(t, "version", newVersion, ctx);
                    }
                } catch (MavenDownloadingException e) {
                    return e.warn(t);
                }
                return t;
            }

            private boolean isUpgrade(@Nullable String current, String artifactId, ExecutionContext ctx)
                    throws MavenDownloadingException {
                return current != null &&
                       Semver.isVersion(current) &&
                       new LatestRelease(null).compare(null, current, newVersion) < 0 &&
                       downloadMetadata(CHASSIS_GROUP, artifactId, ctx).getVersioning().getVersions().contains(newVersion);
            }
        };
    }

    private abstract static class ChassisVisitor extends MavenIsoVisitor<ExecutionContext> {

        /**
         * The artifactId when the tag is the parent, a dependency or a managed dependency with Chassis coordinates.
         */
        @Nullable
        String chassisArtifactId(Xml.Tag tag) {
            if (!isParentTag() && !isDependencyTag() && !isManagedDependencyTag()) {
                return null;
            }
            String artifactId = tag.getChildValue("artifactId").orElse(null);
            return CHASSIS_GROUP.equals(tag.getChildValue("groupId").orElse(null)) &&
                   StringUtils.matchesGlob(artifactId, CHASSIS_ARTIFACTS) ? artifactId : null;
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof UpgradeChassisVersion other && Objects.equals(newVersion, other.newVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(newVersion);
    }
}
