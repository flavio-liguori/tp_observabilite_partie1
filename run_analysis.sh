#!/bin/bash
# Must compile the analysis package
echo "Compiling Analyzer..."
javac -cp "src/main/java:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout)" -d target/classes src/main/java/com/observability/analysis/*.java

echo "Running Analysis on logs/simulation.log..."
java -cp "target/classes" com.observability.analysis.ProfileAnalyzer logs/simulation.log
