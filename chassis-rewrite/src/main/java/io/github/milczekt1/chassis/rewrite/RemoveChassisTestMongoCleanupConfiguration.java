package io.github.milczekt1.chassis.rewrite;

import org.jspecify.annotations.Nullable;
import org.openrewrite.ExecutionContext;
import org.openrewrite.PathUtils;
import org.openrewrite.Recipe;
import org.openrewrite.Tree;
import org.openrewrite.TreeVisitor;
import org.openrewrite.internal.ListUtils;
import org.openrewrite.java.AnnotationMatcher;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.RemoveAnnotationVisitor;
import org.openrewrite.java.tree.Expression;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.TypeUtils;
import org.openrewrite.text.PlainText;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class RemoveChassisTestMongoCleanupConfiguration extends Recipe {

    private static final String CLEANUP_CONFIGURATION =
            "io.github.milczekt1.chassis.test.mongo.ChassisTestMongoCleanupConfiguration";
    private static final String SIMPLE_NAME = "ChassisTestMongoCleanupConfiguration";
    private static final List<String> IMPORT_ANNOTATIONS = List.of(
            "org.springframework.context.annotation.Import",
            "org.springframework.boot.autoconfigure.ImportAutoConfiguration");

    private static final String CLASS_NAME = Pattern.quote(CLEANUP_CONFIGURATION) + "(?![\\w$.])";
    private static final String CONTINUATION = "(?:\\\\\\R[ \\t]*)?";

    // Spring's ImportCandidates ignores whatever follows a '#'
    private static final String IMPORTS_LINE_END = "[ \\t]*(?:#[^\\r\\n]*)?";
    private static final Pattern IMPORTS_LINE = Pattern.compile("(?m)^[ \\t]*" + CLASS_NAME + IMPORTS_LINE_END + "\\R");
    private static final Pattern IMPORTS_LAST_LINE = Pattern.compile("(?:\\R|\\A)[ \\t]*" + CLASS_NAME + IMPORTS_LINE_END + "\\z");

    private static final Pattern FACTORIES_ONLY_VALUE = Pattern.compile(
            "(?m)^[^=:\\r\\n]+[=:][ \\t]*" + CONTINUATION + CLASS_NAME + "[ \\t]*(?:\\R|\\z)");
    private static final Pattern FACTORIES_VALUE_BEFORE_ANOTHER = Pattern.compile(
            CLASS_NAME + "[ \\t]*,[ \\t]*" + CONTINUATION);
    private static final Pattern FACTORIES_LAST_VALUE = Pattern.compile(
            "[ \\t]*,[ \\t]*" + CONTINUATION + CLASS_NAME);

    @Override
    public String getDisplayName() {
        return "Remove references to ChassisTestMongoCleanupConfiguration";
    }

    @Override
    public String getDescription() {
        return "Needed when upgrading from Chassis 0.5.5 or earlier: `ChassisTestMongoCleanupConfiguration` was deleted. " +
               "Removes it from `@Import` and `@ImportAutoConfiguration`, and from `META-INF/spring/*.imports` and " +
               "`META-INF/spring.factories`. Other references are left for the compiler to report.";
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new TreeVisitor<Tree, ExecutionContext>() {
            @Override
            public @Nullable Tree visit(@Nullable Tree tree, ExecutionContext ctx) {
                if (tree instanceof J.CompilationUnit) {
                    return new ImportAnnotationsEditor().visit(tree, ctx);
                }
                if (tree instanceof PlainText text) {
                    return edit(text);
                }
                return tree;
            }
        };
    }

    private static PlainText edit(PlainText text) {
        String before = text.getText();
        String after;
        if (PathUtils.matchesGlob(text.getSourcePath(), "**/META-INF/spring/*.imports")) {
            after = IMPORTS_LINE.matcher(before).replaceAll("");
            after = IMPORTS_LAST_LINE.matcher(after).replaceAll("");
        } else if (PathUtils.matchesGlob(text.getSourcePath(), "**/META-INF/spring.factories")) {
            after = FACTORIES_ONLY_VALUE.matcher(before).replaceAll("");
            after = FACTORIES_VALUE_BEFORE_ANOTHER.matcher(after).replaceAll("");
            after = FACTORIES_LAST_VALUE.matcher(after).replaceAll("");
        } else {
            return text;
        }
        return after.equals(before) ? text : text.withText(after);
    }

    /**
     * Matches {@code ChassisTestMongoCleanupConfiguration.class}. Once a service is on a Chassis without the class, its
     * type no longer resolves, so the literal is then matched by name: fully qualified, or simple and imported.
     */
    private static boolean isCleanupConfigurationLiteral(Expression expression, boolean importsCleanupConfiguration) {
        if (!(expression instanceof J.FieldAccess fieldAccess) || !"class".equals(fieldAccess.getSimpleName())) {
            return false;
        }
        Expression target = fieldAccess.getTarget();
        if (TypeUtils.asFullyQualified(target.getType()) != null) {
            return TypeUtils.isOfClassType(target.getType(), CLEANUP_CONFIGURATION);
        }
        String name = qualifiedName(target);
        return CLEANUP_CONFIGURATION.equals(name) || importsCleanupConfiguration && SIMPLE_NAME.equals(name);
    }

    private static @Nullable String qualifiedName(Expression expression) {
        if (expression instanceof J.Identifier identifier) {
            return identifier.getSimpleName();
        }
        if (expression instanceof J.FieldAccess fieldAccess) {
            String target = qualifiedName(fieldAccess.getTarget());
            return target == null ? null : target + "." + fieldAccess.getSimpleName();
        }
        return null;
    }

    /**
     * Removes the matching elements; the element after a removed first element takes its prefix, so no space is left
     * after the opening parenthesis or brace.
     */
    private static <T extends J> List<T> removeAll(List<T> elements, Predicate<T> remove) {
        List<T> kept = ListUtils.map(elements, element -> remove.test(element) ? null : element);
        if (kept.size() == elements.size() || kept.isEmpty() || !remove.test(elements.getFirst())) {
            return kept;
        }
        return ListUtils.mapFirst(kept, first -> first.withPrefix(elements.getFirst().getPrefix()));
    }

    private static final class ImportAnnotationsEditor extends JavaIsoVisitor<ExecutionContext> {

        @Override
        public J.Annotation visitAnnotation(J.Annotation annotation, ExecutionContext ctx) {
            J.Annotation a = super.visitAnnotation(annotation, ctx);
            // getArguments() builds a new list on every call; the identity check below needs this one
            List<Expression> original = a.getArguments();
            if (original == null ||
                IMPORT_ANNOTATIONS.stream().noneMatch(type -> TypeUtils.isOfClassType(a.getType(), type))) {
                return a;
            }
            boolean importsCleanupConfiguration = getCursor().firstEnclosingOrThrow(J.CompilationUnit.class).getImports()
                    .stream()
                    .anyMatch(anImport -> !anImport.isStatic() && CLEANUP_CONFIGURATION.equals(anImport.getTypeName()));
            Predicate<Expression> isCleanupLiteral = e -> isCleanupConfigurationLiteral(e, importsCleanupConfiguration);
            List<Expression> edited = ListUtils.map(original, argument -> withoutCleanupConfiguration(argument, isCleanupLiteral));
            // identity, not equals(): LST elements compare by id, so an edited array equals the original
            if (edited == original) {
                return a;
            }
            List<Expression> arguments = removeAll(edited, argument -> argument instanceof J.Empty);
            maybeRemoveImport(CLEANUP_CONFIGURATION);
            if (arguments.isEmpty()) {
                UUID id = a.getId();
                doAfterVisit(new RemoveAnnotationVisitor(new AnnotationMatcher("@" + IMPORT_ANNOTATIONS.getFirst()) {
                    @Override
                    public boolean matches(J.Annotation candidate) {
                        return candidate.getId().equals(id);
                    }
                }));
            }
            return a.withArguments(arguments);
        }

        /**
         * Returns the argument without the class literal, or {@link J.Empty} when nothing is left of it.
         */
        private static Expression withoutCleanupConfiguration(Expression argument, Predicate<Expression> isCleanupLiteral) {
            if (argument instanceof J.Assignment assignment) {
                Expression value = withoutCleanupConfiguration(assignment.getAssignment(), isCleanupLiteral);
                return value instanceof J.Empty
                        ? new J.Empty(Tree.randomId(), assignment.getPrefix(), assignment.getMarkers())
                        : assignment.withAssignment(value);
            }
            if (isCleanupLiteral.test(argument)) {
                return new J.Empty(Tree.randomId(), argument.getPrefix(), argument.getMarkers());
            }
            if (argument instanceof J.NewArray array && array.getInitializer() != null) {
                List<Expression> initializer = removeAll(array.getInitializer(), isCleanupLiteral);
                if (initializer.size() == array.getInitializer().size()) {
                    return array;
                }
                return initializer.isEmpty()
                        ? new J.Empty(Tree.randomId(), array.getPrefix(), array.getMarkers())
                        : array.withInitializer(initializer);
            }
            return argument;
        }
    }
}
