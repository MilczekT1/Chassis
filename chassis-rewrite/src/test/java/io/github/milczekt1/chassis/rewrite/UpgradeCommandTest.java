package io.github.milczekt1.chassis.rewrite;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpgradeCommandTest {

    private static final Pattern BUILT_FOR = Pattern.compile("(?m)Run it with rewrite-maven-plugin (\\S+)\\.$");
    private static final Pattern README_PLUGIN = Pattern.compile("rewrite-maven-plugin:([^:\\s]+):");

    @Test
    void readmeRunsThePluginVersionTheRecipeIsBuiltFor() throws IOException {
        Matcher builtFor = BUILT_FOR.matcher(ChassisTestRepository.upgradeChassisYaml());
        assertTrue(builtFor.find(), "UpgradeChassis's description names no rewrite-maven-plugin version");

        List<String> readmeVersions = README_PLUGIN.matcher(Files.readString(Path.of("..", "README.md")))
                .results()
                .map(match -> match.group(1))
                .toList();
        assertFalse(readmeVersions.isEmpty(), "README.md has no rewrite-maven-plugin command");
        assertTrue(readmeVersions.stream().allMatch(builtFor.group(1)::equals),
                () -> "README.md runs rewrite-maven-plugin " + readmeVersions + ", the recipe is built for " + builtFor.group(1));
    }
}
