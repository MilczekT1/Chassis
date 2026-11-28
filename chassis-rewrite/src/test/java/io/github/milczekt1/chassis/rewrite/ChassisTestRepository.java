package io.github.milczekt1.chassis.rewrite;

import org.openrewrite.ExecutionContext;
import org.openrewrite.InMemoryExecutionContext;
import org.openrewrite.maven.MavenExecutionContextView;
import org.openrewrite.maven.tree.MavenRepository;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * A fake local Maven repository for recipe tests. {@link #LEGACY} Chassis brings testcontainers-mongodb and
 * spring-boot-restclient transitively, as 0.5.5 did; every other version brings neither.
 */
final class ChassisTestRepository {

    static final String LEGACY = "0.5.5";

    private static final String CHASSIS_GROUP = "io.github.milczekt1";
    private static final String TESTCONTAINERS_MONGODB = dependency("org.testcontainers", "testcontainers-mongodb", "2.0.5");
    private static final String SPRING_BOOT_RESTCLIENT = dependency("org.springframework.boot", "spring-boot-restclient", "4.0.6");
    private static final Pattern NEW_VERSION = Pattern.compile("newVersion: \"([^\"]+)\"");
    private static final byte[] EMPTY_ZIP = {0x50, 0x4b, 0x05, 0x06, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    private ChassisTestRepository() {
    }

    static String target() {
        try (InputStream yaml = ChassisTestRepository.class.getResourceAsStream("/META-INF/rewrite/upgrade-chassis.yml")) {
            Matcher newVersion = NEW_VERSION.matcher(new String(Objects.requireNonNull(yaml).readAllBytes(), StandardCharsets.UTF_8));
            if (!newVersion.find()) {
                throw new IllegalStateException("No newVersion in META-INF/rewrite/upgrade-chassis.yml");
            }
            return newVersion.group(1);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static ExecutionContext executionContext(Path localRepository, String... chassisVersions) {
        for (String version : chassisVersions) {
            writePom(localRepository, CHASSIS_GROUP, "chassis-parent", version, "pom", """
                    <dependencyManagement><dependencies>
                    %s
                    </dependencies></dependencyManagement>""".formatted(bomImport(version)));
            writePom(localRepository, CHASSIS_GROUP, "chassis-bom", version, "pom", """
                    <dependencyManagement><dependencies>
                    %s
                    %s
                    %s
                    %s
                    </dependencies></dependencyManagement>""".formatted(
                    dependency(CHASSIS_GROUP, "chassis-dependencies", version),
                    dependency(CHASSIS_GROUP, "chassis-tools-test", version),
                    TESTCONTAINERS_MONGODB,
                    SPRING_BOOT_RESTCLIENT));
            writePom(localRepository, CHASSIS_GROUP, "chassis-dependencies", version, "jar",
                    LEGACY.equals(version) ? "<dependencies>" + SPRING_BOOT_RESTCLIENT + "</dependencies>" : "");
            writePom(localRepository, CHASSIS_GROUP, "chassis-tools-test", version, "jar",
                    LEGACY.equals(version) ? "<dependencies>" + TESTCONTAINERS_MONGODB + "</dependencies>" : "");
        }
        writePom(localRepository, "org.testcontainers", "testcontainers-mongodb", "2.0.5", "jar", "");
        writePom(localRepository, "org.springframework.boot", "spring-boot-restclient", "4.0.6", "jar", "");
        writePom(localRepository, CHASSIS_GROUP, "corral-rules", "0.6.1", "jar", "");

        return MavenExecutionContextView.view(new InMemoryExecutionContext(t -> fail("OpenRewrite error", t)))
                .setLocalRepository(MavenRepository.builder()
                        .id("local")
                        .uri(localRepository.toUri().toString())
                        .releases(true)
                        .snapshots(true)
                        .knownToExist(true)
                        .build())
                .setRepositories(List.of())
                .setAddCentralRepository(false);
    }

    private static String dependency(String groupId, String artifactId, String version) {
        return "<dependency><groupId>%s</groupId><artifactId>%s</artifactId><version>%s</version></dependency>"
                .formatted(groupId, artifactId, version);
    }

    private static String bomImport(String version) {
        return "<dependency><groupId>%s</groupId><artifactId>chassis-bom</artifactId><version>%s</version><type>pom</type><scope>import</scope></dependency>"
                .formatted(CHASSIS_GROUP, version);
    }

    private static void writePom(Path repository, String groupId, String artifactId, String version, String packaging, String body) {
        Path versionDirectory = artifactDirectory(repository, groupId, artifactId).resolve(version);
        if ("jar".equals(packaging)) {
            // OpenRewrite ignores a jar-packaged POM in a file repository unless a non-empty jar sits beside it
            write(versionDirectory.resolve(artifactId + "-" + version + ".jar"), EMPTY_ZIP);
        }
        write(versionDirectory.resolve(artifactId + "-" + version + ".pom"), """
                <project>
                <modelVersion>4.0.0</modelVersion>
                <groupId>%s</groupId>
                <artifactId>%s</artifactId>
                <version>%s</version>
                <packaging>%s</packaging>
                %s
                </project>
                """.formatted(groupId, artifactId, version, packaging, body));
    }

    static void writeMetadata(Path repository, String artifactId, String... versions) {
        String versionList = Arrays.stream(versions).map("<version>%s</version>"::formatted).collect(Collectors.joining());
        write(artifactDirectory(repository, CHASSIS_GROUP, artifactId).resolve("maven-metadata-local.xml"), """
                <metadata>
                <groupId>%s</groupId>
                <artifactId>%s</artifactId>
                <versioning><versions>%s</versions></versioning>
                </metadata>
                """.formatted(CHASSIS_GROUP, artifactId, versionList));
    }

    private static Path artifactDirectory(Path repository, String groupId, String artifactId) {
        return repository.resolve(groupId.replace('.', '/')).resolve(artifactId);
    }

    private static void write(Path file, String content) {
        write(file, content.getBytes(StandardCharsets.UTF_8));
    }

    private static void write(Path file, byte[] content) {
        try {
            Files.createDirectories(file.getParent());
            Files.write(file, content);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
