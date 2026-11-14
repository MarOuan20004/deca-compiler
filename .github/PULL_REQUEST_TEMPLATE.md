## Pull Request Summary

### Description
Provide a clear description of the modifications made in this pull request and their underlying rationale.

### Type of Change
- [ ] Bug fix (non-breaking change fixing an issue)
- [ ] New feature (non-breaking enhancement adding functionality)
- [ ] Breaking change (fix or feature modifying compiler behavior or grammar)
- [ ] Documentation update
- [ ] Performance / Code Refactoring

### Compilation Phases Impacted
- [ ] Frontend: Lexical & Syntax Analysis (`syntax/`)
- [ ] Middle-end: Contextual Verification (`context/`, `tree/`)
- [ ] Backend: IMA Code Generation & Register Allocation (`codegen/`, `ima/`)
- [ ] Extensions: TAB / Standard Library (`Math.decah`)
- [ ] Build & Test Harness (`pom.xml`, test scripts)

### Verification & Testing
- [ ] `mvn compile` executes without error
- [ ] `./src/test/script/codegen-valid-provided.sh` passes
- [ ] `mvn test` passes cleanly
- [ ] New test cases added to `src/test/deca/` for the introduced functionality

### Checklist
- [ ] My code adheres to the project's style guidelines
- [ ] Comments and docstrings are added/maintained
- [ ] Documentation and user manuals updated if applicable
- [ ] No regression introduced in existing test suites
