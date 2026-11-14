# Deca Compiler (`decac`)

[![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Maven](https://img.shields.io/badge/Apache%20Maven-3.8+-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![ANTLR](https://img.shields.io/badge/ANTLR-4.13.2-red)](https://www.antlr.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Architecture](https://img.shields.io/badge/Target-IMA%2032--bit%20VM-informational)](#compiler-architecture)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen)](https://github.com/MarOuan20004/deca-compiler/actions)

A production-grade, multi-pass static compiler for the **Deca programming language**, targeting the 32-bit abstract register machine **IMA (Machine Abstraite)**. Developed as part of the Software Engineering curriculum at **Grenoble INP - Ensimag**, this compiler implements full object-oriented capabilities, advanced contextual verification, register allocation optimization, and an extended linear algebra matrix engine (**TAB Extension**).

---

## Table of Contents

- [Project Overview](#project-overview)
- [Key Features](#key-features)
- [Compiler Architecture](#compiler-architecture)
- [Command-Line Interface](#command-line-interface)
- [Prerequisites & Build](#prerequisites--build)
- [Quick Start Guide](#quick-start-guide)
- [TAB Extension: Matrices and Arrays](#tab-extension-matrices-and-arrays)
- [Testing & Quality Assurance](#testing--quality-assurance)
- [Repository Organization](#repository-organization)
- [Documentation Index](#documentation-index)
- [Authors & Credits](#authors--credits)
- [License](#license)

---

## Project Overview

The **Deca language** is a strongly-typed, object-oriented language inspired by Java and C++. It features primitive types (`int`, `float`, `boolean`), procedural control flow, single inheritance, dynamic dispatch through Virtual Method Tables, explicit casting, dynamic runtime checks, and an extensibility framework.

The compiler, `decac`, processes Deca source files (`.deca`) and generates optimized assembly files (`.ass`) that run on the `ima` emulator.

```text
               +---------------------------------------------+
               |                decac Driver                 |
               +---------------------------------------------+
                                      |
     +--------------------------------+--------------------------------+
     |                                |                                |
     v                                v                                v
+---------+                      +---------+                      +---------+
| Pass A  |                      | Pass B  |                      | Pass C  |
| Lexer & |   AST (Undecorated)  | Context |    Decorated AST     | Target  |    Target
| Parser  | -------------------> | & Type  | -------------------> | CodeGen | -> Assembly
| (ANTLR) |                      | Checks  |                      |  (IMA)  |    (.ass)
+---------+                      +---------+                      +---------+
```

---

## Key Features

- **Frontend & Grammar (Pass A)**:
  - Lexical and syntactic specification built with ANTLR 4.13.
  - Full support for comments, multi-line strings, escape sequences, and floating-point literals.
  - Safe `#include` directive handling with circular inclusion detection.
  - AST decompiler flag (`-p`) producing syntactically valid Deca from any input AST.

- **Contextual & Semantic Verification (Pass B)**:
  - Robust 3-pass semantic verification resolving forward references and mutual recursion across classes.
  - Inheritance graph validation preventing cyclical class inheritance.
  - Strict type checking, scope environment chaining, visibility enforcement (`protected` fields).
  - Validation of method signatures, overriding rules, assignment subtyping, and return types.
  - Verification-only CLI flag (`-v`) for semantic validation without code generation.

- **Backend & Target Code Generation (Pass C)**:
  - Full code generator targeting the 32-bit IMA abstract register machine.
  - Dynamic register allocation managed by `RegisterManager` with spill-to-stack mechanisms under high register pressure.
  - User-configurable register budget limit (`-r X`) between 4 and 16 registers.
  - Heap memory allocation (`NEW`, `ALLOC`) and stack frame layout management (`TSTO`, `ADDSP`).
  - Virtual Method Tables (VTables) in static memory for runtime dynamic dispatch (`BSR`).
  - Runtime dynamic typing (`instanceof`) and checked explicit casts.
  - Defensive runtime fault handlers: division by zero, null pointer dereference (`dereferencement.null`), heap overflow, and stack overflow.

- **Concurrent Compilation**:
  - Multi-threaded source compilation flag (`-P`) leveraging Java concurrency to compile multiple `.deca` files simultaneously.

- **Linear Algebra & Matrix Engine (TAB Extension)**:
  - Native support for multi-dimensional heap arrays: `new int[n][m]` and `new float[n][m]`.
  - Built-in array primitives: `getElem`, `setElem`, `getNLines`, `getNCols`.
  - Comprehensive linear algebra library: matrix addition, multiplication, transposition, scalar operations, trace, symmetry checks, nilpotency checks, and Gauss-Jordan matrix inversion.

---

## Compiler Architecture

A comprehensive breakdown of all compiler components is available in **[`ARCHITECTURE.md`](ARCHITECTURE.md)**.

### Pipeline Breakdown

1. **Pass A — Lexical & Syntactic Analysis**:
   - `src/main/antlr4/fr/ensimag/deca/syntax/DecaLexer.g4`
   - `src/main/antlr4/fr/ensimag/deca/syntax/DecaParser.g4`
   - Maps the character stream to token streams and builds the undecorated AST (`fr.ensimag.deca.tree`).

2. **Pass B — Contextual Analysis & Typing**:
   - `fr.ensimag.deca.context.EnvironmentType`
   - `fr.ensimag.deca.context.SymbolTable`
   - Decorated AST node classes (`DeclClass`, `DeclMethod`, `Assign`, `Identifier`).
   - Executes in 3 distinct passes: (1) Register classes, (2) Register methods & fields, (3) Type-check statements and expressions.

3. **Pass C — Assembly Code Generation**:
   - `fr.ensimag.deca.codegen.*`
   - `fr.ensimag.ima.pseudocode.*`
   - Maps AST nodes to target IMA instructions (`LOAD`, `STORE`, `ADD`, `MUL`, `BSR`, `RTS`, `BRA`, `BEQ`, `BNE`).

---

## Command-Line Interface

The executable script `bin/decac` accepts the following options:

| Flag | Parameter | Description |
| :--- | :--- | :--- |
| `-b` | None | Displays compiler banner and team identification. |
| `-p` | None | Stops after Pass A and outputs the reconstructed source from the AST. |
| `-v` | None | Stops after Pass B (contextual checks) without generating code. |
| `-d` | None | Enables debug traces. Can be repeated (`-d -d`) to increase verbosity. |
| `-P` | None | Enables parallel compilation of source files using multiple threads. |
| `-r` | `X` (4 to 16) | Restricts register allocation to `X` registers (R0 to RX-1). |
| `<files>` | `.deca` paths | One or more Deca source files to compile. |

---

## Prerequisites & Build

### System Requirements

- **Linux** (x86_64 or aarch64) or **macOS**
- **Java Development Kit**: OpenJDK 21 or higher
- **Apache Maven**: Version 3.6.3 or higher
- **IMA Emulator**: `ima` binary installed in your `PATH`

### Build Instructions

```bash
# Clone the repository
git clone https://github.com/MarOuan20004/deca-compiler.git
cd deca-compiler

# Build and package the compiler
mvn clean package -DskipTests

# Ensure wrapper script is executable
chmod +x bin/decac scripts/*.sh
```

Alternatively, you can run the automated build script:
```bash
./scripts/build.sh
```

---

## Quick Start Guide

### 1. Hello World

Create a file named `hello.deca`:

```deca
{
    println("Hello from the Deca Compiler!");
}
```

Compile and execute:

```bash
# Compile to IMA assembly
./bin/decac hello.deca

# Run on the IMA virtual machine
ima hello.ass
```

Output:
```text
Hello from the Deca Compiler!
```

### 2. Object-Oriented Programming & Polymorphism

```deca
class Animal {
    void speak() {
        println("Generic animal sound");
    }
}

class Dog extends Animal {
    void speak() {
        println("Woof!");
    }
}

{
    Animal a = new Dog();
    a.speak(); // Dynamically dispatches to Dog.speak() via VTable
}
```

### 3. Parallel Compilation

Compile multiple Deca programs in parallel:

```bash
./bin/decac -P program1.deca program2.deca program3.deca
```

---

## TAB Extension: Matrices and Arrays

The TAB extension integrates linear algebra and multi-dimensional matrices directly into Deca:

```deca
#include "Math.decah"

{
    int[][] I = _unite(3);                  // 3x3 Identity matrix
    int[][] C = _matriceConstante(3, 3, 2); // 3x3 matrix filled with 2
    int[][] sum = _addition(I, C);          // Matrix addition
    int val = getElem(sum, 0, 0);          // Read element: 1 + 2 = 3

    println("Element at (0, 0): ", val);
    println("Trace of sum: ", _trace(sum));
}
```

### Available Matrix Routines

| Function | Signature | Description |
| :--- | :--- | :--- |
| `_unite` | `int[][] _unite(int n)` | Generates an `n x n` identity matrix. |
| `_matriceNulle` | `int[][] _matriceNulle(int n, int m)` | Generates an `n x m` zero matrix. |
| `_matriceConstante`| `int[][] _matriceConstante(int n, int m, int v)` | Generates an `n x m` matrix initialized to `v`. |
| `_transposee` | `int[][] _transposee(int[][] A)` | Computes matrix transposition $A^T$. |
| `_addition` | `int[][] _addition(int[][] A, int[][] B)` | Computes matrix sum $A + B$. |
| `_produit` | `int[][] _produit(int[][] A, int[][] B)` | Computes matrix product $A \times B$. |
| `_scalaire` | `int[][] _scalaire(int[][] A, int v)` | Multiplies matrix $A$ by scalar $v$. |
| `_trace` | `int _trace(int[][] A)` | Computes matrix trace $\text{Tr}(A)$. |
| `_estSymetrique` | `boolean _estSymetrique(int[][] A)` | Verifies if $A = A^T$. |
| `_estNilpotente` | `boolean _estNilpotente(int[][] A, int k)` | Verifies if $A^k = 0$. |
| `determinant` | `float determinant(float[][] M)` | Computes matrix determinant via Gaussian elimination. |

---

## Testing & Quality Assurance

The compiler includes an extensive automated test suite covering unit tests, contextual validation, code generation, and error conditions.

```bash
# Run the complete validation suite
mvn test < /dev/null

# Or use the convenience script
./scripts/test.sh
```

### Test Harness Organization

- `src/test/deca/syntax/`: Tests for Pass A (valid and invalid lexer/parser inputs).
- `src/test/deca/context/`: Tests for Pass B (type checking, subtyping, scoping rules).
- `src/test/deca/codegen/`: Tests for Pass C (execution verification on the IMA emulator).
- `src/test/script/`: Automated bash test runners executed during Maven testing phases.
- `target/site/jacoco/`: Code coverage report generated via JaCoCo.

---

## Repository Organization

```text
.
├── bin/                        # Binary distribution directory
│   ├── Deca-*-jar-...jar       # Standalone compiler JAR with dependencies
│   └── decac                   # Executable shell wrapper
├── docs/                       # Official engineering documentation
│   ├── Manuel_Utilisateur.pdf  # Comprehensive user manual
│   ├── validation.pdf          # Full test and verification report
│   ├── README.md               # Documentation directory index
│   └── suivi{1,2,3}/           # Milestone reviews and technical slides
├── examples/                   # Curated Deca source examples
│   ├── deca/                   # Example .deca programs (basics, OOP, TAB)
│   └── README.md               # Example catalog and execution instructions
├── planning/                   # Project planning and Gantt tracking files
├── scripts/                    # Convenience developer scripts (build, test, run, clean)
├── src/
│   ├── main/
│   │   ├── antlr4/             # Lexical and syntactic ANTLR grammars
│   │   ├── java/               # Compiler source code (tree, context, codegen, ima)
│   │   └── resources/include/  # Standard library headers (Math.decah)
│   └── test/                   # Unit tests, integration tests, and test harness scripts
├── .github/
│   ├── workflows/              # GitHub Actions CI and CodeQL workflows
│   └── ISSUE_TEMPLATE/         # Bug report and feature request templates
├── ARCHITECTURE.md             # In-depth architectural design document
├── CHANGELOG.md                # Project version history and milestones
├── CONTRIBUTING.md             # Contributor guidelines and Git workflow
├── LICENSE                     # MIT License
├── README.md                   # Project overview (this file)
└── pom.xml                     # Maven project configuration
```

---

## Documentation Index

- **[Compiler Architecture (`ARCHITECTURE.md`)](ARCHITECTURE.md)**: Deep dive into the 3-pass compiler, symbol tables, register allocation, and memory layout.
- **[User Manual (PDF)](file:///home/ensimag/GL_FINAL/gl10/docs/Manuel_Utilisateur.pdf)**: Official user reference document detailing command-line options and grammar specifications.
- **[Validation Report (PDF)](file:///home/ensimag/GL_FINAL/gl10/docs/validation.pdf)**: Formal test report documenting test matrices, oracles, and coverage results.
- **[Examples Directory (`examples/`)](examples/README.md)**: Runnable sample programs showcasing language features.
- **[Contribution Guidelines (`CONTRIBUTING.md`)](CONTRIBUTING.md)**: Coding style, branching strategy, and pull request procedures.
- **[Changelog (`CHANGELOG.md`)](CHANGELOG.md)**: Chronological log of versions and enhancements.

---

## Authors & Credits

### Project Lead & Maintainer
- **Marwane Hajjy** ([@MarOuan20004](https://github.com/MarOuan20004)) — Architecture, Contextual Analysis, Code Generation, and Repository Maintenance.

### GL10 Team Co-Authors (Grenoble INP - Ensimag)
- **Amine Lahmouz** — Contextual Analysis, Object-Oriented AST, and Code Generation.
- **Marwane Hajjy** — Code Generation, Dynamic Dispatch, Register Allocation, and Runtime Environment.
- **Mohyieddine Kamal** — Contextual Analysis, Symbol Tables, and Type Environments.
- **Mouad El-Lahoua** — Lexer & Parser, TAB Matrix Extension, and Standard Math Library.
- **Salim Bassalh** — Lexical & Syntactic Analysis, AST Construction, and Error Diagnostics.

### Academic Supervision
- Developed within the **Génie Logiciel (GL)** curriculum at **Grenoble INP - Ensimag** (Promotion 2026).
- Supervised by the Ensimag Computer Science faculty.

---

## License

This project is licensed under the **MIT License**. See the **[`LICENSE`](LICENSE)** file for full terms.
