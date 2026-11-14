# Contributing to the Deca Compiler

Thank you for your interest in contributing to the **Deca Compiler (`decac`)**. This project is developed according to rigorous software engineering standards established at Grenoble INP - Ensimag.

---

## 1. Development Prerequisites

Ensure your development environment meets the following requirements:
* **Java Development Kit (JDK)**: OpenJDK 21 or higher.
* **Apache Maven**: Version 3.6.3 or higher.
* **IMA Emulator** (`ima`): Virtual machine for executing generated assembly files (IMA bytecode).
* **Git**: Version 2.30 or higher.

Verify your environment:
```bash
java -version
mvn -version
```

---

## 2. Getting Started & Build Instructions

1. **Clone the repository**:
   ```bash
   git clone https://github.com/MarOuan20004/deca-compiler.git
   cd deca-compiler
   ```

2. **Compile and assemble the project**:
   ```bash
   mvn clean package -DskipTests
   ```
   This generates the standalone compiler JAR at `target/Deca-0.0.1-SNAPSHOT-jar-with-dependencies.jar` and packages the wrapper script at `bin/decac`.

3. **Verify the executable**:
   ```bash
   ./bin/decac -b
   ```

---

## 3. Branching and Workflow Model

We follow a feature-branch workflow based on the `main` branch:

* `main`: Stable, release-ready branch. Direct pushes to `main` are restricted.
* `feature/<feature-name>`: Dedicated branches for new features or extensions.
* `bugfix/<issue-description>`: Dedicated branches for bug fixes and patches.
* `refactor/<component>`: Dedicated branches for architectural refactorings.

### Branch Lifecycle:
```bash
git checkout main
git pull origin main
git checkout -b feature/register-allocation-optim
# Make your changes and test thoroughly
git commit -m "feat(codegen): implement graph coloring register allocation"
git push origin feature/register-allocation-optim
```

---

## 4. Commit Message Conventions

Commit messages must follow the [Conventional Commits](https://www.conventionalcommits.org/) specification:

```text
<type>(<scope>): <short description>

[optional body explaining context and rationale]

[optional footer referencing issue numbers, e.g., Closes #12]
```

### Allowed Types:
* `feat`: A new feature or language construct.
* `fix`: A bug fix in any compilation phase.
* `docs`: Documentation updates (README, ARCHITECTURE, manuals).
* `test`: Adding or refining test suites and test scripts.
* `refactor`: Code changes that neither fix a bug nor add a feature.
* `perf`: Changes aimed at improving compilation speed or generated code efficiency.
* `ci`: GitHub Actions workflow or build configuration changes.

### Examples:
* `feat(extension): add 2D matrix transpose and trace routines`
* `fix(context): prevent invalid subtype assignment on primitive floats`
* `docs(architecture): document virtual method table memory layout`

---

## 5. Testing Requirements

All contributions must include appropriate automated tests and must not introduce regressions:

1. **Unit & Integration Tests**:
   ```bash
   # Run full test suite
   mvn test < /dev/null
   ```

2. **Validation by Compiler Pass**:
   * **Syntax & Lexer**: Check `src/test/script/syntax-*.sh`
   * **Contextual Checking**: Check `src/test/script/context-*.sh`
   * **Code Generation**: Check `src/test/script/codegen-*.sh`

3. **Writing New Tests**:
   Place test files in their corresponding directories:
   * Valid test programs: `src/test/deca/<pass>/valid/`
   * Expected failure tests: `src/test/deca/<pass>/invalid/`

---

## 6. Code Style & Engineering Guidelines

* **AST Design**: Keep AST nodes immutable where feasible; decorate nodes during Pass B rather than altering the syntactic tree structure.
* **Error Reporting**: All errors must output user-friendly diagnostic messages with precise line and column location:
  ```text
  file.deca:line:col: Error description
  ```
* **No Hardcoded Register Assumptions**: Always route register requests through `RegisterManager` and respect the `-r X` limit.
* **Documentation Integrity**: Maintain clear Javadoc comments on public APIs, classes, and visitor methods.
