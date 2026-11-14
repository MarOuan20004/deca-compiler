# Deca Language Examples

This directory provides a curated collection of Deca programs demonstrating the syntax, features, and extensions supported by the **decac** compiler.

## Example Index

| File | Category | Key Language Features Demonstrated |
| :--- | :--- | :--- |
| [`deca/01_hello_world.deca`](file:///home/ensimag/GL_FINAL/gl10/examples/deca/01_hello_world.deca) | Basics | Standard output (`print`, `println`), string literals |
| [`deca/02_arithmetic_control.deca`](file:///home/ensimag/GL_FINAL/gl10/examples/deca/02_arithmetic_control.deca) | Control Flow | Types (`int`, `float`, `boolean`), `while` loops, `if-else`, hexadecimal floating-point print (`printlnx`) |
| [`deca/03_classes_and_objects.deca`](file:///home/ensimag/GL_FINAL/gl10/examples/deca/03_classes_and_objects.deca) | OOP Basics | Class definitions, protected fields, method invocations, `this` reference, `new` instantiation |
| [`deca/04_polymorphism_and_cast.deca`](file:///home/ensimag/GL_FINAL/gl10/examples/deca/04_polymorphism_and_cast.deca) | Advanced OOP | Single inheritance (`extends`), method overriding, virtual method table (VTable), dynamic dispatch, `instanceof`, explicit casting |
| [`deca/05_matrices_tab.deca`](file:///home/ensimag/GL_FINAL/gl10/examples/deca/05_matrices_tab.deca) | TAB Extension | 2D array allocation, `_unite`, `getElem`, matrix printing |
| [`deca/06_matrix_suite.deca`](file:///home/ensimag/GL_FINAL/gl10/examples/deca/06_matrix_suite.deca) | TAB Extension | Comprehensive validation suite exercising 15 matrix operations |
| [`deca/07_math_library.deca`](file:///home/ensimag/GL_FINAL/gl10/examples/deca/07_math_library.deca) | Standard Library | `#include "Math.decah"`, `sqrt`, `abs`, `ulp`, matrix determinant |

## Compiling and Running Examples

To compile any example to IMA assembly and execute it:

```bash
# Compile to .ass
./bin/decac examples/deca/01_hello_world.deca

# Execute on the IMA virtual machine
ima examples/deca/01_hello_world.ass
```
