package com.observability.spoon;
import spoon.Launcher;
public class SpoonApplier {
    public static void main(String[] args) {
        Launcher launcher = new Launcher();
        // Input sources
        launcher.addInputResource("src/main/java");
        // Don't process the processor itself or applier to avoid recursion loop if
        // running multiple times
        // But for this run it's fine.
        // Output directory for instrumented code
        launcher.setSourceOutputDirectory("target/generated-sources/spoon");
        // Add processor
        launcher.addProcessor(new LogProcessor());
        // Build model
        launcher.getEnvironment().setAutoImports(true);
        launcher.getEnvironment().setNoClasspath(true);// Since we just need source structure mainly

        // Run
        launcher.run();
        System.out.println("Transformation Spoon terminée. Code généré dans target/generated-sources/spoon");
    }
}