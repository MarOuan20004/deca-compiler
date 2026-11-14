---
name: Bug Report
about: Create a report to help us improve the Deca compiler
title: '[BUG] '
labels: bug
assignees: ''
---

## Description
A clear and concise description of what the bug is.

## Compilation Pass / Subsystem
Please indicate which compiler phase is affected:
- [ ] Command Line Options / CLI Driver
- [ ] Pass A: Lexical & Syntactic Analysis (ANTLR4)
- [ ] Pass B: Contextual Verification & Type Checking
- [ ] Pass C: Code Generation (IMA Assembly)
- [ ] Standard Library / TAB Extension (Matrices)

## Reproducible Deca Snippet
Paste a minimal Deca code snippet that reproduces the issue:

```deca
// Minimal reproducible example
{
    // code here
}
```

## Command Executed
```bash
./bin/decac [options] file.deca
```

## Expected Behavior
A clear and concise description of what you expected to happen.

## Actual Output / Error Message
```text
// Paste exact compiler error message or stack trace here
```

## Environment
- OS: Linux / macOS / Windows
- Java Version: (`java -version`)
- Maven Version: (`mvn -version`)
- Branch / Commit:
