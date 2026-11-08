package io.github.milczekt1.chassis.rules.internal;

import com.tngtech.archunit.core.importer.Location;
import com.tngtech.archunit.junit.LocationProvider;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Scans only the Maven module the test class was compiled in, so sibling modules on the classpath — as
 * directories under {@code test}, as jars under {@code verify} — are never analyzed.
 */
public final class CurrentModuleLocations implements LocationProvider {

    @Override
    public Set<Location> get(Class<?> testClass) {
        Path testOutput = codeSourceOf(testClass);
        Path target = testOutput.getParent();
        if (!Files.isDirectory(testOutput)
                || !testOutput.getFileName().toString().equals("test-classes")
                || target == null
                || !target.getFileName().toString().equals("target")) {
            throw new IllegalStateException("Expected " + testClass.getName()
                    + " to be loaded from a module's target/test-classes directory, but it was loaded from "
                    + testOutput);
        }
        Set<Location> locations = new LinkedHashSet<>();
        locations.add(Location.of(testOutput));
        Path productionOutput = target.resolve("classes");
        if (Files.isDirectory(productionOutput)) {
            locations.add(Location.of(productionOutput));
        }
        return locations;
    }

    private static Path codeSourceOf(Class<?> testClass) {
        try {
            return Path.of(testClass.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Cannot resolve the code source of " + testClass.getName(), e);
        }
    }
}
