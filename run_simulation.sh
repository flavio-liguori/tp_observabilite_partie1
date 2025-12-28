#!/bin/bash
# Re-compile to ensure simulation package is included
echo "Compiling instrumented code and simulation..."
# We need to compile the simulation code against the INSTRUMENTED repository
# Step 1: Compile instrumented repository
mkdir -p target/instrumented-classes

# Spoon generated sources need to be compiled
find target/generated-sources/spoon -name "*.java" > sources_gen.txt
javac -cp "target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout)" -d target/instrumented-classes @sources_gen.txt

# Step 2: Compile the simulation generator (which is in src/main/java)
# warning: ScenarioGenerator uses ProductRepository. We want it to use the instrumented one.
# So we compile ScenarioGenerator putting instrumented-classes in classpath AHEAD of src
javac -cp "target/instrumented-classes:src/main/java:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout)" -d target/instrumented-classes src/main/java/com/observability/simulation/ScenarioGenerator.java

mkdir -p logs
echo "Running Simulation... (Output to logs/simulation.log)"
java -cp "target/instrumented-classes:src/main/resources:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout)" com.observability.simulation.ScenarioGenerator > logs/simulation.log 2>&1
echo "Simulation done. Check logs/simulation.log"
