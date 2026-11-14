# Architecture of the Deca Compiler (`decac`)

This document provides a comprehensive technical overview of the architecture, internal abstractions, and design principles implemented in the **Deca Compiler (`decac`)**.

---

## 1. High-Level Pipeline

The compiler implements a classic multi-pass compilation architecture that transforms high-level Deca source code into assembly bytecode targeting the **IMA (Machine Abstraite)** virtual processor:

```text
+-----------------------------------------------------------------------------------+
|                                 Compilation Pipeline                              |
+-----------------------------------------------------------------------------------+
  Source File (.deca)
       |
       v
+------------------+     [ANTLR 4.13]
|  Pass A: Lexer   |  -> Token stream with '#include' resolution
|     & Parser     |  -> Abstract Syntax Tree (AST) construction
+------------------+
       |
       v AST (undecorated)
+------------------+     [Semantic Verification]
|  Pass B: Context |  -> Pass 1: Build class hierarchy & inheritance graph
|     Analysis     |  -> Pass 2: Verify fields & method signatures
|  & Type Checking |  -> Pass 3: Type check expressions & decorate AST nodes
+------------------+
       |
       v Decorated AST (typed identifiers, definitions, symbol bindings)
+------------------+     [Code Generation]
|  Pass C: Backend |  -> Construct Virtual Method Tables (VTables)
|  Code Generator  |  -> Register allocation & expression evaluation
|     for IMA      |  -> Emit IMA assembly instructions & runtime error handlers
+------------------+
       |
       v
  Target File (.ass) -> Executed by the IMA Virtual Machine
```

---

## 2. Pass A: Lexical and Syntactic Analysis

