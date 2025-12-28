#!/bin/bash
# Compiling instrumented code from Spoon output
echo "Compiling instrumented code..."
mkdir -p target/instrumented-classes

# Build classpath
CP=$(mvn dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q)

# Compile all files in generated-sources/spoon
# We use find to get all java files
find target/generated-sources/spoon -name "*.java" > sources.txt
javac -cp "target/classes:$CP" -d target/instrumented-classes @sources.txt

# Run
echo "Running instrumented application..."
java -cp "target/instrumented-classes:src/main/resources:$CP" com.observability.Main
