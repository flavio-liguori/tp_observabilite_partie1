package com.observability.spoon;

import spoon.Launcher;

public class SpoonApplier {
    public static void main(String[] args) {
        Launcher launcher = new Launcher();

        launcher.addInputResource("src/main/java");

        launcher.setSourceOutputDirectory("target/generated-sources/spoon");

        launcher.addProcessor(new LogProcessor());

        launcher.getEnvironment().setAutoImports(true);
        launcher.getEnvironment().setNoClasspath(true);

        // Run
        launcher.run();

        System.out.println("Transformation Spoon terminée. Code généré dans target/generated-sources/spoon");
    }
}