### 2.1 Grammar & ANTLR4 Integration
* **Lexer (`DecaLexer.g4`)**: Recognizes keywords, literals (`int`, `float`, `boolean`, `string`), operators, identifiers, and special directives.
* **Pre-processor Inclusion**: Supports `#include "file.decah"` through custom lexer stream manipulation in [`AbstractDecaLexer.java`](file:///home/ensimag/GL_FINAL/gl10/src/main/java/fr/ensimag/deca/syntax/AbstractDecaLexer.java). It checks for file existence both locally and in system resources (`src/main/resources/include/`), while guarding against circular dependencies.
* **Parser (`DecaParser.g4`)**: Constructs an Abstract Syntax Tree (AST) rooted at `Program`. The grammar strictly isolates grammar productions for:
  * Global functions (TAB extension)
  * Class declarations with fields and methods
  * Main block execution statements

### 2.2 Abstract Syntax Tree (AST)
AST node hierarchies are located in the package `fr.ensimag.deca.tree`:
* **`Tree`**: Abstract base node tracking file location (`Location`) for precise error reporting.
* **`AbstractProgram` / `Program`**: Encapsulates `ListDeclFunction`, `ListDeclClass`, and `AbstractMain`.
* **`AbstractInst`**: Statements including `Assign`, `IfThenElse`, `While`, `Return`, `Print`, and method calls.
* **`AbstractExpr`**: Expressions evaluated for value, type, and side effects.
* **`AbstractOpArith` / `AbstractOpCmp` / `AbstractOpBool`**: Binary operation hierarchies implementing common evaluation algorithms.

---

## 3. Pass B: Contextual & Semantic Analysis

Semantic analysis ensures type safety, scope validity, and adherence to Deca object-oriented semantics. To support mutual recursion and out-of-order declarations, Pass B is executed in **three distinct passes**:

```text
+--------------------------------------------------------------------------+
|                       Pass B: 3-Phase Semantic Analysis                  |
+--------------------------------------------------------------------------+
| Pass 1 (Classes):                                                        |
|   - Register class names in EnvironmentType                              |
|   - Verify single inheritance and detect circular inheritance cycles     |
|   - Link predefined 'Object' root class                                  |
+--------------------------------------------------------------------------+
| Pass 2 (Members):                                                        |
|   - Register fields and method signatures in class environments          |
|   - Check method overriding rules: identical signature and return type   |
|   - Check field hiding and type consistency                              |
+--------------------------------------------------------------------------+
| Pass 3 (Bodies & Instructions):                                          |
|   - Type-check expressions, variable initializations, and method bodies  |
|   - Validate visibility rules (protected fields)                         |
|   - Decorate every AST node with Type and Definition references          |
+--------------------------------------------------------------------------+
```

### 3.1 Environments and Symbol Tables
* **`SymbolTable`**: Efficient hash-mapped table that maps identifier names to unique `Symbol` instances to accelerate lookups.
* **`EnvironmentExp`**: Lexically scoped environment maintaining mappings from `Symbol` to `ExpDefinition` (`VariableDefinition`, `FieldDefinition`, `ParamDefinition`, `MethodDefinition`).
* **`EnvironmentType`**: Global type repository pre-populated with primitive types (`int`, `float`, `boolean`, `void`, `null`) and user-defined `ClassType` objects.

---

## 4. Pass C: IMA Target Code Generation

The backend emits assembly instructions for the IMA (Machine Abstraite) processor, a 32-bit register-based virtual architecture with registers `R0` through `R15`.

### 4.1 Memory Architecture & Layout

The generated assembly coordinates memory across four primary memory zones:

```text
Low Memory (0)
+-------------------------------------------------------+
|  Static Data Area (GB - Global Base Pointer)          |
|  - Virtual Method Tables (VTable for Object & Classes)|
|  - Global variable allocations                        |
+-------------------------------------------------------+
|  Heap Memory (Grows Downward via HEAP)                |
|  - Allocated dynamically using 'NEW' and 'ALLOC'      |
|  - Stores class object instances and 2D matrix arrays |
+-------------------------------------------------------+
|  Stack Memory (LB - Local Base Pointer / SP)          |
|  - Function and method stack frames                   |
|  - Return addresses, saved registers (TSTO/ADDSP)     |
|  - Local variables and spilled temporary evaluations  |
+-------------------------------------------------------+
High Memory
```

### 4.2 Register Allocation (`RegisterManager`)
* Handled by [`RegisterManager.java`](file:///home/ensimag/GL_FINAL/gl10/src/main/java/fr/ensimag/deca/RegisterManager.java).
* Registers `R0` and `R1` are reserved for scratch arithmetic results, return values, and I/O.
* Registers `R2` through `Rmax` (default `R15`, configurable down to `R4` via CLI option `-r X`) are allocated dynamically for expression evaluation.
* When register pressure exceeds available registers, the compiler generates stack spill instructions (`PUSH` / `POP`) to preserve intermediate values without clobbering.

### 4.3 Virtual Method Tables (VTables) & Dynamic Dispatch
For every class declared in the Deca source code:
1. The compiler constructs a method table in the static data area during initialization.
2. The first word in the table contains a pointer to the superclass VTable.
3. Subsequent slots hold code labels (`Label`) for method implementations (`code.ClassName.methodName`).
4. Overridden methods replace their superclass slot, preserving the method index across the inheritance hierarchy.
5. Method calls (`obj.method()`) perform late binding:
   * Load the object address (`LOAD ... , R2`).
   * Dereference word 0 to access the VTable address (`LOAD 0(R2), R1`).
   * Branch to the indexed slot (`BSR index(R1)`).

---

## 5. TAB Extension: Multi-Dimensional Arrays & Matrices

The TAB extension augments Deca with native support for matrices and high-performance linear algebra operations:

### 5.1 Memory Representation of 2D Arrays
A 2D array allocated via `new int[n][m]` is laid out in the heap as:
* **Header word 0**: Number of rows (`n`).
* **Header word 1**: Number of columns (`m`).
* **Contiguous data block**: `n * m` consecutive words stored in row-major order.
* **Element index formula**: `offset = 2 + (row * m + col)`.

### 5.2 Primitive AST Nodes
* `ArrayNew` / `Array2DNew`: Emits heap allocation (`ALLOC`) and initializes dimensions and elements to default values.
* `GetElem` / `SetElem`: Emits bounds computation and reads/writes directly from heap memory using indexed register offsets.
* `GetNLines` / `GetNCols`: Reads dimension metadata stored at offsets 0 and 1.
