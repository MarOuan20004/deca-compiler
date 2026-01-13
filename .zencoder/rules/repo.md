---
description: Repository Information Overview
alwaysApply: true
---

# Deca Compiler Information

## Summary
The **Deca Compiler** is a Java-based project developed for the "Projet Génie Logiciel" at Ensimag. It is designed to compile the **Deca language**, featuring a modular architecture that includes a lexer, parser (using **ANTLR4**), and code generation targeting the **IMA** (Indirection Machine) virtual machine.

## Structure
- **src/main/java**: Core Java source code for the compiler.
- **src/main/antlr4**: ANTLR4 grammar definitions for the Deca language.
- **src/test**: Comprehensive test suite including Java unit tests and shell-based integration tests.
- **bin**: Contains the `decac` wrapper script for running the compiler.
- **docker**: Dockerfile and environment setup for an Ensimag-like development environment.
- **docs**: Project documentation, progress reports, and architectural specifications.
- **examples**: Sample Deca programs for testing and demonstration.

## Language & Runtime
**Language**: Java  
**Version**: Java 21  
**Build System**: Maven  
**Package Manager**: Maven (v3.6.3+ required)

## Dependencies
**Main Dependencies**:
- **ANTLR runtime** (v4.13.2): For lexing and parsing.
- **Log4j** (v1.2.17): For logging.
- **Commons-lang** (v2.6): For utility functions and preconditions.

**Development Dependencies**:
- **JUnit Jupiter** (v6.1.0-M1): For unit testing.
- **Mockito** (v5.20.0): For mocking in tests.
- **JaCoCo** (v0.8.13): For code coverage reports.

## Build & Installation
```bash
# Compile the project
mvn compile

# Package the compiler into an executable jar and wrapper
mvn package
```

## Docker
**Dockerfile**: `docker/Dockerfile`  
**Image**: `projetgl`  
**Configuration**: Based on `ubuntu:24.04`, it installs JDK 23, Maven, and sets up a `gl` user. It is designed to replicate the Ensimag environment and includes `ima` tools for executing compiled code.

## Testing
**Framework**: JUnit 5 (Jupiter) and custom Shell scripts.  
**Test Location**: `src/test/java` (unit tests) and `src/test/script` (integration scripts).  
**Naming Convention**: Standard Maven/JUnit patterns and `.sh` scripts for integration.  
**Configuration**: `pom.xml` handles test execution via `maven-surefire-plugin`.

**Run Command**:
```bash
# Run all tests
mvn test

# Run specific integration scripts
./src/test/script/basic-lex.sh
./src/test/script/common-tests.sh
```

## Main Files & Resources
- **Entry Point**: `fr.ensimag.deca.DecacMain` (Main class for the compiler).
- **Wrapper Script**: `bin/decac` (Shell script to run the compiler).
- **Grammar Files**: Located in `src/main/antlr4/fr/ensimag/deca/DecaLexer.g4` and `DecaParser.g4`.
- **Include Files**: Deca standard libraries in `src/main/resources/include`.
