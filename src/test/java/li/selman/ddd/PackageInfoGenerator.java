package li.selman.ddd;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.Name;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PackageInfoGenerator {
    private static final Set<String> REQUIRED_ANNOTATIONS = Set.of("NonNullApi", "NonNullFields");

    private static final Set<String> ANNOTATION_IMPORTS =
            Set.of("org.springframework.lang.NonNullApi", "org.springframework.lang.NonNullFields");

    public static void updatePackageInfoFiles(String rootDir, String basePackage) throws IOException {
        List<String> packages = findPackages(Paths.get(rootDir), basePackage);

        for (String packageName : packages) {
            Path packageInfoPath = Paths.get(rootDir, packageName.replace('.', '/'), "package-info.java");

            if (Files.exists(packageInfoPath)) {
                updateExistingPackageInfo(packageInfoPath);
            } else {
                generateNewPackageInfo(packageInfoPath, packageName);
            }
        }
    }

    private static void updateExistingPackageInfo(Path packageInfoPath) throws IOException {
        JavaParser parser = new JavaParser();
        String content = Files.readString(packageInfoPath);
        Optional<CompilationUnit> result = parser.parse(content).getResult();

        if (result.isPresent()) {
            CompilationUnit cu = result.get();
            boolean modified = false;

            // Add imports if not present
            for (String importName : ANNOTATION_IMPORTS) {
                if (cu.getImports().stream()
                        .noneMatch(imp -> imp.getNameAsString().equals(importName))) {
                    cu.addImport(importName);
                    modified = true;
                }
            }

            // Get existing package annotations
            NodeList<AnnotationExpr> annotations = new NodeList<>();
            if (cu.getPackageDeclaration().isPresent()) {
                annotations = cu.getPackageDeclaration().get().getAnnotations();
            }

            // Check which required annotations are missing
            Set<String> existingAnnotations =
                    annotations.stream().map(AnnotationExpr::getNameAsString).collect(Collectors.toSet());

            // Add missing annotations
            for (String required : REQUIRED_ANNOTATIONS) {
                if (!existingAnnotations.contains(required)) {
                    annotations.add(new MarkerAnnotationExpr(new Name(required)));
                    modified = true;
                }
            }

            // Only write if we made changes
            if (modified) {
                String updatedContent = cu.toString();
                Files.writeString(packageInfoPath, updatedContent);
                System.out.println("Updated " + packageInfoPath);
            }
        }
    }

    private static void generateNewPackageInfo(Path packageInfoPath, String packageName) throws IOException {
        // Create new package-info.java content directly
        StringBuilder content = new StringBuilder();

        // Add annotations and package declaration
        content.append("@NonNullApi\n");
        content.append("@NonNullFields\n");
        content.append("package ").append(packageName).append(";\n\n");

        // Add imports
        content.append("import org.springframework.lang.NonNullApi;\n");
        content.append("import org.springframework.lang.NonNullFields;\n\n");

        // Write the file
        Files.writeString(packageInfoPath, content.toString());
        System.out.println("Generated new package-info.java for " + packageName);
    }

    private static List<String> findPackages(Path rootDir, String excludePackage) throws IOException {
        String excludePath = excludePackage.replace('.', '/');
        Path realRoot = Paths.get(rootDir.toString(), excludePath);

        try (Stream<Path> pathStream = Files.walk(realRoot)) {
            return pathStream
                    .filter(Files::isDirectory)
                    .map(path -> realRoot.relativize(path).toString())
                    .filter(path -> !path.isEmpty()) // exclude root directory
                    .map(path -> path.replace('/', '.'))
                    .map(path -> excludePackage + "." + path)
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    public static void main(String[] args) throws IOException {
        updatePackageInfoFiles("src/main/java", "li.selman");
    }
}