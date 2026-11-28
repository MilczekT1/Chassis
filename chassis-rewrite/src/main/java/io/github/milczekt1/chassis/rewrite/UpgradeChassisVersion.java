package io.github.milczekt1.chassis.rewrite;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.Nullable;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Option;
import org.openrewrite.ScanningRecipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.internal.StringUtils;
import org.openrewrite.maven.MavenIsoVisitor;
import org.openrewrite.maven.MavenVisitor;
import org.openrewrite.semver.LatestRelease;
import org.openrewrite.semver.Semver;
import org.openrewrite.xml.tree.Xml;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Edits only Chassis versions. Do not replace it with {@code UpgradeParentVersion} or {@code UpgradeDependencyVersion}:
 * after a change both run {@code RemoveRedundantDependencyVersions} over every dependency of the POM, which strips
 * unrelated explicit versions and can silently upgrade them.
 * <p>
 * Do not require the new version in the repository's metadata: it is released together with this recipe, and when the
 * metadata lacks it the bump silently never happens.
 * <p>
 * The accumulator holds each property that holds a Chassis version, in any POM of the project; the property is bumped
 * in whichever POM defines it, such as an in-repo parent.
 */
public class UpgradeChassisVersion extends ScanningRecipe<Set<String>> {

    private static final String CHASSIS_GROUP = "io.github.milczekt1";
    private static final String CHASSIS_ARTIFACTS = "chassis*";
    private static final String VERSION_TAG = "version";

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
               "the property holding their version, to the new version. Never lowers a version and changes nothing " +
               "else in the POM.";
    }

    @Override
    public Set<String> getInitialValue(ExecutionContext ctx) {
        return new HashSet<>();
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getScanner(Set<String> versionProperties) {
        return new MavenIsoVisitor<ExecutionContext>() {
            @Override
            public Xml.Tag visitTag(Xml.Tag tag, ExecutionContext ctx) {
                Xml.Tag t = super.visitTag(tag, ctx);
                String version = t.getChildValue(VERSION_TAG).orElse(null);
                if (isChassisArtifact(this, t) && isProperty(version)) {
                    versionProperties.add(version.substring(2, version.length() - 1));
                }
                return t;
            }
        };
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor(Set<String> versionProperties) {
        return new MavenIsoVisitor<ExecutionContext>() {
            @Override
            public Xml.Tag visitTag(Xml.Tag tag, ExecutionContext ctx) {
                Xml.Tag t = super.visitTag(tag, ctx);
                if (isPropertyTag() && versionProperties.contains(t.getName())) {
                    if (isUpgrade(t.getValue().orElse(null))) {
                        maybeUpdateModel();
                        return t.withValue(newVersion);
                    }
                    return t;
                }
                String version = t.getChildValue(VERSION_TAG).orElse(null);
                if (isChassisArtifact(this, t) && !isProperty(version) && isUpgrade(version)) {
                    maybeUpdateModel();
                    return changeChildTagValue(t, VERSION_TAG, newVersion, ctx);
                }
                return t;
            }
        };
    }

    private boolean isUpgrade(@Nullable String current) {
        return current != null &&
               Semver.isVersion(current) &&
               new LatestRelease(null).compare(null, current, newVersion) < 0;
    }

    /**
     * Whether the tag the visitor is on is the parent, a dependency or a managed dependency with Chassis coordinates.
     */
    private static boolean isChassisArtifact(MavenVisitor<?> visitor, Xml.Tag tag) {
        return (visitor.isParentTag() || visitor.isDependencyTag() || visitor.isManagedDependencyTag()) &&
               CHASSIS_GROUP.equals(tag.getChildValue("groupId").orElse(null)) &&
               StringUtils.matchesGlob(tag.getChildValue("artifactId").orElse(null), CHASSIS_ARTIFACTS);
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
