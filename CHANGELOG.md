# Changelog

All notable changes to the **Deca Compiler (`decac`)** project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.0.0] - 2026-01-22

### Added
* **TAB Extension (Matrices & 2D Arrays)**:
  * Syntax support for multidimensional array allocations: `new int[n][m]` and `new float[n][m]`.
  * Built-in indexing and mutation primitives: `getElem`, `setElem`, `getNLines`, `getNCols`.
  * Comprehensive linear algebra functions: `_unite`, `_matriceNulle`, `_matriceConstante`, `_transposee`, `_addition`, `_produit`, `_scalaire`, `_trace`, `_egale`, `_estSymetrique`, `_estAntisymetrique`, `_estNilpotente`.
  * Numerical algorithms: Gauss-Jordan matrix inversion and matrix determinant calculation in `Math.decah`.
* **Standard Math Library**:
  * Deca implementation of `Math.decah` supporting `sqrt`, `abs`, `ulp`, and algebraic routines.
* **CLI Features**:
  * Parallel source compilation option (`-P`) using Java concurrency utilities.
  * Configurable register budget option (`-r X`) allowing restriction between 4 and 16 registers.
  * Syntax decompiler flag (`-p`) reconstructing valid Deca from AST without semantic analysis.
  * Context verification stopper flag (`-v`) stopping execution prior to code generation.
* **Documentation & Validation**:
  * Complete User Manual (`Manuel_Utilisateur.pdf`).
  * Comprehensive validation report covering test matrices and coverage reports (`validation.pdf`).

### Changed
* Hardened register allocation routines under high register pressure with spill-to-stack mechanisms.
* Enhanced error reporting with precise location tracking and context-aware diagnostics.

---

## [0.3.0] - 2026-01-18

### Added
* **IMA Code Generator (Pass C)**:
  * Complete target code generation for the IMA abstract register machine.
  * Virtual Method Tables (VTable) construction in memory heap for dynamic dispatch (`BSR`).
  * Class instantiation (`new`), field initialization, and inheritance memory layout.
  * Method invocation handling with parameter passing via stack and `this` pointer in register `R2`.
  * Dynamic type checking support: `instanceof` and runtime explicit cast validation with branch labels.
  * Arithmetic and boolean evaluation using temporary registers managed by `RegisterManager`.
  * Runtime error detection handlers: division by zero, null pointer dereference (`dereferencement.null`), heap overflow, stack overflow.

---

## [0.2.0] - 2026-01-14

### Added
* **Contextual Verification Engine (Pass B)**:
  * 3-pass contextual analysis algorithm for object-oriented Deca:
    * Pass 1: Class hierarchy construction, inheritance loop detection, object root initialization.
    * Pass 2: Field and method signature verification, method overriding rules, signature compatibility.
    * Pass 3: Method body and expression type checking, scope environment resolution.
  * Symbol table (`SymbolTable.java`) with lexical scope chaining.
  * Type environment manager (`EnvironmentType.java`) tracking primitive and reference types.
  * Semantic rules for assignment compatibility, subtyping, and operator operands.

---

## [0.1.0] - 2026-01-08

### Added
* **Frontend Lexer & Parser (Pass A)**:
  * ANTLR4 lexical grammar (`DecaLexer.g4`) and syntactic grammar (`DecaParser.g4`).
  * Abstract Syntax Tree (AST) node hierarchy under `fr.ensimag.deca.tree`.
  * Support for primitive types (`int`, `float`, `boolean`), operators, control flow statements (`while`, `if-then-else`), and I/O (`print`, `println`, `readInt`, `readFloat`).
  * `#include` preprocessor directive with circular include detection.
  * AST pretty-printing and decompiler routines.
