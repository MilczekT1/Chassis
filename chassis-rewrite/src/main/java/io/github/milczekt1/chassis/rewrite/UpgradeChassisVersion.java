package io.github.milczekt1.chassis.rewrite;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Option;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.internal.StringUtils;
import org.openrewrite.maven.ChangePropertyValue;
import org.openrewrite.maven.MavenDownloadingException;
import org.openrewrite.maven.MavenIsoVisitor;
import org.openrewrite.semver.LatestRelease;
import org.openrewrite.semver.Semver;
import org.openrewrite.xml.tree.Xml;

import java.util.Objects;

/**
 * Edits only Chassis versions. Do not replace it with {@code UpgradeParentVersion} or {@code UpgradeDependencyVersion}:
 * after a change both run {@code RemoveRedundantDependencyVersions} over every dependency of the POM, which strips
 * unrelated explicit versions and can silently upgrade them.
 */
public class UpgradeChassisVersion extends Recipe {

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
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new MavenIsoVisitor<>() {
            @Override
            public Xml.Tag visitTag(Xml.Tag tag, ExecutionContext ctx) {
                Xml.Tag t = super.visitTag(tag, ctx);
                if (!isParentTag() && !isDependencyTag() && !isManagedDependencyTag()) {
                    return t;
                }
                String artifactId = t.getChildValue("artifactId").orElse(null);
                String version = t.getChildValue("version").orElse(null);
                if (!CHASSIS_GROUP.equals(t.getChildValue("groupId").orElse(null)) ||
                    !StringUtils.matchesGlob(artifactId, CHASSIS_ARTIFACTS) || version == null) {
                    return t;
                }
                String property = isProperty(version) ? version.substring(2, version.length() - 1) : null;
                String current = property == null
                        ? version
                        : getResolutionResult().getPom().getRequested().getProperties().get(property);
                try {
                    if (current == null || !isUpgrade(current) || !isPublished(artifactId, ctx)) {
                        return t;
                    }
                } catch (MavenDownloadingException e) {
                    return e.warn(t);
                }
                if (property != null) {
                    doAfterVisit(new ChangePropertyValue(property, newVersion, false, false).getVisitor());
                    return t;
                }
                maybeUpdateModel();
                return changeChildTagValue(t, "version", newVersion, ctx);
            }

            private boolean isUpgrade(String current) {
                return Semver.isVersion(current) && new LatestRelease(null).compare(null, current, newVersion) < 0;
            }

            private boolean isPublished(String artifactId, ExecutionContext ctx) throws MavenDownloadingException {
                return downloadMetadata(CHASSIS_GROUP, artifactId, ctx).getVersioning().getVersions().contains(newVersion);
            }
        };
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
